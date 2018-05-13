package cz.tul.mysql.model;

import org.springframework.context.annotation.Profile;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// valid gate longtitude is from 35 to 70 and latitude from -25 to 105
@Profile("mysql")
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

    public Brana(String id, float cena, String typ) {
        this.id = id;
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

    public static boolean validate(float longtitude, float latitude) {
        return longtitude >= 35 && longtitude <= 70 && latitude >= -25 && longtitude <= 105;
    }
}
