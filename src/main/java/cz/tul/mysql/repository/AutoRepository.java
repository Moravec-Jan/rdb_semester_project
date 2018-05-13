package cz.tul.mysql.repository;

import cz.tul.mysql.model.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional(propagation = Propagation.REQUIRED)
@Repository
public interface AutoRepository extends JpaRepository<Auto, String> {
    Auto findFirstBySpz(String spz);
}
