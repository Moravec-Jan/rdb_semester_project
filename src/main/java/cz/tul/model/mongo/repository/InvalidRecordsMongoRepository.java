package cz.tul.model.mongo.repository;

import cz.tul.model.mongo.InvalidMongoRecords;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Profile("mongo")
@Repository
public interface InvalidRecordsMongoRepository extends MongoRepository<InvalidMongoRecords, Integer> {
}
