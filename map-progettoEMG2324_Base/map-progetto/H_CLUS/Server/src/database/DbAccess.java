package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe che gestisce l'accesso al database per la lettura dei dati di training.
 * Fornisce metodi per inizializzare, ottenere e chiudere una connessione al database.
 *
 * Utilizza il driver MySQL JDBC per stabilire la connessione con un database specificato.
 *
 * @author Elisa Vittoria Cosmai
 */
public class DbAccess {
    /** Nome del driver da utilizzare */
    private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    /** Nome del DBMS da utilizzare */
    private final String DBMS = "jdbc:mysql";
    /** Indirizzo del server da utilizzare */
    private final String SERVER = "localhost";
    /** Nome del database da utilizzare */
    private final String DATABASE = "Mapdb";
    /** Porta del server da utilizzare */
    private final int PORT = 3306;
    /** Nome utente per l'accesso al database */
    private final String USER_ID = "MapUser";
    /** Password per l'accesso al database */
    private final String PASSWORD = "map";
    /** Connessione al database */
    private Connection conn;

    /**
     * Inizializza una connessione al database caricando il driver JDBC e stabilendo la connessione.
     *
     * @throws DatabaseConnectionException Se si verifica un errore durante il caricamento del driver
     *                                     o la connessione al database.
     */
    public void initConnection() throws DatabaseConnectionException
    {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch(ClassNotFoundException e) {
            System.out.println("[!] Driver not found: " + e.getMessage());
            throw new DatabaseConnectionException(e.toString());
        }
        String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
                + "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";


        try {
            conn = DriverManager.getConnection(connectionString);
        } catch(SQLException e) {
            throw new DatabaseConnectionException(e.toString());
        }
    }

    /**
     * Restituisce la connessione attiva al database. Se la connessione non è già inizializzata,
     * viene creata automaticamente tramite {@link #initConnection()}.
     *
     * @return Oggetto {@link Connection} rappresentante la connessione al database.
     * @throws DatabaseConnectionException Se si verifica un errore durante l'inizializzazione della connessione.
     */
    public Connection getConnection() throws DatabaseConnectionException{
        this.initConnection();
        return conn;
    }

    /**
     * Chiude la connessione al database, se attiva.
     *
     * @throws SQLException Se si verifica un errore durante la chiusura della connessione.
     */
    public void closeConnection() throws SQLException {
        conn.close();
    }

}
