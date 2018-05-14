package cz.tul.mongo.repository;

import cz.tul.model.generic.Passage;
import cz.tul.mysql.model.PassageId;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.mongo.model.PassageMongo;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile("mongo")
@Repository
public interface PassageMongoRepository extends MongoRepository<PassageMongo, PassageId> {
    @Transactional
    List<GatePassageProjection> findByBrana_Id(String id, Pageable pageable);

    Passage findFirstByAuto_Spz(String spz);

    Passage findFirstByRidic_Crp(String crp);

    Passage findFirstByBrana_Id(String id);

}
