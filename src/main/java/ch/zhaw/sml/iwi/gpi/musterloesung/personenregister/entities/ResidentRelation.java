/**
 * This file was generated by the Jeddict
 */
package ch.zhaw.sml.iwi.gpi.musterloesung.personenregister.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Bekim
 */
@Entity
@Table(name = "RESIDENT_RELATION")
public class ResidentRelation implements Serializable {

    @Column(name = "ID", unique = true, nullable = false)
    @Id
    private String id;

    @OneToMany(targetEntity = Resident.class, mappedBy = "residentRelation")
    private List<Resident> residents;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Resident> getResidents() {
        if (residents == null) {
            residents = new ArrayList<>();
        }
        return this.residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

    public void addResident(Resident resident) {
        getResidents().add(resident);
        resident.setResidentRelation(this);
    }

    public void removeResident(Resident resident) {
        getResidents().remove(resident);
        resident.setResidentRelation(null);
    }

}