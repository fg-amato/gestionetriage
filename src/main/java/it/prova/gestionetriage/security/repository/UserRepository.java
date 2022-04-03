package it.prova.gestionetriage.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.prova.gestionetriage.model.Utente;

public interface UserRepository extends JpaRepository<Utente, Long> {
	Optional<Utente> findByUsername(String username);

}