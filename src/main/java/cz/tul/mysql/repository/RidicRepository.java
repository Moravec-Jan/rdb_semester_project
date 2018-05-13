package cz.tul.mysql.repository;

import cz.tul.mysql.model.Ridic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RidicRepository extends CrudRepository<Ridic, String> {
    Ridic findFirstByCrp(String crp);
}
