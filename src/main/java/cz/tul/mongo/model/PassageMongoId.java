package cz.tul.mongo.model;

import java.util.Date;

public class PassageMongoId {
    private Date cas;
    private String ridic;

    public PassageMongoId(Date cas, String ridic) {
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
