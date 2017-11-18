package ch.zhaw.sml.iwi.gpi.musterloesung.personenregister.helpers;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0058._5.SendingApplicationType;
import java.util.Date;

/**
 * Initialisiert ein HeaderType-Objekt mit sinnvollen Standard-Eigenschaften
 * Hierdurch können mehrfach verwendete Eigenschaften wie z.B. die Bezeichnung
 * dieser Applikation einmalig gesetzt werden.
 */
public class DefaultHeaderHelper {
    // Initialisieren des neuen HeaderType-Objekts
    private final HeaderType headerResponse = new HeaderType();
    
    // Initialiseiren des dem Konstruktor übergebenen HeaderType-Objekts aus dem SOAP-Request
    private final HeaderType headerRequest;
    
    // Konstruktor-Methode, welche den Header aus dem SOAP-Request als Parameter erwartet
    public DefaultHeaderHelper(HeaderType headerRequest) {
        // Der Parameter headerRequest wird der Objekteigenschaft headerRequest zugewiesen
        this.headerRequest = headerRequest;
        
        // Identifikation des Senders gemäss Sedex wird gesetzt (frei erfunden)
        headerResponse.setSenderId("sedex://personenregister");
        
        // Die im Request erhaltene Nachrichten-Id wird auch in der Response wieder übergeben
        headerResponse.setMessageId(headerRequest.getMessageId());
        
        // Ein neues Objekt mit Eigenschaften zur Personenregister-Applikation wird erstellt
        SendingApplicationType sendingApplication = new SendingApplicationType();
        
        // Diesem Objekt wird der Hersteller/Betreiber zugewiesen
        sendingApplication.setManufacturer("Kanton Bern");
        
        // Diesem Objekt wird die Applikations-Bezeichnung zugewiesen
        sendingApplication.setProduct("Personenregister-Applikation");
        
        // Diesem Objekt wird die Applikations-Version zugewiesen
        sendingApplication.setProductVersion("1.0");
        
        // Dieses Objekt wird dem Antwort-Header zugewiesen
        headerResponse.setSendingApplication(sendingApplication);
        
        // Ein neues Datumskonversation-Hilfsobjekt wird initialisiert
        DateConversionHelper dateConversionHelper = new DateConversionHelper();
        
        // Mithilfe dieses Hilfsobjekt wird das aktuelle Datum (inkl. Uhrzeit)
        // in ein XMLGregorianCalendar-Objekt umgewandelt und der Nachrichten-Datums-
        // Eigenschaft im Antwort-Header zugewiesen
        headerResponse.setMessageDate(dateConversionHelper.DateToXMLGregorianCalendar(new Date()));
        
        // Es wird angegeben, dass es sich um eine Testnachricht handelt (Entwicklungs-Modus)
        headerResponse.setTestDeliveryFlag(true);
    }

    // Getter-Methode, um den Antwort-Header mit den Default-Eigenschaften zu erhalten
    public HeaderType getHeaderResponse() {
        return headerResponse;
    }
}
