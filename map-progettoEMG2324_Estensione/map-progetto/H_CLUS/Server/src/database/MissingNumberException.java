package database;

/**
 * Eccezione personalizzata lanciata quando un numero richiesto è mancante.
 *
 * Questa eccezione viene utilizzata per indicare situazioni in cui un valore numerico
 * atteso non è presente, causando un errore nel flusso dell'applicazione.
 */
public class MissingNumberException extends Exception {
    /**
     * Costruttore che inizializza l'eccezione con un messaggio descrittivo.
     *
     * @param message Messaggio che descrive la causa dell'eccezione.
     */
    public MissingNumberException(String message) {
        super(message);
    }
}
