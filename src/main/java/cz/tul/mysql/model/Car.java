package cz.tul.mysql.model;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Profile("mysql")
@Entity
@Table(name = Car.TABLE_NAME)
public class Car implements Persistable<String> {

    static final String TABLE_NAME = "Auto";

    @Id
    private String spz;
    private int barva;
    private String vyrobce;
    private String typ;

    // support variable for determining if variable is meant for create or update
    @Transient
    private boolean update;

    public Car() {
    }

    public Car(String spz, int barva, String vyrobce, String typ) {
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

    @Override
    public String getId() {
        return getSpz();
    }

    @Override
    public boolean isNew() {
        return !update;
    }
}
