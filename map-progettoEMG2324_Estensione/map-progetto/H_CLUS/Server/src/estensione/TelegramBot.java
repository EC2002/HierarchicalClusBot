package estensione;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import database.DbAccess;
import database.DatabaseConnectionException;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Classe che gestisce la comunicazione tra un utente di Telegram e un server.
 *
 * Il bot consente agli utenti di interagire con un server per l'elaborazione e la costruzione di dendrogrammi,
 * fornendo funzionalità per avviare sessioni, caricare dati, specificare parametri e salvare i risultati.
 *
 * @author Elisa Vittoria Cosmai
 */
public class TelegramBot extends TelegramLongPollingBot {
    /** Token del bot per l'autenticazione. */
    private final String botToken;
    /** Indirizzo IP del server. */
    private final String serverIp;
    /** Porta del server. */
    private final int serverPort;
    /** Mappa che associa gli utenti Telegram alle loro sessioni attive. */
    private final Map<String, ClientSession> userSessions = new HashMap<>();

    /**
     * Costruttore per la classe TelegramBot.
     *
     * @param botToken  Token del bot per l'autenticazione.
     * @param serverIp  Indirizzo IP del server.
     * @param serverPort Porta del server.
     */
    public TelegramBot(String botToken, String serverIp, int serverPort) {
        this.botToken = botToken;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * Restituisce il nome utente del bot.
     *
     * @return Nome utente del bot.
     */
    public String getBotUsername() {
        return "HierarchicalClusBot";
    }

    /**
     * Restituisce il token del bot per l'autenticazione con l'API di Telegram.
     *
     * @return Token del bot.
     */
    public String getBotToken() {
        return this.botToken;
    }

    /**
     * Metodo chiamato automaticamente quando il bot riceve un aggiornamento da Telegram.
     *
     * @param update Oggetto {@link Update} contenente informazioni sul messaggio ricevuto.
     */
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String receivedMessage = update.getMessage().getText();

            try {
                switch (receivedMessage) {
                    case "/end" -> {
                        if (this.userSessions.containsKey(chatId)) {
                            this.closeSession(chatId);
                            this.sendMessage(chatId, "Sessione terminata! Scrivi /start per iniziare una nuova sessione.");
                        } else {
                            this.sendMessage(chatId, "Nessuna sessione attiva. Scrivi /start per iniziare una nuova sessione.");
                        }
                    }
                    case "/start" -> this.handleMessage(chatId, receivedMessage);
                    default -> {
                        if(this.userSessions.containsKey(chatId)) {
                            this.handleMessage(chatId, receivedMessage);
                        } else {
                            this.sendMessage(chatId, "Comando non valido. Scrivi /start per iniziare una nuova sessione.");
                        }
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Legge il contenuto di un file specificato e lo invia all'utente tramite Telegram.
     *
     * @param chatId   ID della chat utente.
     * @param filePath Percorso del file da leggere e inviare.
     */
    private void sendFileContent(String chatId, String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String content = String.join("\n", lines);
            this.sendMessage(chatId, content);
        } catch (IOException e) {
            e.printStackTrace();
            this.sendMessage(chatId, "Errore durante la lettura del file.");
        }
    }

    /**
     * Gestisce i messaggi ricevuti dall'utente e aggiorna lo stato della sessione.
     *
     * @param chatId          ID della chat utente.
     * @param receivedMessage Testo del messaggio ricevuto.
     * @throws IOException            In caso di errore di input/output.
     * @throws ClassNotFoundException In caso di classe non trovata durante l'elaborazione.
     */
    private void handleMessage(String chatId, String receivedMessage) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        String infoFilePath = System.getProperty("user.dir") + "/info.txt";
        if (session.state == null) {
            session.state = "START"; // Stato iniziale della sessione.
        }

        switch (session.state) {
            case "START":
                if (receivedMessage.equals("/start")) {
                    this.sendFileContent(chatId, infoFilePath);
                    session.out.writeObject(0);
                    List<String> tableNames = getTableNames();
                    this.sendMessage(chatId,"Tabelle disponibili nel database:\n" + String.join("\n- ", tableNames + "\n\nInserisci il nome della tabella del database da caricare."));
                    session.state = "LOAD_DATA"; // Aggiorna lo stato della sessione.
                }
                break;
            case "MENU":
                if (receivedMessage.equals("1")) {
                    session.out.writeObject(2);
                    this.sendMessage(chatId, "Inserisci il nome del file con una di queste estensioni: \n.txt, .csv, .json, .xml, .dat, .bin, .ser");
                    session.state = "LOAD_FILE";
                } else if (receivedMessage.equals("2")) {
                    session.out.writeObject(1);
                    this.sendMessage(chatId, "Inserisci la profondità del dendrogramma (da 1 a 5):");
                    session.state = "ENTER_DEPTH";
                } else {
                    this.sendMessage(chatId, "Scelta non valida. Scegli una opzione:\n1. Carica Dendrogramma da File\n2. Apprendi Dendrogramma da Database");
                    session.state = "MENU";
                }
                break;
            case "SAVE_FILE":
                session.out.writeObject(3);
                this.handleSaveFile(chatId, receivedMessage);
                break;
            case "LOAD_FILE":
                this.handleLoadDendrogramFromFile(chatId, receivedMessage);
                break;
            case "LOAD_DATA":
                this.handleLoadData(chatId, receivedMessage);
                break;
            case "ENTER_DEPTH":
                this.handleDepth(chatId, receivedMessage);
                break;
            case "ENTER_DISTANCE":
                this.handleDistance(chatId, receivedMessage);
                break;
            default:
                this.sendMessage(chatId, "Comando non valido.");
        }
    }

    /**
     * Invia un messaggio all'utente tramite l'API Telegram.
     *
     * @param chatId ID della chat utente.
     * @param text   Testo del messaggio da inviare.
     */
    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il caricamento di un dendrogramma da un file.
     * @param chatId ID della chat utente.
     * @param fileName Nome del file da cui caricare il dendrogramma.
     * @throws IOException In caso di errore durante la comunicazione con il server.
     * @throws ClassNotFoundException In caso di mancata corrispondenza dei dati ricevuti.
     */
    private void handleLoadDendrogramFromFile(String chatId, String fileName) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(fileName);
        String risposta = (String) session.in.readObject();
        if (risposta.equals("OK")) {
            this.sendMessage(chatId, (String) session.in.readObject());
            this.sendMessage(chatId, "Sessione terminata! Scrivi /start per iniziare una nuova sessione.");
            this.closeSession(chatId);
            session.state = "START";
        } else {
            this.sendMessage(chatId, risposta);
            this.sendMessage(chatId, "Inserisci il nome del file con una di queste estensioni: \n.txt, .csv, .json, .xml, .dat, .bin, .ser");
            session.out.writeObject(2);
            session.state = "LOAD_FILE";
        }
    }

    /**
     * Gestisce il caricamento dei dati per la costruzione del dendrogramma da un database.
     *
     * @param chatId    L'ID della chat.
     * @param tableName Il nome della tabella del database da cui caricare i dati.
     * @throws IOException              Se si verifica un errore di I/O.
     * @throws ClassNotFoundException   Se una classe non viene trovata.
     */
    private void handleLoadData(String chatId, String tableName) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(tableName);
        String risposta = (String) session.in.readObject();
        if (risposta.equals("OK")) {
            this.sendMessage(chatId, "Scegli una opzione:\n1. Carica Dendrogramma da File\n2. Apprendi Dendrogramma da Database");
            session.state = "MENU";
        } else {
            this.sendMessage(chatId, risposta);
            this.sendMessage(chatId, "Inserisci il nome della tabella del database da caricare.");
            session.out.writeObject(0);
            session.state = "LOAD_DATA";
        }
    }

