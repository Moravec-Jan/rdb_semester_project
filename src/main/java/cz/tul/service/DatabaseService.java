package cz.tul.service;

import cz.tul.model.generic.GatePassageProjection;
import cz.tul.model.generic.InvalidRecords;
import cz.tul.model.generic.Passage;
import cz.tul.model.ui.DriverEntity;

import java.sql.Timestamp;
import java.util.List;

public abstract class DatabaseService {
    public abstract void deleteAll();

    public abstract boolean saveWholeRecord(Passage projeti);

    public abstract void addInvalidRecords(int count);

    public abstract InvalidRecords getInvalidRecordsReport();

    public abstract List<GatePassageProjection> getByGate(String key);

    public abstract List<DriverEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km);
}
