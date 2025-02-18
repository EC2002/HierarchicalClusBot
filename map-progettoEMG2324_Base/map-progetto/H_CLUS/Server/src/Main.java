import server.MultiServer;

/**
 * Classe principale per l'avvio del server.
 *
 * Questa classe avvia il server sulla porta specificata e inizializza
 * il bot Telegram per la gestione delle comunicazioni.
 */
public class Main {

    /**
     * Questo metodo stampa un messaggio di avvio e istanzia il server con
     * i parametri specificati.
     * @param args vettore dei parametri
     */
    public static void main( String[] args) {

        int port= 8080;

        System.out.println("Server avviato sulla porta " + port);
        MultiServer.instanceMultiServer(port);
    }
}

