package cz.tul.model;

import javax.persistence.*;
import java.io.Serializable;
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
    private Ridic crp_ridic;

    @ManyToOne
    @JoinColumn(name = "id_brana")
    private Brana id_brana;

    @ManyToOne
    @JoinColumn(name = "spz_auto")
    private Auto spz_auto;

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

    public Projeti(Timestamp cas, int najeto, int benzin, float napeti, Ridic crp_ridic, Brana id_brana, Auto spz_auto) {
        this.cas = cas;
        this.najeto = najeto;
        this.benzin = benzin;
        this.napeti = napeti;
        this.crp_ridic = crp_ridic;
        this.id_brana = id_brana;
        this.spz_auto = spz_auto;
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

    public Ridic getCrp_ridic() {
        return crp_ridic;
    }

    public Brana getId_brana() {
        return id_brana;
    }

    public Auto getSpz_auto() {
        return spz_auto;
    }
}
