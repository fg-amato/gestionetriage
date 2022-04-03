package it.prova.gestionetriage.service;

import java.util.List;

import org.springframework.data.domain.Page;

import it.prova.gestionetriage.model.Paziente;

public interface PazienteService {
	List<Paziente> listAllElements();

	List<Paziente> listAllElementsEager();

	Paziente caricaSingoloElemento(Long id);

	Paziente caricaSingoloElementoConDottore(Long id);

	Paziente aggiorna(Paziente pazienteInstance);

	Paziente inserisciNuovo(Paziente pazienteInstance);

	public Page<Paziente> findByExampleWithPagination(Paziente example, Integer pageNo, Integer pageSize,
			String sortBy);

	void rimuovi(Paziente pazienteInstance);

}