    /**
     * Gestisce l'inserimento della profondità del dendrogramma.
     *
     * @param chatId    L'ID della chat.
     * @param depthStr  La stringa rappresentante la profondità del dendrogramma.
     * @throws IOException Se si verifica un errore di I/O.
     */
    private void handleDepth(String chatId, String depthStr) throws IOException {
        int depth;
        try {
            depth = Integer.parseInt(depthStr);
        } catch (NumberFormatException e) {
            this.sendMessage(chatId, "Profondità non valida.\n Inserisci la profondità del dendrogramma (da 1 a 5):");
            return;
        }
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(depth);
        this.sendMessage(chatId, "Scegli il tipo di distanza:\n1. Single-link\n2. Average-link");
        session.state = "ENTER_DISTANCE";
    }

    /**
     * Gestisce la scelta del tipo di distanza per la costruzione del dendrogramma.
     *
     * @param chatId       L'ID della chat.
     * @param distanceStr  La stringa rappresentante la scelta del tipo di distanza.
     * @throws IOException              Se si verifica un errore di I/O.
     * @throws ClassNotFoundException   Se una classe non viene trovata.
     */
    private void handleDistance(String chatId, String distanceStr) throws IOException, ClassNotFoundException {
        int distance;
        try {
            distance = Integer.parseInt(distanceStr);
        } catch (NumberFormatException e) {
            this.sendMessage(chatId, "Scelta non valida. Scegli una opzione:\n1. Single-link\n2. Average-link");
            return;
        }
        if (distance != 1 && distance != 2) {
            this.sendMessage(chatId, "Scelta non valida. Scegli una opzione:\n1. Single-link\n2. Average-link");
            return;
        }
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(distance);
        String risposta = (String) session.in.readObject();
        if (risposta.equals("OK")) {
            this.sendMessage(chatId, (String) session.in.readObject());
            this.sendMessage(chatId, "Inserisci il nome del file con una di queste estensioni: \n.txt, .csv, .json, .xml, .dat, .bin, .ser");
            session.state = "SAVE_FILE";

        } else {
            this.sendMessage(chatId, risposta);
            session.out.writeObject(1);
            this.sendMessage(chatId, "Inserisci la profondità del dendrogramma (da 1 a 5):");
            session.state = "ENTER_DEPTH";
        }
    }

