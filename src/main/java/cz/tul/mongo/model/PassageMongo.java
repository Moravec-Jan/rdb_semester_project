package cz.tul.mongo.model;

import cz.tul.model.generic.Passage;
import cz.tul.mysql.model.Car;
import cz.tul.mysql.model.Gate;
import cz.tul.mysql.model.Driver;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.sql.Timestamp;

@Document(collection = Passage.TABLE_NAME)
public class PassageMongo implements Passage {

    @Id
    PassageMongoId id;


    private Gate brana;
    private Car auto;
    private Driver ridic;
    private int najeto;
    private int benzin;
    private float napeti;
    private Timestamp cas;

    public PassageMongo() {
    }

    public PassageMongo(PassageMongoId id, Gate brana, Car auto, Driver ridic, int najeto, int benzin, float napeti) {
        this.id = id;
        this.brana = brana;
        this.auto = auto;
        this.ridic = ridic;
        this.najeto = najeto;
        this.benzin = benzin;
        this.napeti = napeti;
    }

    public PassageMongo(Passage projetiItem) {
        this.id = new PassageMongoId(convertTimestampToDate(projetiItem.getCas()),projetiItem.getRidic().getCrp());
        this.brana = projetiItem.getBrana();
        this.auto = projetiItem.getAuto();
        this.ridic = projetiItem.getRidic();
        this.najeto = projetiItem.getNajeto();
        this.benzin = projetiItem.getBenzin();
        this.napeti = projetiItem.getNapeti();
    }

    public Driver getRidic() {
        return ridic;
    }

    public PassageMongoId getId() {
        return id;
    }

    public Gate getBrana() {
        return brana;
    }

    public Car getAuto() {
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


    public void setId(PassageMongoId id) {
        this.id = id;
    }

    public void setBrana(Gate brana) {
        this.brana = brana;
    }

    public void setAuto(Car auto) {
        this.auto = auto;
    }

    public void setRidic(Driver ridic) {
        this.ridic = ridic;
    }

    public void setNajeto(int najeto) {
        this.najeto = najeto;
    }

    public void setBenzin(int benzin) {
        this.benzin = benzin;
    }

    public void setNapeti(float napeti) {
        this.napeti = napeti;
    }

    public void setCas(Timestamp cas) {
        this.cas = cas;
    }
}
