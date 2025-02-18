package data;

import database.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * La classe Data rappresenta un dataset come una lista di oggetti {@link Example}.
 * I dati possono essere inizializzati leggendo da una tabella specificata in un database.
 */
public class Data {
    /**
     * Lista di esempi che costituisce il dataset.
     */
    private final List<Example> data = new ArrayList<>(); // rappresenta il dataset

    /**
     * Costruisce un oggetto Data inizializzando il dataset con i dati prelevati da una tabella del database.
     *
     * @param tableName Nome della tabella da cui leggere i dati.
     * @throws NoDataException Se la tabella è vuota o si verifica un errore durante il recupero dei dati.
     */
    public Data(String tableName) throws NoDataException {
        DbAccess dbAccess = new DbAccess();
        try {
            TableData tableData = new TableData(dbAccess);
            List<Example> examples = tableData.getDistinctTransazioni(tableName);
            this.data.addAll(examples);
        } catch (DatabaseConnectionException e) {
            throw new NoDataException("Errore di connessione al database: " + e.getMessage() + "\n");
        } catch (EmptySetException e) {
            throw new NoDataException("La tabella " + tableName + " è vuota: " + e.getMessage() + "\n");
        } catch (MissingNumberException e) {
            throw new NoDataException("Eccezione durante l'elaborazione dei dati: " + e.getMessage() + "\n");
        } catch (SQLException e) {
            throw new NoDataException("Errore SQL durante il recupero dei dati dalla tabella. \n");
        }
    }

    /**
     * Restituisce il numero di esempi presenti nel dataset.
     *
     * @return Numero di esempi nel dataset.
     */
    public int getNumberOfExample() {
        return data.size();
    }

    /**
     * Restituisce un esempio specifico dal dataset dato un indice.
     *
     * @param exampleIndex Indice dell'esempio da recuperare.
     * @return L'oggetto {@link Example} corrispondente all'indice specificato.
     */
    public Example getExample(int exampleIndex) {
        return data.get(exampleIndex);
    }

    /**
     * Restituisce un iteratore per scorrere gli esempi contenuti nel dataset.
     *
     * @return Un iteratore sugli elementi della lista {@code data}.
     */
    public Iterator<Example> iterator() {
        return data.iterator();
    }

    /**
     * Restituisce una rappresentazione testuale del dataset, con enumerazione degli esempi e dei loro valori.
     *
     * @return Una stringa che rappresenta il dataset.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        Iterator<Example> iterator = iterator();
        int count = 0;

        while (iterator.hasNext()) {
            s.append(count++).append(":[").append(iterator.next().toString()).append("]\n");
        }

        return s.toString();
    }
}

