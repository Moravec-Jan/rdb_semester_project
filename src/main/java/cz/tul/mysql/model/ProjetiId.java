package cz.tul.mysql.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ProjetiId implements Serializable {
    Timestamp cas;
    String ridic;

    public ProjetiId(Timestamp cas, String ridic) {
        this.cas = cas;
        this.ridic = ridic;
    }

    public ProjetiId() {
    }

    public Timestamp getCas() {
        return cas;
    }

    public String getRidic() {
        return ridic;
    }
}