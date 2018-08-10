package ch.zhaw.gpi.residentregister.delegates;

import ch.zhaw.gpi.residentregister.entities.ResidentEntity;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import ch.zhaw.gpi.residentregister.repository.ResidentRegisterRepository;

/**
 * Datenbankabfrage nach den Personenangaben
 * Es wird in der Personenregister-Datenbank eine Abfrage platziert, welche einer 
 * AND-Kombination aus allen erhaltenen Prozess-Variablen entspricht. Als Antwort 
 * kommen 0-n Treffer von der Datenbank zurück, welche in einem personsFound-List-Objekt 
 * gespeichert werden. Für jede gefundene Person ist auch eCH-0194:moveAllowed 
 * enthalten.
 */
@Named("queryPersonInRegisterAdapter")
public class GetResidentFromResidentRegisterDelegate implements JavaDelegate{
    
    // Das für die Datenbankabfragen zuständige Repository wird als Dependency injiziert
    @Autowired
    private ResidentRegisterRepository residentRepository;
    
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
        List<ResidentEntity> personsFound = residentRepository.findResidentByIdentificationParameters(
                (String) processVariables.get("firstName"), 
                (String) processVariables.get("officialName"),
                (Integer) processVariables.get("sex"),
                (Date) processVariables.get("dateOfBirth")
        );
        
        // Wenn genau eine Person gefunden wurde, sollen die mitzuziehnden Personen als Prozess-Variable gesetzt werden.
        if(personsFound.size() == 1) {
           //Da es ja nur ein Element in der Liste gibt, wird das erste Element (also Index 0) aus der Liste in eine Variable von Typ ResidentEntity zugewiesen.
           ResidentEntity resident = personsFound.get(0);
           // Die relatives dieser Person werden ermittelt
           List<ResidentEntity> relatives = resident.getRelativesOnly();
           //Prozessvariable personRelatives wird erstellt.
           delegateExecution.setVariable("personRelatives", relatives);
        }
        
        // Das personsFound-Objekt der gleich benannten neuen Prozess-Variable zuweisen
        delegateExecution.setVariable("personsFound", personsFound);
    }
}
