package cz.tul.model.db;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@IdClass(ProjetiId.class)
@Table(name = Projeti.TABLE_NAME)
public class Projeti {
    static final String TABLE_NAME = "Projeti";

    @Id
    private Timestamp cas;
    private int najeto;
    private int benzin;
    private float napeti;

    @Id
    @ManyToOne
    @JoinColumn(name = "crp_ridic")
    private Ridic ridic;

    @ManyToOne
    @JoinColumn(name = "id_brana")
    private Brana brana;

    @ManyToOne
    @JoinColumn(name = "spz_auto")
    private Auto auto;

//    @Override
//    public boolean equals(Object obj) {
//        return super.equals(obj);
//    }
//
//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }

    public Projeti() {
    }

    public Projeti(Timestamp cas, int najeto, int benzin, float napeti, Ridic ridic, Brana id_brana, Auto spz_auto) {
        this.cas = cas;
        this.najeto = najeto;
        this.benzin = benzin;
        this.napeti = napeti;
        this.ridic = ridic;
        this.brana = id_brana;
        this.auto = spz_auto;
    }

    public Timestamp getCas() {
        return cas;
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

    public Ridic getRidic() {
        return ridic;
    }

    public Brana getBrana() {
        return brana;
    }

    public Auto getAuto() {
        return auto;
    }
}