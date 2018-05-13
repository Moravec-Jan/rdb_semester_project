package cz.tul.model.generic;


import cz.tul.mysql.model.Auto;
import cz.tul.mysql.model.Brana;
import cz.tul.mysql.model.Ridic;

import java.sql.Timestamp;

public interface Projeti {
    String TABLE_NAME = "Projeti";
    String COLUMN_FAMILY = "data";

    Timestamp getCas();

    int getNajeto();

    int getBenzin();

    float getNapeti();

    Ridic getRidic();

    Brana getBrana();

    Auto getAuto();
}
