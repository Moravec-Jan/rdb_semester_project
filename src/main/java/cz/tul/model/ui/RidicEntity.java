package cz.tul.model.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RidicEntity {

    @Column(name = "najetoKm")
    int km;

    @Id
    String crp;
    String jmeno;

    public RidicEntity() {
    }

    public int getKm() {
        return km;
    }

    public String getCrp() {
        return crp;
    }

    public String getJmeno() {
        return jmeno;
    }
}
