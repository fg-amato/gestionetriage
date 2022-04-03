package it.prova.gestionetriage.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.gestionetriage.model.Dottore;
import it.prova.gestionetriage.model.Paziente;
import it.prova.gestionetriage.model.StatoPaziente;
import it.prova.gestionetriage.repository.DottoreRepository;
import it.prova.gestionetriage.repository.PazienteRepository;
import it.prova.gestionetriage.web.api.exception.PazienteConDottoreException;
import it.prova.gestionetriage.web.api.exception.PazienteNotFoundException;

@Service
public class DottoreServiceImpl implements DottoreService {

	@Autowired
	private DottoreRepository repository;

	@Autowired
	private PazienteRepository pazienteRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Dottore> listAllElements() {
		return (List<Dottore>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Dottore> listAllElementsEager() {
		return (List<Dottore>) repository.findAllEager();
	}

	@Override
	@Transactional(readOnly = true)
	public Dottore caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Dottore caricaSingoloElementoConPaziente(Long id) {
		return repository.findByIdEager(id);
	}

	@Override
	@Transactional
	public Dottore aggiorna(Dottore dottoreInstance) {
		return repository.save(dottoreInstance);
	}

	@Override
	@Transactional
	public Dottore inserisciNuovo(Dottore dottoreInstance) {
		return repository.save(dottoreInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Dottore dottoreInstance) {
		repository.delete(dottoreInstance);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Dottore> findByExampleWithPagination(Dottore example, Integer pageNo, Integer pageSize, String sortBy) {
		Specification<Dottore> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getNome()))
				predicates.add(cb.like(cb.upper(root.get("nome")), "%" + example.getNome().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getCognome()))
				predicates.add(cb.like(cb.upper(root.get("cognome")), "%" + example.getCognome().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getCodiceDipendente()))
				predicates.add(cb.like(cb.upper(root.get("codiceDipendente")),
						"%" + example.getCodiceDipendente().toUpperCase() + "%"));

			if (example.getPazienteAttualmenteInVisita() != null
					&& example.getPazienteAttualmenteInVisita().getId() != null)
				predicates.add(cb.equal(cb.upper(root.get("pazienteAttualmenteInVisita")),
						example.getPazienteAttualmenteInVisita().getId()));

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		Pageable paging = null;
		// se non passo parametri di paginazione non ne tengo conto
		if (pageSize == null || pageSize < 10)
			paging = Pageable.unpaged();
		else
			paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		return repository.findAll(specificationCriteria, paging);
	}

	@Override
	@Transactional
	public Dottore assegnaPaziente(String codiceDipendente, Long idPaziente) {
		Paziente daVisitare = pazienteRepository.findById(idPaziente).get();

		if (daVisitare == null) {
			throw new PazienteNotFoundException("Il paziente con id: " + idPaziente + " non è stato trovato!");
		}

		if (daVisitare.getStatoPaziente() != StatoPaziente.IN_ATTESA_VISITA) {
			throw new PazienteConDottoreException("Il paziente con id: " + idPaziente + " non è in attesa di visita");
		}

		Dottore quelloCheVisita = repository.findByCodiceDipendente(codiceDipendente);

		daVisitare.setDottore(quelloCheVisita);
		quelloCheVisita.setPazienteAttualmenteInVisita(daVisitare);
		daVisitare.setStatoPaziente(StatoPaziente.IN_VISITA);
		

		return repository.save(quelloCheVisita);
	}

}
