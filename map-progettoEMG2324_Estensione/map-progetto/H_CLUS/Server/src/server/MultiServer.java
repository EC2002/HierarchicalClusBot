package server;

import estensione.TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe che gestisce il server principale utilizzando un approccio multi-threaded.
 *
 * Il server accetta connessioni su una porta specificata e gestisce ogni client
 * in un thread separato. Inoltre, permette l'integrazione con un bot Telegram.
 *
 * @author Elisa Vittoria Cosmai
 */
public class MultiServer {
    /** Porta su cui il server accetta connessioni. */
    private final int PORT;
    /** Istanza Singleton della classe MultiServer. */
    private static MultiServer singleton = null;
    /** Istanza di {@link TelegramBotsApi} per la gestione del bot Telegram. */
    private static TelegramBotsApi telegramBot;

    /**
     * Costruttore privato della classe, inizializza la porta e avvia il server.
     *
     * @param port La porta su cui il server sarà in ascolto.
     */
    private MultiServer(int port) {
        this.PORT = port;
        run();
    }

    /**
     * Metodo statico per ottenere un'istanza di {@link MultiServer}.
     * Garantisce che la classe venga istanziata una sola volta (Singleton).
     *
     * @param token   Il token del bot Telegram.
     * @param address L'indirizzo del server.
     * @param port    La porta su cui avviare il server.
     */
    public static void instanceMultiServer(String token, String address, int port) {
        try {
            telegramBot = new TelegramBotsApi(DefaultBotSession.class);
            telegramBot.registerBot(new TelegramBot(token, address, port));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        singleton = new MultiServer(port);
    }

    /**
     * Metodo principale che avvia il server.
     *
     * Crea un {@link ServerSocket} per accettare connessioni dai client.
     * Ogni connessione viene gestita in un thread separato utilizzando {@link ServerOneClient}.
     */
    private void run() {
        ServerSocket s = null;
        try {
            s = new ServerSocket(this.PORT);
            System.out.println("Started: " + s);
            while(true) {
                Socket socket = s.accept();
                System.out.println("Connessione client: " + socket);
                try {
                    new ServerOneClient(socket, telegramBot);
                } catch (IOException e) {
                    System.out.println("Errore nella creazione del socket: " + socket);
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante l'avvio del server: " + e.getMessage());
        } finally {
            if(s != null && !s.isClosed()) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("Errore nella chiusura del socket: " + e.getMessage());
                }
            }
        }
    }

}
