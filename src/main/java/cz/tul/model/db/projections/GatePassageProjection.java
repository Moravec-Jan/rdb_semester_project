package cz.tul.model.db.projections;

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
