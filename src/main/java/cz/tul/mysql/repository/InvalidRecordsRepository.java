package cz.tul.mysql.repository;

import cz.tul.mysql.model.InvalidMySqlRecords;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidRecordsRepository extends CrudRepository<InvalidMySqlRecords, Integer> {

}
