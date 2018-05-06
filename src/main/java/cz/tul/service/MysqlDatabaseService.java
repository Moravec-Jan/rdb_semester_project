package cz.tul.service;

import cz.tul.model.mysql.Auto;
import cz.tul.model.mysql.Brana;
import cz.tul.model.mysql.InvalidMySqlRecords;
import cz.tul.model.mysql.ProjetiMysql;
import cz.tul.model.generic.Projeti;
import cz.tul.model.mysql.repository.*;
import cz.tul.model.ui.RidicEntity;
import cz.tul.model.generic.GatePassageProjection;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Profile("mysql")
@Service
public class MysqlDatabaseService extends DatabaseService {
    @Autowired
    ProjetiRepository projetiRepository;
    @Autowired
    BranaRepository branaRepository;
    @Autowired
    AutoRepository autoRepository;
    @Autowired
    RidicRepository ridicRepository;
    @Autowired
    EntityManagerFactory factory;
    @Autowired
    InvalidRecordsRepository invalidRecordsRepository;

    @Override
    public void deleteAll() {
        invalidRecordsRepository.save(new InvalidMySqlRecords(1, 0)); // reset to zero
        projetiRepository.deleteAll();
        branaRepository.deleteAll();
        autoRepository.deleteAll();
        ridicRepository.deleteAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveWholeRecord(Projeti projeti) {
            if (!ridicRepository.existsById(projeti.getRidic().getCrp()))
                ridicRepository.save(projeti.getRidic());
            if (!autoRepository.existsById(projeti.getAuto().getSpz()))
                autoRepository.save(projeti.getAuto());
            if (!branaRepository.existsById(projeti.getBrana().getId()))
                branaRepository.save(projeti.getBrana());

            projetiRepository.save(new ProjetiMysql(projeti));
    }

    @Override
    public void addInvalidRecords(int count) {
        Optional<InvalidMySqlRecords> number = invalidRecordsRepository.findById(1);
        number.ifPresent(invalidRecords -> invalidRecordsRepository.save(new InvalidMySqlRecords(1, number.get().getPocet() + count)));
    }

    @Override
    public Iterable<InvalidMySqlRecords> getInvalidRecordsReport() {
        return invalidRecordsRepository.findAll();
    }

    @Override
    public List<GatePassageProjection> getByGate(String key) {
        return projetiRepository.findByBrana_Id(key, PageRequest.of(0, 1000));
    }

    @Override
    public List<RidicEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km) {

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
                "WHERE A.najeto_km > " + km, RidicEntity.class).getResultList();
    }
}
