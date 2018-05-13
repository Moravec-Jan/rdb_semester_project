package cz.tul.mysql.repository;

import cz.tul.mysql.model.ProjetiMysql;
import cz.tul.mysql.model.ProjetiId;
import cz.tul.model.generic.GatePassageProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjetiRepository extends CrudRepository<ProjetiMysql, ProjetiId> {

    List<GatePassageProjection> findByBrana_Id(String id, Pageable pageable);

}
