package cz.tul.model.mongo;

import cz.tul.model.mysql.*;
import cz.tul.model.generic.Projeti;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.sql.Timestamp;

@Document(collection = Projeti.TABLE_NAME)
public class ProjetiMongo implements Projeti{

    @Id
    ProjetiMongoId id;


    private Brana brana;
    private Auto auto;
    private Ridic ridic;
    private int najeto;
    private int benzin;
    private float napeti;
    private Timestamp cas;

    public ProjetiMongo() {
    }

    public ProjetiMongo(ProjetiMongoId id, Brana brana, Auto auto, Ridic ridic, int najeto, int benzin, float napeti) {
        this.id = id;
        this.brana = brana;
        this.auto = auto;
        this.ridic = ridic;
        this.najeto = najeto;
        this.benzin = benzin;
        this.napeti = napeti;
    }

    public ProjetiMongo(Projeti projetiItem) {
        this.id = new ProjetiMongoId(convertTimestampToDate(projetiItem.getCas()),projetiItem.getRidic().getCrp());
        this.brana = projetiItem.getBrana();
        this.auto = projetiItem.getAuto();
        this.ridic = projetiItem.getRidic();
        this.najeto = projetiItem.getNajeto();
        this.benzin = projetiItem.getBenzin();
        this.napeti = projetiItem.getNapeti();
    }

    public Ridic getRidic() {
        return ridic;
    }

    public ProjetiMongoId getId() {
        return id;
    }

    public Brana getBrana() {
        return brana;
    }

    public Auto getAuto() {
        return auto;
    }

    @Override
    public Timestamp getCas() {
        return convertDateToTimestamp(id.getCas());
    }

    public int getNajeto() {
        return najeto;
    }

    public int getBenzin() {
        return benzin;
    }

    public float getNapeti() {
        return napeti;
    }

    public Timestamp convertDateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Date convertTimestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
}
