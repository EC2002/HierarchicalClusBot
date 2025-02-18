package database;

import data.Example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsabile della gestione dell'accesso ai dati di una tabella del database.
 *
 * Questa classe fornisce metodi per recuperare e gestire i dati contenuti in una tabella,
 * garantendo l'integrità e il corretto trattamento degli errori.
 */
public class TableData {
    /**
     * Oggetto {@link DbAccess} utilizzato per gestire la connessione al database.
     */
    private final DbAccess db;

    /**
     * Costruttore che inizializza la connessione al database.
     *
     * @param db Oggetto {@link DbAccess} che gestisce la connessione al database.
     */
    public TableData(DbAccess db) {
        this.db = db;
    }

    /**
     * Recupera le transazioni distinte dalla tabella specificata.
     *
     * Questo metodo esegue una query sulla tabella specificata e restituisce un elenco di oggetti
     * {@link Example} che rappresentano le transazioni univoche.
     *
     * @param table Nome della tabella da cui recuperare i dati.
     * @return Lista di oggetti {@link Example} contenenti i dati delle transazioni distinte.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     * @throws EmptySetException Se la tabella non contiene alcun dato.
     * @throws MissingNumberException Se uno degli attributi della tabella non è di tipo numerico.
     * @throws DatabaseConnectionException Se si verifica un problema nella connessione al database.
     */
    public List<Example> getDistinctTransazioni(String table) throws SQLException, EmptySetException, MissingNumberException, DatabaseConnectionException {
        List<Example> transazioni = new ArrayList<>();
        Connection con = db.getConnection();
        Statement stmt;
        ResultSet rs;
        TableSchema schema = new TableSchema(db, table);

        stmt = con.createStatement();
        String query = "SELECT DISTINCT * FROM " + table;
        rs = stmt.executeQuery(query);

        if (!rs.isBeforeFirst()) { // Verifica se il ResultSet è vuoto
            throw new EmptySetException("La tabella " + table + " è vuota.\n");
        }

        while (rs.next()) {
            Example example = new Example();
            for (int i = 0; i < schema.getNumberOfAttributes(); i++) {
                TableSchema.Column column = schema.getColumn(i);
                if (!column.isNumber()) {
                    throw new MissingNumberException("Attributo non numerico trovato: " + column.getColumnName() + "\n");
                }
                example.add(rs.getDouble(column.getColumnName()));
            }
            transazioni.add(example);
        }

        rs.close();
        stmt.close();
        db.closeConnection();

        return transazioni;
    }
}

