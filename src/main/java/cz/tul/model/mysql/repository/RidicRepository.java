package cz.tul.model.mysql.repository;

import cz.tul.model.mysql.Ridic;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface RidicRepository extends CrudRepository<Ridic, String> {
    Ridic findFirstByCrp(String crp);
}
