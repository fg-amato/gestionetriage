package it.prova.gestionetriage.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.prova.gestionetriage.model.Ruolo;
import it.prova.gestionetriage.model.RuoloName;

public interface AuthorityRepository extends JpaRepository<Ruolo, Long> {
	Ruolo findByNome(RuoloName name);

}