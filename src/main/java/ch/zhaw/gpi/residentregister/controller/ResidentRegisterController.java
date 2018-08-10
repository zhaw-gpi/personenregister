package ch.zhaw.gpi.residentregister.controller;

import ch.ech.xmlns.ech_0044._4.DatePartiallyKnownType;
import ch.ech.xmlns.ech_0044._4.PersonIdentificationType;
import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0194._1.DeliveryType;
import ch.ech.xmlns.ech_0194._1.InfoType;
import ch.ech.xmlns.ech_0194._1.NegativeReportType;
import ch.ech.xmlns.ech_0194._1.PersonMoveResponse;
import ch.zhaw.gpi.residentregister.entities.ResidentEntity;
import ch.zhaw.gpi.residentregister.helpers.DateConversionHelper;
import ch.zhaw.gpi.residentregister.helpers.DefaultHeaderHelper;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation für den PersonenRegisterService
 * Diese Klasse enthält die eigentliche Implementation der Web Service-Operationen,
 * wobei jede Funktion einer gleich lautenden Web Service-Operation entspricht.
 * Sie stellt die Verbindung zwischen Web Service-Schnittstelle und Process Engine her
 */
public class ResidentRegisterController {
    
    /**
     * Unser Web Service nutzt die Camunda Process Engine als Abhängigkeit
     * Über @Autowired wird die zur Laufzeit verfügbare Process Engine-Instanz
     * in diesem Service als Variable processEngine eingefügt (Dependency Injection)
     */ 
    @Autowired
    private ProcessEngine processEngine;
    
