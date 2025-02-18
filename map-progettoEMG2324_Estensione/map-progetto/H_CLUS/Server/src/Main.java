import server.MultiServer;

/**
 * Classe principale per l'avvio del server.
 *
 * Questa classe avvia il server sulla porta specificata e inizializza
 * il bot Telegram per la gestione delle comunicazioni.
 *
 * @author Elisa Vittoria Cosmai
 */
public class Main {

    /**
     * Metodo principale per l'avvio del server.
     *
     * Questo metodo stampa un messaggio di avvio e istanzia il server con
     * i parametri specificati.
     *
     * @param args Argomenti passati da terminale (se presenti).
     */
    public static void main( String[] args) {

        String address = "0.0.0.0";
        int port = 8080;
        String botToken = "7813312590:AAHuduBG0hFgjHmN3dbjsVJVxpVSWAlV3q8";

        // Sovrascrive i valori di default di address e port se presenti in args
        if (args.length > 0) {
            address = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Errore: la porta deve essere un numero intero.");
                return;
            }
        }

        // Avvio del server
        System.out.println("Server avviato su " + address + ":" + port);
        MultiServer.instanceMultiServer(botToken, address, port);

    }

}
