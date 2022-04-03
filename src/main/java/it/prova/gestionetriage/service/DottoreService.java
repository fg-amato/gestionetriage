package it.prova.gestionetriage.service;

import java.util.List;

import org.springframework.data.domain.Page;

import it.prova.gestionetriage.model.Dottore;

public interface DottoreService {
	List<Dottore> listAllElements();

	List<Dottore> listAllElementsEager();

	Dottore caricaSingoloElemento(Long id);

	Dottore caricaSingoloElementoConPaziente(Long id);

	Dottore aggiorna(Dottore dottoreInstance);

	Dottore inserisciNuovo(Dottore dottoreInstance);

	public Page<Dottore> findByExampleWithPagination(Dottore example, Integer pageNo, Integer pageSize, String sortBy);

	void rimuovi(Dottore pazienteInstance);

	Dottore assegnaPaziente(String codiceDipendente, Long idPaziente);

}