    /**
     * Gestisce il salvataggio del file contenente il dendrogramma generato.
     *
     * @param chatId   ID della chat utente.
     * @param fileName Nome del file in cui salvare il dendrogramma.
     * @throws IOException            In caso di errore durante la comunicazione con il server.
     * @throws ClassNotFoundException In caso di mancata corrispondenza dei dati ricevuti.
     */
    private void handleSaveFile(String chatId, String fileName) throws IOException, ClassNotFoundException {
        ClientSession session = this.getSession(chatId);
        session.out.writeObject(fileName);
        String risposta = (String) session.in.readObject();
        if (risposta.equals("OK")) {
            this.sendMessage(chatId, (String) session.in.readObject());
            this.sendMessage(chatId, "Sessione terminata! Scrivi /start per iniziare una nuova sessione.");
            this.closeSession(chatId);
            session.state = "START";
        } else {
            this.sendMessage(chatId, risposta);
            this.sendMessage(chatId, "Inserisci il nome del file con una di queste estensioni: \n.txt, .csv, .json, .xml, .dat, .bin, .ser");
            session.state = "SAVE_FILE";
        }
    }

    /**
     * Ottiene la sessione attiva per una specifica chat.
     *
     * @param chatId L'ID della chat.
     * @return La sessione associata alla chat.
     * @throws IOException Se si verifica un errore di I/O.
     */
    private ClientSession getSession(String chatId) throws IOException {
        if (!this.userSessions.containsKey(chatId)) {
            ClientSession session = new ClientSession(this.serverIp, this.serverPort);
            this.userSessions.put(chatId, session);
        }
        return this.userSessions.get(chatId);
    }

    /**
     * Chiude la sessione per una specifica chat.
     *
     * @param chatId L'ID della chat.
     * @throws IOException Se si verifica un errore di I/O.
     */
    private void closeSession(String chatId) throws IOException {
        ClientSession session = this.userSessions.remove(chatId);
        if (session != null) {
            session.out.close();
            session.in.close();
            session.socket.close();
        }
    }

    /**
     * Recupera i nomi delle tabelle disponibili nel database collegato.
     *
     * @return Lista dei nomi delle tabelle disponibili nel database.
     */
    private List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        DbAccess dbAccess = new DbAccess();
        try {
            dbAccess.initConnection();
            DatabaseMetaData meta = dbAccess.getConnection().getMetaData();
            ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
            dbAccess.closeConnection();
        } catch (DatabaseConnectionException | SQLException e) {
            e.printStackTrace();
        }


        return tableNames;
    }

    /**
     * Classe interna che rappresenta la sessione del client con il server.
     */
    static class ClientSession {
        /** Socket per la connessione con il server. */
        Socket socket;
        /** Stream di output per inviare dati al server. */
        ObjectOutputStream out;
        /** Stream di input per ricevere dati dal server. */
        ObjectInputStream in;
        /** Stato corrente della sessione. */
        String state;

        /**
         * Costruttore per la classe ClientSession.
         *
         * @param serverIp   Indirizzo IP del server.
         * @param serverPort Porta del server.
         * @throws IOException In caso di errore di connessione.
         */
        public ClientSession(String serverIp, int serverPort) throws IOException {
            this.socket = new Socket(InetAddress.getByName(serverIp), serverPort);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.state = null;
        }
    }
}