    /**
     * Implementation der Web Service-Operation handleDelivery
     * Prüft, ob wirklich eine personMoveRequest-Nachricht übergeben wird. Falls
     * nein, wird ein negativeReport zurückgegeben. Falls ja, werden aus dem erhaltenen
     * DeliveryType-Objekt die relevanten Eigenschaften in Prozessvariablen übersetzt.
     * Daraufhin startet eine neue Instanz des Prozesses PersonIdentificationProcess
     * mit diesen Prozessvariablen. Die Variablen am Ende des Prozesses werden
     * ausgelesen und den entsprechenden Eigenschaften des PersonMoveResponse-Objekts
     * zugewiesen. Dieses wird schliesslich gewrapped in einem DeliveryType-Objekt
     * an den Webservice-Endpoint zurück gegeben.
     * 
     * @param deliveryRequest
     * @return 
     */
    public DeliveryType handleDelivery(DeliveryType deliveryRequest){
        // Header des Anfrage-Objekts in neue Variable auslesen
        HeaderType headerRequest = deliveryRequest.getDeliveryHeader();
        
        // Antwort-Objekt initialisieren
        DeliveryType deliveryResponse = new DeliveryType();
        
        // Header des Antwort-Objekts mit Default-Werten basierend auf Hilfsklasse generieren
        HeaderType headerResponse = new DefaultHeaderHelper(headerRequest).getHeaderResponse();
        
        // Nur wenn der Nachrichten-Typ der Anfrage einem personMoveRequest entspricht
        // soll es weiter gehen, ansonsten soll ein negativeReport zurück gesendet werden
        if(headerRequest.getMessageType().equals("sedex://personMoveRequest")){
            // PersonIdentification des Anfrage-Objekts in neue Variable auslesen
            PersonIdentificationType personIdentification = deliveryRequest.getPersonMoveRequest().getPersonIdentification();
        
            // Map vorbereiten für die Übergabe an die Prozessinstanz als Prozessvariablen
            Map<String, Object> processStartVariables = new HashMap<>();
            
            // Vorname aus PersonIdentification an Prozessvariable übergeben
            processStartVariables.put("firstName", personIdentification.getFirstName());
            
            // Nachname aus PersonIdentification an Prozessvariable übergeben
            processStartVariables.put("officialName", personIdentification.getOfficialName());
            
            // Geschlecht aus PersonIdentification an Prozessvariable übergeben
            processStartVariables.put("sex", Integer.parseInt(personIdentification.getSex()));
            
            // Geburtsdatum aus PersonIdentification in ein Date-Objekt umwandeln
            Date dateOfBirth = personIdentification.getDateOfBirth().getYearMonthDay().toGregorianCalendar().getTime();
            
            // Umgewandeltes Geburtsdatum aus PersonIdentification an Prozessvariable übergeben
            processStartVariables.put("dateOfBirth", dateOfBirth);

            // Eine neue Instanz des Prozesses mit der Id "PersonIdentificationProcess" wird gestartet
            // Die vorbereiteten Variablen werden übergeben
            // Als Antwort kommen die zuletzt bekannten Prozessvariablen zurück
            // Da der Prozess hierbei vollständig durchgelaufen ist, kommen also die
            // Variablen zurück, wie sie beim Endereignis eingetroffen sind
            ProcessInstanceWithVariables processInstanceWithVariables = processEngine.getRuntimeService()
                    .createProcessInstanceByKey("PersonIdentificationProcess")
                    .setVariables(processStartVariables)
                    .executeWithVariablesInReturn();

            // Prozess-Variablen am Prozessende an ein Map-Objekt übergeben
            Map<String, Object> processEndVariables = processInstanceWithVariables.getVariables();

            // Ein neues Antwort-Teil-Objekt wird instanziert
            PersonMoveResponse personMoveResponse = new PersonMoveResponse();

            // Dem Antwort-Teil-Objekt wird die in der Anfrage erhaltene Personenidentifikation übergeben
            personMoveResponse.setPersonIdentification(personIdentification);
            
            // Dem Antwort-Teil-Objekt wird die in der Anfrage erhaltene Gemeindeidentifikation übergeben
            personMoveResponse.setReportingMunicipality(deliveryRequest.getPersonMoveRequest().getMunicipality());
            
            // Prozessvariable, ob Person gefunden wurde, wird der entsprechenden Antwort-Teil-Objekt-Eigenschaft zugewiesen 
            personMoveResponse.setPersonKnown((Boolean) processEndVariables.get("personKnown"));
            
            // Prozessvariable, ob Person für elektronische Umzugsmeldung berechtigt ist, wird einer neuen Boolean-Variable zugewiesen
            Boolean moveAllowedBoolean = (Boolean) processEndVariables.get("moveAllowed");
            
            // Nur wenn diese Prozessvariable gesetzt wurde, soll sie auch im Antwort-Objekt mit aufgeführt werden
            if(moveAllowedBoolean != null){
                // Die Boolean-Variable muss in eine Integer-Variable umgewandelt werden
                BigInteger moveAllowedInteger = (moveAllowedBoolean ? BigInteger.valueOf(1) : BigInteger.valueOf(2));

                // Die Integer-Variable kann dem Antwort-Objekt übergeben werden
                personMoveResponse.setMoveAllowed(moveAllowedInteger);
                
                //Prozessvariable personRelatives wird einer List zugewiesen. Die Prozessvariable beinhaltet alle mitzuziehende Personen. 
                List<ResidentEntity> relatives = (List<ResidentEntity>)processEndVariables.get("personRelatives");
                //Es wird überprüft ob die Liste der mitzuziehenden Personen nicht null und nicht leer ist.
                if(relatives != null && !relatives.isEmpty()) {
                    //Es wird über alle mitzuziehende Person iteriert und jedes Element der Liste wird einer Variable vom Typ ResidentEntity zugewiesen. 
                    for (ResidentEntity resident : relatives) {
                        // Nur Personen, welche umziehen dürfen, werden der Liste hinzugefügt
                        if (resident.isMoveAllowed()) {
                            //Related Person wird instanziert. RelatedPerson entspricht letztendlich einer mitzuziehenden Person.
                            PersonMoveResponse.RelatedPerson relatedPerson = new PersonMoveResponse.RelatedPerson();
                            //PersonIdentificationType wird für die mitzuziehenden Person instanziert.
                            PersonIdentificationType relatedPersonIdentification = new PersonIdentificationType();
                            //Vorhandene LocalPersonID der Hauptperson wird für jede mitzuziehende Person zugewiesen.
                            relatedPersonIdentification.setLocalPersonId(personIdentification.getLocalPersonId());
                            //Vorname der mitzuziehenden Person wird gesetzt.
                            relatedPersonIdentification.setFirstName(resident.getFirstName());
                            //Nachname der mitzuziehenden Person wird gesetzt.
                            relatedPersonIdentification.setOfficialName(resident.getOfficialName());
                            //DatePartiallyKnownType wird instanziert. Wird gebraucht um Geburtstagsdatum der mitzuziehenden Person zu setzen.
                            DatePartiallyKnownType relatedPersonDateOfBirth = new DatePartiallyKnownType();
                            //Das Geburtstagsdatum wird im Format YearMonthDay gesetzt. Es wird die Methode dateToXMLGregorianCalendar in der Klasse DateConversionHelper aufgerufen, 
                            //um das Datum in ein XMLGregorianCalendar umzuwandeln.
                            relatedPersonDateOfBirth.setYearMonthDay(DateConversionHelper.DateToXMLGregorianCalendar(resident.getDateOfBirth()));
                            //Geburtstagsdatum der mitzuziehenden Person wird gesetzt.
                            relatedPersonIdentification.setDateOfBirth(relatedPersonDateOfBirth);
                            //Geschlecht der mitzuziehenden Person wird gesetzt. Da ein String erwartet wird, wird Variable sex von int in ein String umgewandelt.
                            relatedPersonIdentification.setSex(Integer.toString(resident.getSex()));
                            //Die Personen Identifikation wird der mitzuziehenden Person gesetzt.
                            relatedPerson.setPersonIdentification(relatedPersonIdentification);
                            // Die mitzuziehende Personen werden dem Antwort-Objekt übergeben, indem sie der relatedPerson Liste
                            // hinzugefügt werden.
                            personMoveResponse.getRelatedPerson().add(relatedPerson);
                        }
                    }
                }
            }        
            
            // Das Antwort-Teil-Objekt wird dem Antwort-Objekt zugewiesen
            deliveryResponse.setPersonMoveResponse(personMoveResponse);

            // Im Header der Antwort wird die Aktion auf 9 (=Positive Antwort) gesetzt
            headerResponse.setAction("9");
            
            // Im Header der Antwort wird der Nachrichtentyp auf personMoveResponse gesetzt
            headerResponse.setMessageType("sedex://personMoveResponse");
            
            // Der Header wird der Antwort zugewiesen
            deliveryResponse.setDeliveryHeader(headerResponse);
        } else {
            // Ein neues InfoType-Objekt wird initialisiert
            InfoType info = new InfoType();
            
            // Diesem Objekt wird der Fehler-Code 1 (Abgelehnt) zugewiesen
            info.setCode(BigInteger.valueOf(1));
            
            // Diesem Objekt wird eine Fehlermeldung zugewiesen, dass der falsche Nachrichtentyp übergeben wurde
            info.setTextGerman("MessageType sedex://personMoveRequest erwartet, aber " 
                    + headerRequest.getMessageType() + " erhalten.");
            
            // Ein neues NegativeReportType-Objekt wird initialisiert
            NegativeReportType negativeReport = new NegativeReportType();
            
            // Diesem Objekt wird das InfoType-Objekt als GeneralError-Objekt zugewiesen
            negativeReport.getGeneralError().add(info);
            
            // Das Objekt wird dem Antwort-Objekt zugewiesen
            deliveryResponse.setNegativeReport(negativeReport);
            
            // Im Header der Antwort wird die Aktion auf 8 (=Negative Antwort) gesetzt
            headerResponse.setAction("8");
            
            // Im Header der Antwort wird der Nachrichtentyp auf NegativeReport gesetzt
            headerResponse.setMessageType("sedex://negativeReport");
            
            // Der Header wird der Antwort zugewiesen
            deliveryResponse.setDeliveryHeader(headerResponse);
        }
        
        // Das Antwort-Objekt wird an die Webservice-Schnittstelle zurück gegeben
        return deliveryResponse;
    }
}
