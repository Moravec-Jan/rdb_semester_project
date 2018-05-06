package cz.tul.service;

import cz.tul.model.mysql.Brana;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.model.generic.Projeti;
import cz.tul.model.mongo.InvalidMongoRecords;
import cz.tul.model.mongo.ProjetiMongo;
import cz.tul.model.ui.RidicEntity;
import cz.tul.model.mongo.repository.InvalidRecordsMongoRepository;
import cz.tul.model.mongo.repository.ProjetiMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Profile("mongo")
@Service
public class MongoDatabaseService extends DatabaseService {
    @Lazy
    @Autowired
    ProjetiMongoRepository projetiMongoRepository;

    @Lazy
    @Autowired
    InvalidRecordsMongoRepository invalidRecordsMongoRepository;

    @Autowired
    private MongoTemplate mongo;

    @Override
    public void deleteAll() {
        invalidRecordsMongoRepository.save(new InvalidMongoRecords(1, 0));
        projetiMongoRepository.deleteAll();
    }

    @Override
    public void saveWholeRecord(Projeti projeti) {
//        if (projetiMongoRepository.existsByAuto_Spz(projeti.getAuto().getSpz()) &&
//                projetiMongoRepository.existsByBrana_Id(projeti.getBrana().getId()) &&
//                projetiMongoRepository.existsByRidic_Crp(projeti.getRidic().getCrp()))
            projetiMongoRepository.save(new ProjetiMongo(projeti));
    }


    @Override
    public void addInvalidRecords(int count) {
        Optional<InvalidMongoRecords> records = invalidRecordsMongoRepository.findById(1);

        if (!records.isPresent()) {
            invalidRecordsMongoRepository.save(new InvalidMongoRecords(1, count));

        } else {
            InvalidMongoRecords invalidRecords = records.get();
            invalidRecords.setPocet(invalidRecords.getPocet() + count);
            try {
                invalidRecordsMongoRepository.save(invalidRecords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Iterable<InvalidMongoRecords> getInvalidRecordsReport() {
        return invalidRecordsMongoRepository.findAll();
    }

    @Override
    public List<GatePassageProjection> getByGate(String key) {
        return projetiMongoRepository.findByBrana_Id(key, PageRequest.of(0, 1000));
    }

    @Override
    public List<RidicEntity> getDriversByKmWhoDidNotPassSatelliteGate(Timestamp from, Timestamp to, int km) {
        return null;
    }
}
