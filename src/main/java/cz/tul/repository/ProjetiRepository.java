package cz.tul.repository;

import cz.tul.model.Projeti;
import cz.tul.model.ProjetiId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetiRepository extends CrudRepository<Projeti, ProjetiId> {
}
