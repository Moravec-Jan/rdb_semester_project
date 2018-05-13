package cz.tul.mongo.repository;

import cz.tul.mongo.model.InvalidMongoRecords;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Profile("mongo")
@Repository
public interface InvalidRecordsMongoRepository extends MongoRepository<InvalidMongoRecords, Integer> {
}
