package cz.tul.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Ridic.TABLE_NAME)
public class Ridic {

    static final String TABLE_NAME = "Ridic";
    @Id
    private String crp;
    private String jmeno;

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
}
