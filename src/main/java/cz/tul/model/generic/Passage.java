package cz.tul.model.generic;


import cz.tul.mysql.model.Car;
import cz.tul.mysql.model.Gate;
import cz.tul.mysql.model.Driver;

import java.sql.Timestamp;

public interface Passage {
    String TABLE_NAME = "Projeti";
    String COLUMN_FAMILY = "data";

    Timestamp getCas();

    int getNajeto();

    int getBenzin();

    float getNapeti();

    Driver getRidic();

    Gate getBrana();

    Car getAuto();
}
