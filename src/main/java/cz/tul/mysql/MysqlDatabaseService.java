package cz.tul.mysql;

import cz.tul.model.generic.InvalidRecords;
import cz.tul.model.generic.Passage;
import cz.tul.mysql.model.*;
import cz.tul.mysql.repository.*;
import cz.tul.model.ui.DriverEntity;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Profile("mysql")
@Service
public class MysqlDatabaseService extends DatabaseService {
    @Autowired
    PassageRepository passageRepository;
    @Autowired
    GateRepository gateRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    EntityManagerFactory factory;
    @Autowired
    InvalidRecordsRepository invalidRecordsRepository;

    @Override
    public void deleteAll() {
        invalidRecordsRepository.save(new InvalidMySqlRecords(1, 0)); // reset to zero
        passageRepository.deleteAll();
        gateRepository.deleteAll();
        carRepository.deleteAll();
        driverRepository.deleteAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean saveWholeRecord(Passage projeti) {
        Boolean branaValid = validateBrana(projeti.getBrana());
        if (branaValid != null && !branaValid) {
            return false;
        }
        Boolean autoValid = validateAuto(projeti.getAuto());
        if (autoValid != null && !autoValid) {
            return false;
        }
        Boolean ridicValid = validateRidic(projeti.getRidic());
        if (ridicValid != null && !ridicValid) {
            return false;
        }


        if (autoValid == null) {
            carRepository.save(projeti.getAuto());

        }
        if (ridicValid == null) {
            driverRepository.save(projeti.getRidic());
        }
        if (branaValid == null) {
            gateRepository.save(projeti.getBrana());
        }

        passageRepository.save(new PassageMysql(projeti));
        return true;
    }

    private Boolean validateRidic(Driver ridic) {
        Driver found = driverRepository.findFirstByCrp(ridic.getCrp());
        if (found == null)
            return null;
        return found.getJmeno().equals(ridic.getJmeno());
    }

    private Boolean validateAuto(Car auto) {
        Car found = carRepository.findFirstBySpz(auto.getSpz());
        if (found == null)
            return null;
        return found.getTyp().equals(auto.getTyp()) && found.getVyrobce().equals(auto.getVyrobce()) && found.getBarva() == auto.getBarva();
    }

    private Boolean validateBrana(Gate brana) {
        Gate found = gateRepository.findFirstById(brana.getId());
        if (found == null) {
            return null;
        }
        return  found.getCena() == brana.getCena() && found.getTyp().equals(brana.getTyp());
    }


    public void addInvalidRecords(int count) {
        Optional<InvalidMySqlRecords> number = invalidRecordsRepository.findById(1);
        number.ifPresent(invalidRecords -> invalidRecordsRepository.save(new InvalidMySqlRecords(1, number.get().getPocet() + count)));
    }

    @Override
    public InvalidRecords getInvalidRecordsReport() {
        return invalidRecordsRepository.findAll().iterator().next();
    }

    @Override
    public List<GatePassageProjection> getByGate(String key) {
        return passageRepository.findByBrana_Id(key, PageRequest.of(0, 1000));
    }

    @Override
    public List<DriverEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km) {

        return factory.createEntityManager().createNativeQuery("SELECT crp,jmeno,najeto_km  " +
                "FROM( " +
                " SELECT crp_ridic,(MAX(najeto) - MIN(najeto)) as najeto_km " +
                " FROM rdb.projeti " +
                " WHERE cas BETWEEN TIMESTAMP('" + from + "') AND TIMESTAMP('" + to + "') " +
                " AND crp_ridic NOT IN ( " +
                "  SELECT rdb.projeti.crp_ridic " +
                "  FROM rdb.projeti INNER JOIN rdb.brana ON rdb.projeti.id_brana = rdb.brana.id  " +
                "  WHERE rdb.brana.typ='Satellite' AND cas BETWEEN TIMESTAMP('" + from + "') AND TIMESTAMP('" + to + "') " +
                " ) " +
                " GROUP BY crp_ridic) AS A INNER JOIN ridic ON A.crp_ridic = ridic.crp " +
                "WHERE A.najeto_km > " + km, DriverEntity.class).getResultList();
    }
}
