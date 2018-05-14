package cz.tul.hbase;

import cz.tul.hbase.model.DriverHbase;
import cz.tul.hbase.repository.InvalidRecordsHbaseRepository;
import cz.tul.hbase.repository.PassageHbaseRepository;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.model.generic.InvalidRecords;
import cz.tul.model.generic.Passage;
import cz.tul.model.ui.DriverEntity;
import cz.tul.mysql.model.Car;
import cz.tul.mysql.model.Gate;
import cz.tul.mysql.model.Driver;
import cz.tul.service.DatabaseService;
import org.apache.hadoop.hbase.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Profile("hbase")
@Service
public class HbaseDatabaseService extends DatabaseService {
    @Autowired
    Admin admin;

    @Autowired
    PassageHbaseRepository passageRepository;

    @Autowired
    InvalidRecordsHbaseRepository invalidRecordsRepository;

    @PostConstruct
    private void createScheme() throws IOException {
        HbaseConfig.createScheme(admin);
    }

    //@Transactional
    @Override
    public void deleteAll() {
        try {
            passageRepository.delete(Passage.TABLE_NAME);
            invalidRecordsRepository.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean saveWholeRecord(Passage projeti) {
        try {
            if (!validateBrana(projeti.getBrana())) {
                return false;
            }
            if (!validateAuto(projeti.getAuto())) {
                return false;
            }
            if (!validateRidic(projeti.getRidic())) {
                return false;
            }


            passageRepository.save(projeti);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean validateAuto(Car auto) throws IOException {
        Car found = passageRepository.getAutoBySpz(auto.getSpz());
        if (found == null)
            return true;
        return found.getTyp().equals(auto.getTyp()) && found.getVyrobce().equals(auto.getVyrobce()) && found.getBarva() == auto.getBarva();
    }

    private boolean validateRidic(Driver ridic) throws IOException {
        Driver found = passageRepository.getRidicByCrp(ridic.getCrp());
        if (found == null)
            return true;
        return found.getJmeno().equals(ridic.getJmeno());
    }

    private boolean validateBrana(Gate brana) throws IOException {
        Gate found = passageRepository.getBranaById(brana.getId());
        if (found == null)
            return true;
        return found.getCena() == brana.getCena() && found.getTyp().equals(brana.getTyp());
    }

    @Override
    public void addInvalidRecords(int count) {
        try {
            invalidRecordsRepository.increment(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InvalidRecords getInvalidRecordsReport() {
        try {
            return invalidRecordsRepository.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<GatePassageProjection> getByGate(String key) {
        try {
            return passageRepository.getByBranaId(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<DriverEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km) {
        List<DriverEntity> entities = new ArrayList<>();
        try {
            List<DriverHbase> drivers = passageRepository.getDriverByDate(from, to);
            if (drivers == null) {
                return null;
            }

            for (DriverHbase driver : drivers) {
                if (containedInList(driver.getCrp(), entities)) {
                    continue;
                }
                boolean passedSatelliteGate = passageRepository.hasPassedSatelliteGate(from, to, driver.getCrp());
                if (passedSatelliteGate) {
                    continue;
                }
                int distance = passageRepository.findDistance(from, to, driver.getCrp());
                if (distance >= km) {
                    entities.add(new DriverEntity(distance, driver.getCrp(), driver.getName()));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return entities;
    }

    private boolean containedInList(String crp, List<DriverEntity> entities) {
        for (DriverEntity entity : entities) {
            if (entity.getCrp().equals(crp))
                return true;
        }
        return false;
    }

}
