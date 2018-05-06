package cz.tul.model.mongo.repository;

import cz.tul.model.generic.Projeti;
import cz.tul.model.mysql.ProjetiId;
import cz.tul.model.generic.GatePassageProjection;
import cz.tul.model.mongo.ProjetiMongo;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile("mongo")
@Repository
public interface ProjetiMongoRepository extends MongoRepository<ProjetiMongo, ProjetiId> {
    @Transactional
    List<GatePassageProjection> findByBrana_Id(String id, Pageable pageable);


    boolean existsByRidic_Crp(String id);

    boolean existsByAuto_Spz(String id);

    boolean existsByBrana_Id(String id);
}
