package data;

/**
 * Eccezione personalizzata per gestire situazioni in cui non sono disponibili dati.
 * Questa eccezione viene sollevata quando un dataset o una fonte di dati risulta vuota,
 * impedendo l'esecuzione di operazioni che richiedono dati validi.
 */
public class NoDataException extends Exception {
    /**
     * Costruisce un'istanza di {@code NoDataException} con un messaggio specificato.
     *
     * @param message Il messaggio di errore descrittivo.
     */
    public NoDataException(String message) {
        super(message);
    }
}
