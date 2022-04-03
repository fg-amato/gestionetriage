package it.prova.gestionetriage.service;

import java.util.ArrayList;
import java.util.Date;
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

import it.prova.gestionetriage.model.Paziente;
import it.prova.gestionetriage.model.StatoPaziente;
import it.prova.gestionetriage.repository.PazienteRepository;

@Service
public class PazienteServiceImpl implements PazienteService {

	@Autowired
	private PazienteRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Paziente> listAllElements() {
		return (List<Paziente>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Paziente> listAllElementsEager() {
		return (List<Paziente>) repository.findAllEager();
	}

	@Override
	@Transactional(readOnly = true)
	public Paziente caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Paziente caricaSingoloElementoConDottore(Long id) {
		return repository.findByIdEager(id);
	}

	@Override
	@Transactional
	public Paziente aggiorna(Paziente pazienteInstance) {
		return repository.save(pazienteInstance);
	}

	@Override
	@Transactional
	public Paziente inserisciNuovo(Paziente pazienteInstance) {
		pazienteInstance.setDataRegistrazione(new Date());
		pazienteInstance.setStatoPaziente(StatoPaziente.IN_ATTESA_VISITA);
		return repository.save(pazienteInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Paziente pazienteInstance) {
		repository.delete(pazienteInstance);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Paziente> findByExampleWithPagination(Paziente example, Integer pageNo, Integer pageSize,
			String sortBy) {
		Specification<Paziente> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getNome()))
				predicates.add(cb.like(cb.upper(root.get("nome")), "%" + example.getNome().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getCognome()))
				predicates.add(cb.like(cb.upper(root.get("cognome")), "%" + example.getCognome().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getCodiceFiscale()))
				predicates.add(cb.like(cb.upper(root.get("codiceFiscale")),
						"%" + example.getCodiceFiscale().toUpperCase() + "%"));
			if (example.getDataRegistrazione() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("dataRegistrazione"), example.getDataRegistrazione()));

			if (example.getStatoPaziente() != null)
				predicates.add(cb.equal(root.get("statoPaziente"), example.getStatoPaziente()));

			if (example.getDottore() != null && example.getDottore().getId() != null)
				predicates.add(cb.equal(cb.upper(root.get("dottore")), example.getDottore().getId()));

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

}
