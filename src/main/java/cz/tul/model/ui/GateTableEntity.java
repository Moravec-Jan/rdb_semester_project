package cz.tul.model.ui;

import cz.tul.model.generic.GatePassageProjection;

import java.util.ArrayList;
import java.util.List;

public class GateTableEntity {
    private String crp;
    private String jmeno;
    private String cas;
    private String spz;
    private String vyrobce;
    private String typ;


    private GateTableEntity(String crp, String jmeno, String cas, String spz, String vyrobce, String typ) {
        this.crp = crp;
        this.jmeno = jmeno;
        this.cas = cas;
        this.spz = spz;
        this.vyrobce = vyrobce;
        this.typ = typ;
    }

    public String getCrp() {
        return crp;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getCas() {
        return cas;
    }

    public String getSpz() {
        return spz;
    }

    public String getVyrobce() {
        return vyrobce;
    }

    public String getTyp() {
        return typ;
    }

    public static List<GateTableEntity> createFromProjeti(List<GatePassageProjection> projeti) {
        List<GateTableEntity> entities = new ArrayList<>();

        projeti.forEach(value -> entities.add(new GateTableEntity(value.getRidic().getCrp(), value.getRidic().getJmeno(), value.getCas().toString(),value.getAuto().getSpz(),value.getAuto().getVyrobce(),value.getAuto().getTyp())));
        return entities;
    }
}
