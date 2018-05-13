package cz.tul.mysql.model;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Profile("mysql")
@Entity
@Table(name = Ridic.TABLE_NAME)
public class Ridic implements Persistable<String> {

    static final String TABLE_NAME = "Ridic";
    @Id
    private String crp;
    private String jmeno;

    // support variable for determining if variable is meant for create or update
    @Transient
    private boolean update;


    public Ridic() {
    }

    public Ridic(String crp, String jmeno) {
        this.crp = crp;
        this.jmeno = jmeno;
    }

    public String getCrp() {
        return crp;
    }

    public String getJmeno() {
        return jmeno;
    }

    @Override
    public String getId() {
        return crp;
    }

    @Override
    public boolean isNew() {
        return !this.update;
    }
}
