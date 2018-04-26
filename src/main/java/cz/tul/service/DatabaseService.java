package cz.tul.service;

import cz.tul.model.ui.RidicEntity;
import cz.tul.model.db.Projeti;
import cz.tul.model.db.projections.GatePassageProjection;
import cz.tul.repository.AutoRepository;
import cz.tul.repository.BranaRepository;
import cz.tul.repository.ProjetiRepository;
import cz.tul.repository.RidicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.util.List;

@Service
public class DatabaseService {
    @Lazy
    @Autowired
    ProjetiRepository projetiRepository;
    @Lazy
    @Autowired
    BranaRepository branaRepository;
    @Lazy
    @Autowired
    AutoRepository autoRepository;
    @Lazy
    @Autowired
    RidicRepository ridicRepository;
    @Lazy
    @Autowired
    EntityManagerFactory factory;

    public void deleteAll() {
        projetiRepository.deleteAll();
        branaRepository.deleteAll();
        autoRepository.deleteAll();
        ridicRepository.deleteAll();
    }

    public long getCount() {
        return projetiRepository.count();
    }

    public List<Projeti> getAll(Pageable page) {
        return projetiRepository.findAll(page).getContent();
    }

    public Iterable<Projeti> getAll() {
        return projetiRepository.findAll();
    }

    public void saveWholeRecords(List<Projeti> projeti) {
        if (projeti != null) {
            projeti.forEach(projeti1 -> {
                ridicRepository.save(projeti1.getRidic());
                autoRepository.save(projeti1.getAuto());
                branaRepository.save(projeti1.getBrana());
                try {
                    projetiRepository.save(projeti1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public List<GatePassageProjection> getByGate(String key) {
        return projetiRepository.findByBrana_Id(key, PageRequest.of(0, 1000));
    }

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
