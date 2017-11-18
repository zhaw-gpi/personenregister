package ch.zhaw.sml.iwi.gpi.musterloesung.personenregister.delegates;

import ch.zhaw.sml.iwi.gpi.musterloesung.personenregister.entities.Resident;
import ch.zhaw.sml.iwi.gpi.musterloesung.personenregister.repository.ResidentRepository;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Datenbankabfrage nach den Personenangaben
 * Es wird in der Personenregister-Datenbank eine Abfrage platziert, welche einer 
 * AND-Kombination aus allen erhaltenen Prozess-Variablen entspricht. Als Antwort 
 * kommen 0-n Treffer von der Datenbank zurück, welche in einem personsFound-List-Objekt 
 * gespeichert werden. Für jede gefundene Person ist auch eCH-0194:moveAllowed 
 * enthalten.
 */
@Named("queryPersonInRegisterAdapter")
public class GetResidentFromRegisterDbDelegate implements JavaDelegate{
    
    // Das für die Datenbankabfragen zuständige Repository wird als Dependency injiziert
    @Autowired
    private ResidentRepository residentRepository;
    
    /**
     * Datenbankabfrage nach den Personenangaben
     * Siehe Klassen-JavaDoc
     * 
     * @param delegateExecution
     * @throws Exception 
     */
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        // Prozess-Variablen einem neuen Map-Objekt zuweisen
        Map<String, Object> processVariables = delegateExecution.getVariables();
        
        // Repository-Methode aufrufen, welche die eigentliche Datenbankabfrage basierend
        // auf den übergegebenen Prozessvariablen durchführt und ein personsFound-Objekt zurückgibt
        List<Resident> personsFound = residentRepository.findResidentByIdentificationParameters(
                (String) processVariables.get("firstName"), 
                (String) processVariables.get("officialName"),
                (Integer) processVariables.get("sex"),
                (Date) processVariables.get("dateOfBirth")
        );
        
        // Das personsFound-Objekt der gleich benannten neuen Prozess-Variable zuweisen
        delegateExecution.setVariable("personsFound", personsFound);
    }
}
