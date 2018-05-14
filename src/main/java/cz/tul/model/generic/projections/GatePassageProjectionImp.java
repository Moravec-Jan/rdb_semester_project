package cz.tul.model.generic.projections;

import cz.tul.mysql.model.Car;
import cz.tul.mysql.model.Driver;

import java.sql.Timestamp;

public class GatePassageProjectionImp implements cz.tul.model.generic.GatePassageProjection, cz.tul.model.generic.GatePassageProjection.RidicCrpJmenoOnly, cz.tul.model.generic.GatePassageProjection.AutoSpzVyrobceTypOnly {


    private Driver ridic;
    private Car auto;
    private Timestamp timestamp;

    public GatePassageProjectionImp(Driver ridic, Car auto, Timestamp timestamp) {
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
