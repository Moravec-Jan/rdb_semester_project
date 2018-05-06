package cz.tul.model.generic.projections;

import cz.tul.model.mysql.Auto;
import cz.tul.model.mysql.Ridic;
import cz.tul.model.generic.GatePassageProjection;

import java.sql.Timestamp;

public class GatePassageMongoProjection implements GatePassageProjection,GatePassageProjection.RidicCrpJmenoOnly,GatePassageProjection.AutoSpzVyrobceTypOnly {


    private Ridic ridic;
    private Auto auto;
    private Timestamp timestamp;

    public GatePassageMongoProjection(Ridic ridic, Auto auto, Timestamp timestamp) {
        this.ridic = ridic;
        this.auto = auto;
        this.timestamp = timestamp;
    }


    @Override
    public Timestamp getCas() {
        return timestamp;
    }

    @Override
    public RidicCrpJmenoOnly getRidic() {
        return this;
    }

    @Override
    public AutoSpzVyrobceTypOnly getAuto() {
        return this;
    }

    @Override
    public String getSpz() {
        return auto.getSpz();
    }

    @Override
    public String getVyrobce() {
        return auto.getVyrobce();
    }

    @Override
    public String getTyp() {
        return auto.getTyp();
    }

    @Override
    public String getCrp() {
        return ridic.getCrp();
    }

    @Override
    public String getJmeno() {
        return ridic.getJmeno();
    }
}
