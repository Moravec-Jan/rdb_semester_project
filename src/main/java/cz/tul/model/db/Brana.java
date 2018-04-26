package cz.tul.model.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Brana.TABLE_NAME)
public class Brana {

    static final String TABLE_NAME = "Brana";
    @Id
    private String id;
    private float longtitude;
    private float latitude;
    private float cena;
    private String typ;

    public Brana() {
    }

    public Brana(String id, float longtitude, float latitude, float cena, String typ) {
        this.id = id;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.cena = cena;
        this.typ = typ;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public String getId() {
        return id;
    }

    public float getLongtitude() {
        return longtitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getCena() {
        return cena;
    }

    public String getTyp() {
        return typ;
    }
}
