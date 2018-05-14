package cz.tul.model.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DriverEntity {

    @Column(name = "najetoKm")
    int km;

    @Id
    String crp;
    String jmeno;

    public DriverEntity() {
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

    public DriverEntity(int km, String crp, String jmeno) {
        this.km = km;
        this.crp = crp;
        this.jmeno = jmeno;
    }
}
