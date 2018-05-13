package cz.tul.hbase;

import cz.tul.hbase.model.RidicHbase;
import cz.tul.hbase.repository.InvalidRecordsHbaseRepository;
import cz.tul.hbase.repository.ProjetiHbaseRepository;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.model.generic.InvalidRecords;
import cz.tul.model.generic.Projeti;
import cz.tul.model.ui.RidicEntity;
import cz.tul.mysql.model.Auto;
import cz.tul.mysql.model.Brana;
import cz.tul.mysql.model.Ridic;
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
    ProjetiHbaseRepository projetiRepository;

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
            projetiRepository.delete(Projeti.TABLE_NAME);
            invalidRecordsRepository.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean saveWholeRecord(Projeti projeti) {
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


            projetiRepository.save(projeti);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean validateAuto(Auto auto) throws IOException {
        Auto found = projetiRepository.getAutoBySpz(auto.getSpz());
        if (found == null)
            return true;
        return found.getTyp().equals(auto.getTyp()) && found.getVyrobce().equals(auto.getVyrobce()) && found.getBarva() == auto.getBarva();
    }

    private boolean validateRidic(Ridic ridic) throws IOException {
        Ridic found = projetiRepository.getRidicByCrp(ridic.getCrp());
        if (found == null)
            return true;
        return found.getJmeno().equals(ridic.getJmeno());
    }

    private boolean validateBrana(Brana brana) throws IOException {
        Brana found = projetiRepository.getBranaById(brana.getId());
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
            return projetiRepository.getByBranaId(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<RidicEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km) {
        List<RidicEntity> entities = new ArrayList<>();
        try {
            RidicHbase ridicHbase = null;
            String lastKey = null;
            while (true) {
                ridicHbase = projetiRepository.getRidicByDate(from, to, lastKey);

                if (ridicHbase == null || containedInList(ridicHbase.getCrp(), entities))
                    break;

                lastKey = ridicHbase.getRowId();
                boolean passedSatelliteGate = projetiRepository.hasPassedSatelliteGate(from, to, ridicHbase.getCrp());

                if (passedSatelliteGate) {
                    continue;
                }
                int distance = projetiRepository.findDistance(from, to, ridicHbase.getCrp());
                if (distance > km) {
                    entities.add(new RidicEntity(distance, ridicHbase.getCrp(), ridicHbase.getJmeno()));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return entities;
    }

    private boolean containedInList(String crp, List<RidicEntity> entities) {
        for (RidicEntity entity : entities) {
            if (entity.getCrp().equals(crp))
                return true;
        }
        return false;
    }

}
