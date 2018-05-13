package cz.tul.mysql.repository;

import cz.tul.mysql.model.Brana;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BranaRepository extends CrudRepository<Brana, String> {
    Brana findFirstById(String id);
}
