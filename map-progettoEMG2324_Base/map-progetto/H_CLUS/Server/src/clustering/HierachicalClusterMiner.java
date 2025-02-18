package clustering;

import data.Data;
import data.InvalidSizeException;
import distance.ClusterDistance;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

/**
 * La classe HierarchicalClusterMiner gestisce il processo di clustering gerarchico
 * e consente di creare, salvare e caricare un dendrogramma.
 *
 * Utilizza un'istanza di {@link Dendrogram} per rappresentare la gerarchia dei cluster.
 *
 * Implementa {@link Serializable} per supportare la serializzazione del modello.
 *
 * @author Elisa Vittoria Cosmai
 */
public class HierachicalClusterMiner implements Serializable {
	/**
	 * Dendrogramma che rappresenta la gerarchia dei cluster generati dal processo di clustering.
	 */
	private final Dendrogram dendrogram;

	/**
	 * Percorso della directory utilizzata per salvare e caricare i modelli serializzati.
	 */
	private static final String DIRECTORY_PATH = "./saved/";

	/**
	 * Costruisce un'istanza di HierarchicalClusterMiner con un dendrogramma della profondità specificata.
	 *
	 * @param depth La profondità del dendrogramma.
	 * @throws InvalidDepthException Se la profondità è inferiore a 1.
	 */
	public HierachicalClusterMiner(int depth) throws InvalidDepthException {
		dendrogram = new Dendrogram(depth);
	}

	/**
	 * Restituisce la profondità del dendrogramma, ovvero il numero totale di livelli.
	 *
	 * @return La profondità del dendrogramma.
	 */
	public int getDepth() {
		return dendrogram.getDepth();
	}

	/**
	 * Esegue il clustering del dataset e costruisce il dendrogramma associato.
	 * Il clustering viene eseguito iterativamente fino al raggiungimento della profondità del dendrogramma.
	 *
	 * @param data Il dataset su cui effettuare il clustering.
	 * @param distance La funzione di distanza tra cluster.
	 * @throws InvalidDepthException Se la profondità del dendrogramma è minore del numero di esempi nel dataset.
	 * @throws InvalidSizeException Se la dimensione di un cluster è minore di 2.
	 * @throws InvalidClustersNumberException Se il numero di cluster disponibili è minore di 2.
	 */
	public void mine(Data data, ClusterDistance distance) throws InvalidDepthException, InvalidSizeException, InvalidClustersNumberException {
		if (getDepth() > data.getNumberOfExample()) {
			throw new InvalidDepthException("Numero di esempi maggiore della profondità del dendrogramma!\n");
		}

		// Inizializza il livello 0 con un cluster per ciascun esempio
		ClusterSet level0 = new ClusterSet(data.getNumberOfExample());
		for (int i = 0; i < data.getNumberOfExample(); i++) {
			Cluster c = new Cluster();
			c.addData(i);
			level0.add(c);
		}
		dendrogram.setClusterSet(level0, 0);

		// Costruisce i livelli successivi del dendrogramma
		for (int i = 1; i < getDepth(); i++) {
			try {
				ClusterSet nextLevel = dendrogram.getClusterSet(i - 1).mergeClosestClusters(distance, data);
				dendrogram.setClusterSet(nextLevel, i);
			} catch (InvalidSizeException | InvalidClustersNumberException e) {
				throw e;
			}
		}
	}

	/**
	 * Restituisce una rappresentazione testuale del dendrogramma.
	 *
	 * @return Una stringa che rappresenta la struttura del dendrogramma.
	 */
	@Override
	public String toString() {
		return dendrogram.toString();
	}

	/**
	 * Restituisce una rappresentazione testuale del dendrogramma utilizzando i dati del dataset.
	 *
	 * @param data Il dataset di riferimento.
	 * @return Una stringa che rappresenta il dendrogramma con i dati dei cluster.
	 * @throws InvalidDepthException Se la profondità del dendrogramma è minore del numero di esempi nel dataset.
	 */
	public String toString(Data data) throws InvalidDepthException {
		return dendrogram.toString(data);
	}

	/**
	 * Carica un'istanza di HierarchicalClusterMiner da un file serializzato.
	 *
	 * @param fileName Il nome del file da cui caricare il modello.
	 * @return L'istanza caricata.
	 * @throws IOException Se si verifica un errore durante la lettura del file.
	 * @throws ClassNotFoundException Se la classe del file serializzato non è trovata.
	 */
	public static HierachicalClusterMiner loadHierachicalClusterMiner(String fileName) throws IOException, ClassNotFoundException {
		if (fileName == null || fileName.trim().isEmpty()) {
			throw new IllegalArgumentException("Il nome del file non può essere nullo o vuoto");
		}
		String filePath = DIRECTORY_PATH + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("File non trovato: " + fileName);
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			HierachicalClusterMiner stream = (HierachicalClusterMiner) ois.readObject();
			ois.close();
			return stream;
		}
	}

	/**
	 * Salva l'istanza corrente su un file serializzato.
	 *
	 * @param fileName Il nome del file su cui salvare il modello.
	 * @throws IOException Se si verifica un errore durante la scrittura del file.
	 */
	public void salva(String fileName) throws IOException {
		final String invalidRegex = "[<>:\"|?*]";
		final String validRegex = "^[\\w,\\s-]+\\.(txt|csv|json|xml|dat|bin|ser)$";

		if (fileName == null || fileName.trim().isEmpty()) {
			throw new IllegalArgumentException("Il nome del file non può essere nullo o vuoto");
		}

		if (fileName.matches(invalidRegex)) {
			throw new IOException("Il nome del file contiene caratteri non validi.");
		}

		if (!fileName.matches(validRegex)) {
			throw new IOException("Estensione del file non valida. Usa: .txt, .csv, .json, .xml, .dat, .bin, .ser");
		}

		File directory = new File(DIRECTORY_PATH);
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Impossibile creare la directory: " + DIRECTORY_PATH);
		}

		File file = new File(DIRECTORY_PATH + fileName);
		if (file.exists()) {
			throw new FileAlreadyExistsException("Il file esiste già: " + fileName);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(this);
		}
	}
}
