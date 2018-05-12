package cz.tul.service;

import cz.tul.model.generic.GatePassageProjection;
import cz.tul.model.generic.InvalidRecords;
import cz.tul.model.generic.Projeti;
import cz.tul.model.ui.RidicEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public abstract class DatabaseService {
    public abstract void deleteAll();

    public abstract boolean saveWholeRecord(Projeti projeti);

    public abstract void addInvalidRecords(int count);

    public abstract Iterable<? extends InvalidRecords> getInvalidRecordsReport();

    public abstract List<GatePassageProjection> getByGate(String key);

    public abstract List<RidicEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km);
}
