package cz.tul.repository;

import cz.tul.model.Brana;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranaRepository extends CrudRepository<Brana, String> {
}
