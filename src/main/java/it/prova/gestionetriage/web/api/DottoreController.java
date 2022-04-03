package it.prova.gestionetriage.web.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.gestionetriage.dto.DottoreConBooleaniDTO;
import it.prova.gestionetriage.dto.DottoreDTO;
import it.prova.gestionetriage.model.Dottore;
import it.prova.gestionetriage.service.DottoreService;
import it.prova.gestionetriage.web.api.exception.DottoreCheNonPuoVisitareIlPazienteCheDicoIoException;
import it.prova.gestionetriage.web.api.exception.DottoreConPazienteAssociatoException;
import it.prova.gestionetriage.web.api.exception.DottoreNotFoundException;
import it.prova.gestionetriage.web.api.exception.IdNotNullForInsertException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/dottore")
public class DottoreController {

	@Autowired
	private DottoreService dottoreService;

	@Autowired
	private WebClient webClient;

	@GetMapping
	public List<DottoreDTO> getAll() {
		// senza DTO qui hibernate dava il problema del N + 1 SELECT
		// (probabilmente dovuto alle librerie che serializzano in JSON)
		return DottoreDTO.createDottoreDTOListFromModelList(dottoreService.listAllElementsEager(), false);
	}

	@GetMapping("/{id}")
	public DottoreDTO findById(@PathVariable(value = "id", required = true) long id) {
		Dottore dottore = dottoreService.caricaSingoloElementoConPaziente(id);

		if (dottore == null)
			throw new DottoreNotFoundException("Dottore not found con id: " + id);

		return DottoreDTO.buildDottoreDTOFromModel(dottore, false);
	}

	// gli errori di validazione vengono mostrati con 400 Bad Request ma
	// elencandoli grazie al ControllerAdvice
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DottoreDTO createNew(@Valid @RequestBody DottoreDTO dottoreInput) {
		// se mi viene inviato un id jpa lo interpreta come update ed a me (producer)
		// non sta bene
		if (dottoreInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");

		// prima di salvarlo devo verificare se la banca dati esterna lo censisce
		ResponseEntity<DottoreDTO> response = webClient.post().uri("")
				.body(Mono.just(new DottoreDTO(dottoreInput.getNome(), dottoreInput.getCognome(),
						dottoreInput.getCodiceDipendente())), DottoreDTO.class)
				.retrieve().toEntity(DottoreDTO.class).block();

		// ANDREBBE GESTITA CON ADVICE!!!
		if (response.getStatusCode() != HttpStatus.CREATED)
			throw new RuntimeException("Errore nella creazione della nuova voce tramite api esterna!!!");

		Dottore dottoreInserito = dottoreService.inserisciNuovo(dottoreInput.buildDottoreModel());
		return DottoreDTO.buildDottoreDTOFromModel(dottoreInserito, false);
	}

	@PutMapping("/{id}")
	public DottoreDTO update(@Valid @RequestBody DottoreDTO dottoreInput, @PathVariable(required = true) Long id) {
		Dottore dottore = dottoreService.caricaSingoloElemento(id);

		if (dottore == null)
			throw new DottoreNotFoundException("Dottore not found con id: " + id);

		dottoreInput.setId(id);
		Dottore dottoreAggiornato = dottoreService.aggiorna(dottoreInput.buildDottoreModel());
		return DottoreDTO.buildDottoreDTOFromModel(dottoreAggiornato, false);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(required = true) Long id) {
		Dottore dottore = dottoreService.caricaSingoloElemento(id);

		if (dottore == null)
			throw new DottoreNotFoundException("Dottore not found con id: " + id);
		if (dottore.getPazienteAttualmenteInVisita() != null)
			throw new DottoreConPazienteAssociatoException(
					"Il dottore che si sta cercando di eliminare ha un paziente");

		dottoreService.rimuovi(dottore);
	}

	@PostMapping("/search")
	public List<DottoreDTO> search(@RequestBody DottoreDTO example, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "9") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy) {
		return DottoreDTO.createDottoreDTOListFromModelList(dottoreService
				.findByExampleWithPagination(example.buildDottoreModel(), pageNo, pageSize, sortBy).getContent(),
				false);
	}

	@PostMapping("/assegnaPaziente")
	public DottoreDTO assegna(@RequestBody Map<String, String> allParams) {

		String idPaziente = allParams.get("idPaziente");
		String codiceDipendente = allParams.get("codiceDipendente");

		DottoreConBooleaniDTO dottoreCheDovrebbeVisitare = webClient.get().uri("/verifica/" + codiceDipendente)
				.retrieve().bodyToMono(DottoreConBooleaniDTO.class).block();

		System.out.println(idPaziente);

		if (dottoreCheDovrebbeVisitare == null) {
			throw new DottoreNotFoundException("Dottore not found con codice dipendente: " + codiceDipendente);
		}

		if (dottoreCheDovrebbeVisitare.getInVisita() || !dottoreCheDovrebbeVisitare.getInServizio()) {
			throw new DottoreCheNonPuoVisitareIlPazienteCheDicoIoException(
					"Il dottore con codice: " + codiceDipendente + " non può visitare il paziente che dici tu");
		}
		ResponseEntity<DottoreDTO> response = webClient.post().uri("/impostaInVisita").bodyValue(codiceDipendente)
				.retrieve().toEntity(DottoreDTO.class).block();

//		ResponseEntity<DottoreConBooleaniDTO> response = webClient.post().uri("impostaInVisita")
//				.body(dottoreCheDovrebbeVisitare, DottoreConBooleaniDTO.class).retrieve()
//				.toEntity(DottoreConBooleaniDTO.class).block();

		if (response.getStatusCode() != HttpStatus.OK)
			throw new RuntimeException("Errore nell'associazione medicoPaziente!!!");

		return DottoreDTO.buildDottoreDTOFromModel(
				dottoreService.assegnaPaziente(codiceDipendente, Long.parseLong(idPaziente)), true);
	}

}
