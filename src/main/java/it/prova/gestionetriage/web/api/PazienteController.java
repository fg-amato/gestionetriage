package it.prova.gestionetriage.web.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.gestionetriage.dto.PazienteDTO;
import it.prova.gestionetriage.model.Paziente;
import it.prova.gestionetriage.service.PazienteService;
import it.prova.gestionetriage.web.api.exception.IdNotNullForInsertException;
import it.prova.gestionetriage.web.api.exception.PazienteNotFoundException;

@RestController
@RequestMapping("api/paziente")
public class PazienteController {

	@Autowired
	private PazienteService pazienteService;

	@GetMapping
	public List<PazienteDTO> getAll() {
		// senza DTO qui hibernate dava il problema del N + 1 SELECT
		// (probabilmente dovuto alle librerie che serializzano in JSON)
		return PazienteDTO.createPazienteDTOListFromModelList(pazienteService.listAllElements(), true);
	}

	@GetMapping("/{id}")
	public PazienteDTO findById(@PathVariable(value = "id", required = true) long id) {
		Paziente paziente = pazienteService.caricaSingoloElementoConDottore(id);

		if (paziente == null)
			throw new PazienteNotFoundException("Paziente not found con id: " + id);

		return PazienteDTO.buildPazienteDTOFromModel(paziente, true);
	}

	// gli errori di validazione vengono mostrati con 400 Bad Request ma
	// elencandoli grazie al ControllerAdvice
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PazienteDTO createNew(@Valid @RequestBody PazienteDTO pazienteInput) {
		// se mi viene inviato un id jpa lo interpreta come update ed a me (producer)
		// non sta bene
		if (pazienteInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");

		Paziente pazienteInserito = pazienteService.inserisciNuovo(pazienteInput.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteInserito, false);
	}

	@PutMapping("/{id}")
	public PazienteDTO update(@Valid @RequestBody PazienteDTO pazienteInput, @PathVariable(required = true) Long id) {
		Paziente paziente = pazienteService.caricaSingoloElemento(id);

		if (paziente == null)
			throw new PazienteNotFoundException("Paziente not found con id: " + id);

		pazienteInput.setId(id);
		Paziente pazienteAggiornato = pazienteService.aggiorna(pazienteInput.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteAggiornato, false);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(required = true) Long id) {
		Paziente paziente = pazienteService.caricaSingoloElemento(id);

		if (paziente == null)
			throw new PazienteNotFoundException("Paziente not found con id: " + id);

		pazienteService.rimuovi(paziente);
	}

	@PostMapping("/search")
	public List<PazienteDTO> search(@RequestBody PazienteDTO example, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "9") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy) {
		return PazienteDTO.createPazienteDTOListFromModelList((List<Paziente>) pazienteService
				.findByExampleWithPagination(example.buildPazienteModel(), pageNo, pageSize, sortBy).getContent(),
				false);
	}

}
