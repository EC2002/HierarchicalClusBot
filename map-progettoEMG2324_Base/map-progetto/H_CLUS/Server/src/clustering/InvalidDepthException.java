package clustering;

/**
 * Eccezione personalizzata per gestire situazioni in cui la profondità di un dendrogramma
 * è minore o uguale a zero. Questa eccezione viene sollevata quando un'operazione
 * richiede una profondità valida ma il valore fornito non soddisfa il requisito.
 */
public class InvalidDepthException extends Exception{
    /**
     * Costruisce un'istanza di {@code InvalidDepthException} con un messaggio specificato.
     *
     * @param msg Il messaggio di errore descrittivo.
     */
    InvalidDepthException(String msg) {
        super(msg);
    }
}
