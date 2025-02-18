import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * La classe MainTest gestisce la comunicazione con un server remoto.
 * Permette di inviare richieste al server, ricevere risposte e
 * interagire tramite un menu per selezionare diverse operazioni.
 */
public class MainTest {
    /** Stream per inviare richieste al server */
    private final ObjectOutputStream out;
    /** Stream per ricevere risposte dal server */
    private final ObjectInputStream in;

    /**
     * Costruttore della classe MainTest.
     * Inizializza la connessione al server tramite socket e flussi di input/output.
     *
     * @param ip Indirizzo IP del server a cui connettersi.
     * @param port Porta del server per la connessione.
     * @throws IOException Se si verificano errori di input/output durante la connessione.
     */
    public MainTest(String ip, int port) throws IOException {
        InetAddress addr = InetAddress.getByName(ip); // Indirizzo IP
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, port); // Porta
        System.out.println(socket);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }



    /**
     * Invia al server una richiesta per caricare i dati da una tabella del database.
     * Continua a richiedere l'inserimento del nome della tabella fino a quando il server conferma la corretta ricezione.
     *
     * @throws IOException Se si verificano errori di input/output durante la comunicazione.
     * @throws ClassNotFoundException Se la risposta ricevuta dal server non può essere interpretata correttamente.
     */
    private void loadDataOnServer() throws IOException, ClassNotFoundException {
        boolean flag = false;
        do {
            System.out.println("Inserisci il nome della tabella: (exampletab)");
            String tableName = Keyboard.readString();
            out.writeObject(0);
            out.writeObject(tableName);
            String risposta = (String) (in.readObject());
            if (risposta.equals("OK"))
                flag = true;
            else System.out.println(risposta);
        } while (!flag);
    }

    /**
     * Mostra un menu all'utente per selezionare un'opzione e legge l'input.
     *
     * @return Il numero dell'opzione selezionata dall'utente.
     */
    private int menu() {
        int answer;
        System.out.println("Scegli una delle due opzioni:");
        do {
            System.out.println("1. Carica il dendrogramma da file");
            System.out.println("2. Apprendi il dendrogramma dal database");
            System.out.print("Risposta: ");
            answer = Keyboard.readInt();
        } while (answer <= 0 || answer > 2);
        return answer;
    }

    /**
     * Invia una richiesta al server per caricare un dendrogramma da un file.
     * L'utente deve specificare il nome del file con una delle estensioni supportate.
     *
     * @throws IOException Se si verificano errori di input/output durante la comunicazione.
     * @throws ClassNotFoundException Se la risposta ricevuta dal server non può essere interpretata correttamente.
     */
    private void loadDendrogramFromFileOnServer() throws IOException, ClassNotFoundException {
        System.out.println("Inserisci il nome del file con una di queste estensioni: \n.txt, .csv, .json, .xml, .dat, .bin, .ser");
        String fileName = Keyboard.readString();

        out.writeObject(2);
        out.writeObject(fileName);
        String risposta = (String) (in.readObject());
        if (risposta.equals("OK"))
            System.out.println(in.readObject()); // Stampa il dendrogramma inviato dal server
        else
            System.out.println(risposta); // Stampa un messaggio di errore
    }

    /**
     * Invia al server una richiesta per apprendere un dendrogramma.
     * @throws IOException Se si verificano errori di input/output durante la comunicazione.
     * @throws ClassNotFoundException Se la risposta ricevuta dal server non può essere interpretata correttamente.
     */
    private void mineDendrogramOnServer() throws ClassNotFoundException, IOException {
        out.writeObject(1);
        System.out.println("Inserisci la profondità' del dendrogramma");
        int depth=Keyboard.readInt();
        out.writeObject(depth);
        int dType=-1;
        do {
            System.out.println("Scegli il tipo di distanza: \n1. single-link \n2. average-link:");
            dType=Keyboard.readInt();
        }while (dType<=0 || dType>2);
        out.writeObject(dType);

        String risposta= (String) (in.readObject());
        if(risposta.equals("OK")) {
            System.out.println(in.readObject()); // stampo il dendrogramma che il server mi sta inviando
            System.out.println("Inserisci il nome del file con una di queste estensioni: \n.txt, .csv, .json, .xml, .dat, .bin, .ser");
            String fileName=Keyboard.readString();
            out.writeObject(fileName);
            risposta = (String) in.readObject();
            System.out.println(risposta);
        }
        else
            System.out.println(risposta); // stampo il messaggio di errore
    }


    /**
     * Restituisce il flusso di output verso il server.
     * @return out Il flusso di output verso il server.
     */
    public ObjectOutputStream getOut() {
        return out;
    }

    /**
     * Restituisce il flusso di input dal server.
     * @return in Il flusso di input dal server.
     */
    public ObjectInputStream getIn() {
        return in;
    }

    /**
     * Chiude i flussi di input/output.
     *
     */
    public void closeStreams(){
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Metodo principale per l'avvio del programma.
     * Stabilisce una connessione con il server utilizzando un'istanza della classe MainTest
     * e gestisce le operazioni principali tramite un menu interattivo.
     * L'utente può scegliere tra diverse operazioni, come caricare un dendrogramma
     * da file o apprenderlo da un database.
     *
     * @param args Parametri opzionali che possono specificare l'indirizzo IP e la porta del server (pre-impostati nel codice).
     */
    public static void main(String[] args) {
        String ip = "0.0.0.0";
        int port = 8080;
        MainTest main = null;
        try {
            main = new MainTest(ip, port);

            main.loadDataOnServer();
            int scelta = main.menu();
            if (scelta == 1)
                main.loadDendrogramFromFileOnServer();
            else
                main.mineDendrogramOnServer();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if(main != null) {
                main.closeStreams();
            }
        }
    }
}
