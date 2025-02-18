package database;

/**
 * Eccezione personalizzata lanciata quando un insieme di dati risulta vuoto.
 *
 * Questa eccezione viene utilizzata per segnalare situazioni in cui un'operazione
 * che si aspetta un insieme di dati non riesce a ottenere alcun risultato.
 */
public class EmptySetException extends Exception {
    /**
     * Costruttore che inizializza l'eccezione con un messaggio descrittivo.
     *
     * @param message Messaggio che descrive la causa dell'eccezione.
     */
    public EmptySetException(String message) {
        super(message);
    }
}