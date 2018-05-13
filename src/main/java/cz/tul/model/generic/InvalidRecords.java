package cz.tul.model.generic;


public interface InvalidRecords {

    String TABLE_NAME = "Nevalidni_zaznamy";
    String COLUMN_FAMILY= "data";
    int getId();

    int getPocet();
}
