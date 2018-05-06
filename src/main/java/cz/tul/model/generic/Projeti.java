package cz.tul.model.generic;


import cz.tul.model.mysql.Auto;
import cz.tul.model.mysql.Brana;
import cz.tul.model.mysql.Ridic;

import java.sql.Timestamp;

public interface Projeti {
    String TABLE_NAME = "Projeti";

    Timestamp getCas();

    int getNajeto();

    int getBenzin();

    float getNapeti();

    Ridic getRidic();

    Brana getBrana();

    Auto getAuto();
}
