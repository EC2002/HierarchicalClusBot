package server;

import clustering.HierachicalClusterMiner;
import clustering.InvalidClustersNumberException;
import clustering.InvalidDepthException;
import data.Data;
import data.InvalidSizeException;
import data.NoDataException;
import distance.AverageLinkDistance;
import distance.ClusterDistance;
import distance.SingleLinkDistance;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Classe che gestisce la connessione e le richieste di un singolo client al server.
 *
 * Ogni istanza di questa classe viene eseguita su un thread separato per gestire
 * la comunicazione con un client specifico. Supporta il caricamento di dati,
 * l'esecuzione di algoritmi di clustering e la gestione di dendrogrammi.
 */
class ServerOneClient extends Thread {
    /** Socket per la connessione al client. */
    private final Socket clientSocket;
    /** Stream di output per inviare dati al client. */
    private final ObjectOutputStream out;
    /** Stream di input per ricevere dati dal client. */
    private final ObjectInputStream in;
    /** Oggetto per gestire i dati caricati dal database. */
    private Data data;
    /** Mantiene il dendrogramma generato. */
    private HierachicalClusterMiner clustering;
    /** Istanza del bot Telegram. */
    TelegramBotsApi bot;

    /**
     * Costruttore della classe {@link ServerOneClient}.
     * Inizializza gli stream di input e output e avvia il thread.
     *
     * @param socket    Il socket associato al client.
     * @param telegramBot L'istanza del bot Telegram.
     * @throws IOException Se si verifica un errore nella creazione degli stream.
     */
    public ServerOneClient(Socket socket, TelegramBotsApi telegramBot) throws IOException {
        this.clientSocket = socket;
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.bot = telegramBot;
        this.clustering = null; // Inizialmente non c'è un dendrogramma
        this.start(); // Avvia il thread
    }

    /**
     * Metodo principale che gestisce le richieste del client.
     */
    @Override
    public void run() {
        try {
            while (true) {
                int requestType = (int) in.readObject();

                switch (requestType) {
                    case 0:
                        handleLoadData();
                        break;
                    case 1:
                        handleClustering();
                        break;
                    case 2:
                        handleLoadDendrogramFromFile();
                        break;
                    case 3:
                        handleSaveDendrogram();
                        break;
                    default:
                        out.writeObject("Tipo di richiesta non valido");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnessione client: " + clientSocket);
        } catch (ClassNotFoundException e) {
            System.out.println("Errore nella lettura dell'oggetto: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                System.err.println("Errore nella chiusura del socket o degli stream: " + e.getMessage());
            }
        }
    }

    /**
     * Carica i dati dal database specificato dal client.
     * @throws IOException Se si verifica un errore durante la comunicazione con il client.
     * @throws ClassNotFoundException Se si verifica un errore durante la lettura dell'oggetto dal client.
     */
    private void handleLoadData() throws IOException, ClassNotFoundException {
        String tableName = (String) in.readObject();
        try {
            this.data = new Data(tableName); // Carica i dati dal database
            out.writeObject("OK");
        } catch (NoDataException e) {
            out.writeObject(e.getMessage());
        }
    }

    /**
     * Esegue il clustering sui dati caricati.
     * @throws IOException Se si verifica un errore durante la comunicazione con il client.
     * @throws ClassNotFoundException Se si verifica un errore durante la lettura dell'oggetto dal client.
     */
    private void handleClustering() throws IOException, ClassNotFoundException {
        if (data == null) {
            out.writeObject("Dati non caricati");
            return;
        }

        int depth = (int) in.readObject();
        int distanceType = (int) in.readObject();

        try {
            this.clustering = new HierachicalClusterMiner(depth);
            ClusterDistance distance = (distanceType == 1) ? new SingleLinkDistance() : new AverageLinkDistance();
            clustering.mine(data, distance);

            out.writeObject("OK");
            out.writeObject(clustering.toString(data));

        } catch (InvalidSizeException | InvalidClustersNumberException | InvalidDepthException | IllegalArgumentException e) {
            out.writeObject(e.getMessage());
        }
    }


    /**
     * Salva il dendrogramma generato su file.
     * @throws IOException Se si verifica un errore durante la comunicazione con il client.
     * @throws ClassNotFoundException Se si verifica un errore durante la lettura dell'oggetto dal client.
     */
    private void handleSaveDendrogram() throws IOException, ClassNotFoundException {
        String fileName = (String) in.readObject();

        if (clustering == null) {
            out.writeObject("Errore: Nessun dendrogramma disponibile per il salvataggio.");
            return;
        }

        File file = new File(fileName);
        if (file.exists()) {
            out.writeObject("Errore: Il file " + fileName + " esiste già.");
            return;
        }

        try {
            clustering.salva(fileName);
            out.writeObject("OK");
            out.writeObject("Dendrogramma salvato correttamente: " + fileName);
        } catch (IOException e) {
            out.writeObject("Errore durante il salvataggio: " + e.getMessage());
        }
    }


    /**
     * Carica un dendrogramma salvato su file.
     * @throws IOException Se si verifica un errore durante la comunicazione con il client.
     * @throws ClassNotFoundException Se si verifica un errore durante la lettura dell'oggetto dal client.
     */
    private void handleLoadDendrogramFromFile() throws IOException, ClassNotFoundException {
        String fileName = (String) in.readObject();
        try {
            this.clustering = HierachicalClusterMiner.loadHierachicalClusterMiner(fileName);

            if (data == null) {
                out.writeObject("Dati non caricati");
                return;
            }

            if (clustering.getDepth() > data.getNumberOfExample()) {
                out.writeObject("Errore: Il numero di esempi è inferiore alla profondità del dendrogramma!");
            } else {
                out.writeObject("OK");
                out.writeObject(clustering.toString(data));
            }
        } catch (IOException | ClassNotFoundException | InvalidDepthException e) {
            out.writeObject(e.getMessage());
        }
    }
}
