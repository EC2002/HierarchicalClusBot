package database;

/**
 * Eccezione personalizzata per gestire errori di connessione al database.
 * Questa eccezione viene sollevata quando il sistema non riesce a stabilire una connessione
 * con il database richiesto, impedendo l'accesso ai dati.
 */
public class DatabaseConnectionException extends Exception {
    /**
     * Costruisce un'istanza di {@code DatabaseConnectionException} con un messaggio specificato.
     *
     * @param msg Il messaggio descrittivo dell'errore di connessione.
     */
    DatabaseConnectionException(String msg) {
        super(msg);
    }
}
