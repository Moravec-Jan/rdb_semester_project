package cz.tul.model.generic;

import java.sql.Timestamp;

public interface GatePassageProjection {
    Timestamp getCas();

    RidicCrpJmenoOnly getRidic();

    AutoSpzVyrobceTypOnly getAuto();

    interface AutoSpzVyrobceTypOnly {
        String getSpz();

        String getVyrobce();

        String getTyp();
    }

    interface RidicCrpJmenoOnly {
        String getCrp();

        String getJmeno();
    }
}
