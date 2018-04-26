package cz.tul.model.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Auto.TABLE_NAME)
public class Auto {

    static final String TABLE_NAME = "Auto";
    @Id
    private String spz;
    private int barva;
    private String vyrobce;
    private String typ;

    public Auto() {
    }

    public Auto(String spz, int barva, String vyrobce, String typ) {
        this.spz = spz;
        this.barva = barva;
        this.vyrobce = vyrobce;
        this.typ = typ;
    }

    public String getSpz() {
        return spz;
    }

    public int getBarva() {
        return barva;
    }

    public String getVyrobce() {
        return vyrobce;
    }

    public String getTyp() {
        return typ;
    }
}
