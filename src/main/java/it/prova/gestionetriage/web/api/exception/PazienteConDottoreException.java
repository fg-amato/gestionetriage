package it.prova.gestionetriage.web.api.exception;

public class PazienteConDottoreException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PazienteConDottoreException(String m) {
		super(m);
	}
}
