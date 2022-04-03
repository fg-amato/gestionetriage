package it.prova.gestionetriage.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.prova.gestionetriage.model.Paziente;
import it.prova.gestionetriage.model.StatoPaziente;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PazienteDTO {

	private Long id;

	@NotBlank(message = "{nome.notblank}")
	private String nome;

	@NotBlank(message = "{cognome.notblank}")
	private String cognome;

	@NotBlank(message = "{codiceFiscale.notblank}")
	private String codiceFiscale;

	//@NotNull(message = "{dataRegistrazione.notnull}")
	private Date dataRegistrazione;

	//@NotNull(message = "{statoPaziente.notnull}")
	private StatoPaziente statoPaziente;

	@JsonIgnoreProperties(value = { "dottore" })
	private DottoreDTO dottore;

	public PazienteDTO() {
		super();
	}

	public PazienteDTO(Long id, @NotBlank(message = "{nome.notblank}") String nome,
			@NotBlank(message = "{cognome.notblank}") String cognome,
			@NotBlank(message = "{codiceFiscale.notblank}") String codiceFiscale,
			@NotNull(message = "{dataRegistrazione.notnull}") Date dataRegistrazione,
			@NotNull(message = "{statoPaziente.notnull}") StatoPaziente statoPaziente) {
		super();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.dataRegistrazione = dataRegistrazione;
		this.statoPaziente = statoPaziente;
	}

	public PazienteDTO(Long id, @NotBlank(message = "{nome.notblank}") String nome,
			@NotBlank(message = "{cognome.notblank}") String cognome,
			@NotBlank(message = "{codiceFiscale.notblank}") String codiceFiscale,
			@NotNull(message = "{dataRegistrazione.notnull}") Date dataRegistrazione,
			@NotNull(message = "{statoPaziente.notnull}") StatoPaziente statoPaziente, DottoreDTO dottore) {
		super();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.dataRegistrazione = dataRegistrazione;
		this.statoPaziente = statoPaziente;
		this.dottore = dottore;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public Date getDataRegistrazione() {
		return dataRegistrazione;
	}

	public void setDataRegistrazione(Date dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}

	public StatoPaziente getStatoPaziente() {
		return statoPaziente;
	}

	public void setStatoPaziente(StatoPaziente statoPaziente) {
		this.statoPaziente = statoPaziente;
	}

	public DottoreDTO getDottore() {
		return dottore;
	}

	public void setDottore(DottoreDTO dottore) {
		this.dottore = dottore;
	}

	public Paziente buildPazienteModel() {
		return new Paziente(this.id, this.nome, this.cognome, this.codiceFiscale, this.dataRegistrazione,
				this.statoPaziente);
	}

	public static PazienteDTO buildPazienteDTOFromModel(Paziente pazienteModel, boolean includeDottore) {
		PazienteDTO result = new PazienteDTO(pazienteModel.getId(), pazienteModel.getNome(), pazienteModel.getCognome(),
				pazienteModel.getCodiceFiscale(), pazienteModel.getDataRegistrazione(),
				pazienteModel.getStatoPaziente());

		if (includeDottore && pazienteModel.getDottore() != null)
			result.setDottore(DottoreDTO.buildDottoreDTOFromModel(pazienteModel.getDottore(), false));

		return result;
	}

	public static List<PazienteDTO> createPazienteDTOListFromModelList(List<Paziente> modelListInput,
			boolean includeDottore) {
		return modelListInput.stream().map(pazienteEntity -> {
			return PazienteDTO.buildPazienteDTOFromModel(pazienteEntity, includeDottore);
		}).collect(Collectors.toList());
	}

	public static Set<PazienteDTO> createPazienteDTOSetFromModelSet(Set<Paziente> modelListInput,
			boolean includeDottore) {
		return modelListInput.stream().map(pazienteEntity -> {
			return PazienteDTO.buildPazienteDTOFromModel(pazienteEntity, includeDottore);
		}).collect(Collectors.toSet());
	}

}
