package cz.tul.model.mysql.repository;

import cz.tul.model.mysql.Auto;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional(propagation = Propagation.REQUIRED)
@Repository
public interface AutoRepository extends JpaRepository<Auto, String> {
}
