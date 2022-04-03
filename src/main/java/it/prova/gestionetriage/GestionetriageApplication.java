package it.prova.gestionetriage;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.prova.gestionetriage.model.Ruolo;
import it.prova.gestionetriage.model.RuoloName;
import it.prova.gestionetriage.model.StatoUtente;
import it.prova.gestionetriage.model.Utente;
import it.prova.gestionetriage.security.repository.AuthorityRepository;
import it.prova.gestionetriage.security.repository.UserRepository;
import it.prova.gestionetriage.service.DottoreService;
import it.prova.gestionetriage.service.PazienteService;

@SpringBootApplication
public class GestionetriageApplication {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthorityRepository authorityRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(GestionetriageApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase(DottoreService dottoreService, PazienteService pazienteService) {
		return (args) -> {

			// inizializzo il Db

			// Ora la parte di sicurezza
			Utente user = userRepository.findByUsername("admin").orElse(null);

			if (user == null) {

				/**
				 * Inizializzo i dati del mio test
				 */

				Ruolo authorityAdmin = new Ruolo();
				authorityAdmin.setName(RuoloName.ROLE_ADMIN);
				authorityAdmin = authorityRepository.save(authorityAdmin);

				Ruolo authoritySubOperator = new Ruolo();
				authoritySubOperator.setName(RuoloName.ROLE_SUB_OPERATOR);
				authoritySubOperator = authorityRepository.save(authoritySubOperator);

				List<Ruolo> authorities = Arrays.asList(new Ruolo[] { authorityAdmin, authoritySubOperator });

				user = new Utente();
				user.setRuoli(authorities);
				user.setStato(StatoUtente.ATTIVO);
				user.setUsername("admin");
				user.setPassword(passwordEncoder.encode("admin"));

				user = userRepository.save(user);

			}

			Utente commonUtente = userRepository.findByUsername("commonUtente").orElse(null);

			if (commonUtente == null) {

				/**
				 * Inizializzo i dati del mio test
				 */

				Ruolo authorityUtente = authorityRepository.findByNome(RuoloName.ROLE_SUB_OPERATOR);
				if (authorityUtente == null) {
					authorityUtente = new Ruolo();
					authorityUtente.setName(RuoloName.ROLE_SUB_OPERATOR);
					authorityUtente = authorityRepository.save(authorityUtente);
				}

				List<Ruolo> authorities = Arrays.asList(new Ruolo[] { authorityUtente });

				commonUtente = new Utente();
				commonUtente.setRuoli(authorities);
				commonUtente.setStato(StatoUtente.ATTIVO);
				commonUtente.setUsername("commonUtente");
				commonUtente.setPassword(passwordEncoder.encode("commonUtente"));

				commonUtente = userRepository.save(commonUtente);

			}
		};
	}

}
