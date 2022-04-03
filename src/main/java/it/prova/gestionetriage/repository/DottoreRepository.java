package it.prova.gestionetriage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.prova.gestionetriage.model.Dottore;

public interface DottoreRepository
		extends PagingAndSortingRepository<Dottore, Long>, JpaSpecificationExecutor<Dottore> {

	@Query("from Dottore d left join fetch d.pazienteAttualmenteInVisita where d.id=?1")
	Dottore findByIdEager(Long id);

	@Query("select distinct d from Dottore d left join fetch d.pazienteAttualmenteInVisita")
	List<Dottore> findAllEager();

	Dottore findByCodiceDipendente(String codiceDipendente);
}
