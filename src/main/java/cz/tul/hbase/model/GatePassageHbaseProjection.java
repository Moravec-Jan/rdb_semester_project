package cz.tul.hbase.model;

import cz.tul.model.generic.GatePassageProjection;

import java.sql.Timestamp;

public class GatePassageHbaseProjection implements GatePassageProjection {
    Timestamp cas;
    String spz;
    String vyrobce;
    String typ;
    String crp;
    String jmeno;

    public GatePassageHbaseProjection(Timestamp cas, String spz, String vyrobce, String typ, String crp, String jmeno) {
        this.cas = cas;
        this.spz = spz;
        this.vyrobce = vyrobce;
        this.typ = typ;
        this.crp = crp;
        this.jmeno = jmeno;
    }

    @Override
    public Timestamp getCas() {
        return cas;
    }

    @Override
    public RidicCrpJmenoOnly getRidic() {
        return new RidicCrpJmenoOnly() {
            @Override
            public String getCrp() {
                return crp;
            }

            @Override
            public String getJmeno() {
                return jmeno;
            }
        };
    }

    @Override
    public AutoSpzVyrobceTypOnly getAuto() {
        return new AutoSpzVyrobceTypOnly() {
            @Override
            public String getSpz() {
                return spz;
            }

            @Override
            public String getVyrobce() {
                return vyrobce;
            }

            @Override
            public String getTyp() {
                return typ;
            }
        };
    }
}
