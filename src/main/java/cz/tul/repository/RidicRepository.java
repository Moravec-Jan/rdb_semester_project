package cz.tul.repository;

import cz.tul.model.db.Ridic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RidicRepository extends CrudRepository<Ridic, String> {
}
