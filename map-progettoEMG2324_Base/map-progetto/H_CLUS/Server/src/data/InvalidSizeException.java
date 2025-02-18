package data;

/**
 * Eccezione personalizzata per gestire situazioni in cui la dimensione di un insieme di cluster è inferiore a 1.
 * Questa eccezione viene sollevata quando un'operazione di clustering richiede un insieme con almeno un elemento,
 * ma la dimensione fornita non soddisfa il requisito minimo.
 */
public class InvalidSizeException extends Exception{
    /**
     * Costruisce un'istanza di {@code InvalidSizeException} con un messaggio specificato.
     *
     * @param message Il messaggio di errore descrittivo.
     */
    public InvalidSizeException(String message){
        super(message);
    }
}
