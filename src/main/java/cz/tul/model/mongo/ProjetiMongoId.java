package cz.tul.model.mongo;

import java.util.Date;

public class ProjetiMongoId {
    private Date cas;
    private String ridic;

    public ProjetiMongoId(Date cas, String ridic) {
        this.cas = cas;
        this.ridic = ridic;
    }

    public Date getCas() {
        return cas;
    }

    public String getRidic() {
        return ridic;
    }

}
