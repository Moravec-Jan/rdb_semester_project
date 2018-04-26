package cz.tul.repository;

import cz.tul.model.db.Projeti;
import cz.tul.model.db.ProjetiId;
import cz.tul.model.db.projections.GatePassageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetiRepository extends CrudRepository<Projeti, ProjetiId> {

    List<GatePassageProjection> findByBrana_Id(String id, Pageable pageable);

    Page<Projeti> findAll(Pageable pageable);
}
