package cz.tul.model.mysql;


import cz.tul.model.generic.InvalidRecords;
import org.springframework.context.annotation.Profile;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Profile("mysql")
@Entity
@Table(name = InvalidRecords.TABLE_NAME)
public class InvalidMySqlRecords implements InvalidRecords {

    @Id
    private int id;
    private int pocet;

    public InvalidMySqlRecords() {
    }

    public InvalidMySqlRecords(int id, int pocet) {
        this.id = id;
        this.pocet = pocet;
    }

    public int getId() {
        return id;
    }

    public int getPocet() {
        return pocet;
    }
}
