package it.prova.gestionetriage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.prova.gestionetriage.model.Paziente;

public interface PazienteRepository
		extends PagingAndSortingRepository<Paziente, Long>, JpaSpecificationExecutor<Paziente> {
	@Query("from Paziente p left join fetch p.dottore where p.id=?1")
	Paziente findByIdEager(Long id);

	@Query("select distinct p from Paziente p left join fetch p.dottore ")
	List<Paziente> findAllEager();

}
