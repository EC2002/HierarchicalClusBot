package clustering;

/**
 * Eccezione personalizzata per gestire situazioni in cui il numero di cluster è inferiore a 1.
 * Questa eccezione viene sollevata quando un'operazione di clustering richiede almeno un cluster,
 * ma il numero fornito non soddisfa questo requisito.
 */
public class InvalidClustersNumberException extends Exception{
    /**
     * Costruisce un'istanza di {@code InvalidClustersNumberException} con un messaggio specificato.
     *
     * @param msg Il messaggio di errore descrittivo.
     */
    InvalidClustersNumberException(String msg) {
        super(msg);
    }
}
