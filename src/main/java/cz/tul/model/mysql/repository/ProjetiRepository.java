package cz.tul.model.mysql.repository;

import cz.tul.model.mysql.ProjetiMysql;
import cz.tul.model.mysql.ProjetiId;
import cz.tul.model.generic.GatePassageProjection;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
public interface ProjetiRepository extends CrudRepository<ProjetiMysql, ProjetiId> {

    List<GatePassageProjection> findByBrana_Id(String id, Pageable pageable);

}
