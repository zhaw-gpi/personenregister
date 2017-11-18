package ch.zhaw.sml.iwi.gpi.musterloesung.personenregister.repository;

import ch.zhaw.sml.iwi.gpi.musterloesung.personenregister.entities.Resident;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Repository mit CRUD- und weitern Operationen für Bürger-Objekte (Resident)
 * Erweitert die mit dem Spring Data JPA-Paket mitgelieferte CrudRepository, so
 * dass einerseits einfache CRUD-Operationen mit der Resident-Entität durchgeführt
 * werden können als auch eigene definierte Methoden
 */
public interface ResidentRepository extends CrudRepository<Resident, Long> {
    
    /**
     * Methode, um alle Personen mit bestimmten Identifikationsmerkmalen zu erhalten
     * Funktioniert basierend auf einer in JPQL (Java Persistence Query Language)
     * geschriebenen Abfrage
     * @param firstName
     * @param officialName
     * @param sex
     * @param dateOfBirth
     * @return 
     */
    @Query("SELECT r FROM Resident r WHERE r.firstName = :firstName AND " +
            "r.officialName = :officialName AND r.sex = :sex AND " +
            "r.dateOfBirth = :dateOfBirth")
    public List<Resident> findResidentByIdentificationParameters(
            @Param("firstName") String firstName,
            @Param("officialName") String officialName,
            @Param("sex") Integer sex,
            @Param("dateOfBirth") Date dateOfBirth
    );
}
