package cz.tul.model.mysql;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Profile("mysql")
@Entity
@Table(name = Auto.TABLE_NAME)
public class Auto implements Persistable<String> {

    static final String TABLE_NAME = "Auto";

    @Id
    private String spz;
    private int barva;
    private String vyrobce;
    private String typ;

    // support variable for determining if variable is meant for create or update
    @Transient
    private boolean update;

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

    @Override
    public String getId() {
        return getSpz();
    }

    @Override
    public boolean isNew() {
        return !update;
    }
}
