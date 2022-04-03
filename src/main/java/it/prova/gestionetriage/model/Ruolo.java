package it.prova.gestionetriage.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;

@Entity
@Table(name = "ruolo")
public class Ruolo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "nome", length = 50)
	@NotNull
	@Enumerated(EnumType.STRING)
	private RuoloName nome;

	@ManyToMany(mappedBy = "ruoli", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<Utente> utenti = new ArrayList<>();

	public Ruolo() {
		super();
	}

	public Ruolo(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RuoloName getNome() {
		return nome;
	}

	public void setName(RuoloName nome) {
		this.nome = nome;
	}

	public List<Utente> getUtenti() {
		return utenti;
	}

	public void setUsers(List<Utente> utenti) {
		this.utenti = utenti;
	}
}
