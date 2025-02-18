package clustering;

import data.Data;
import data.InvalidSizeException;
import distance.ClusterDistance;

import java.io.Serializable;

/**
 * La classe ClusterSet rappresenta un insieme di cluster in un dataset.
 * Permette la gestione dei cluster e la loro fusione in base a una metrica di distanza.
 * Ogni cluster è un'istanza della classe {@link Cluster}.
 *
 * Implementa {@link Serializable} per consentire la serializzazione dell'insieme.
 *
 * @author Elisa Vittoria Cosmai
 */
class ClusterSet implements Serializable {
	/** Array che contiene i cluster */
	private final Cluster[] C;
	/** Indice dell'ultimo cluster aggiunto */
	private int lastClusterIndex = 0;

	/**
	 * Costruisce un insieme di cluster con una capacità massima specificata.
	 *
	 * @param k Numero massimo di cluster contenibili nell'insieme.
	 */
	ClusterSet(int k) {
		C = new Cluster[k];
	}

	/**
	 * Aggiunge un nuovo cluster all'insieme, evitando duplicati.
	 *
	 * @param c Cluster da aggiungere.
	 */
	void add(Cluster c) {
		for (int j = 0; j < lastClusterIndex; j++) {
			if (c == C[j]) { // Evita duplicati
				return;
			}
		}
		C[lastClusterIndex] = c;
		lastClusterIndex++;
	}

	/**
	 * Restituisce il cluster situato all'indice specificato.
	 *
	 * @param i Indice del cluster da ottenere.
	 * @return Cluster presente alla posizione specificata.
	 */
	Cluster get(int i) {
		return C[i];
	}

	/**
	 * Esegue la fusione dei due cluster più vicini secondo una metrica di distanza.
	 *
	 * @param distance Metrica per il calcolo della distanza tra i cluster.
	 * @param data Dataset su cui è eseguito il clustering.
	 * @return Un nuovo ClusterSet con i due cluster più vicini fusi.
	 * @throws InvalidSizeException Se i cluster hanno dimensioni incompatibili.
	 * @throws InvalidClustersNumberException Se non ci sono abbastanza cluster da fondere.
	 */
	ClusterSet mergeClosestClusters(ClusterDistance distance, Data data) throws InvalidSizeException, InvalidClustersNumberException {
		if (lastClusterIndex <= 1) {
			throw new InvalidClustersNumberException("Non ci sono abbastanza cluster da fondere");
		}

		double minD = Double.MAX_VALUE;
		Cluster cluster1 = null;
		Cluster cluster2 = null;

		// Trova i due cluster più vicini
		for (int i = 0; i < this.C.length; i++) {
			Cluster c1 = get(i);
			for (int j = i + 1; j < this.C.length; j++) {
				Cluster c2 = get(j);
				double d;
				try {
					d = distance.distance(c1, c2, data);
					if (d < minD) {
						minD = d;
						cluster1 = c1;
						cluster2 = c2;
					}
				} catch (InvalidSizeException e) {
					throw e; // Interrompe l'elaborazione se la dimensione è invalida
				}
			}
		}

		// Fonde i due cluster più vicini
        assert cluster1 != null;
        Cluster mergedCluster = cluster1.mergeCluster(cluster2);
		ClusterSet finalClusterSet = new ClusterSet(this.C.length - 1);

		// Ricostruisce il nuovo insieme di cluster
		for (int i = 0; i < this.C.length; i++) {
			Cluster c = get(i);
			if (c != cluster1 && c != cluster2) {
				finalClusterSet.add(c);
			}
		}
		finalClusterSet.add(mergedCluster);

		return finalClusterSet;
	}

	/**
	 * Restituisce una rappresentazione testuale dei cluster presenti nell'insieme.
	 *
	 * @return Stringa con gli indici degli esempi raggruppati in ciascun cluster.
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < C.length; i++) {
			if (C[i] != null) {
				str.append("cluster").append(i).append(":").append(C[i]).append("\n");
			}
		}
		return str.toString();
	}

	/**
	 * Restituisce una rappresentazione testuale dei cluster con i dati degli esempi associati.
	 *
	 * @param data Dataset utilizzato per il clustering.
	 * @return Stringa contenente i dati associati a ciascun cluster.
	 */
	public String toString(Data data) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < C.length; i++) {
			if (C[i] != null) {
				str.append("cluster").append(i).append(":").append(C[i].toString(data)).append("\n");
			}
		}
		return str.toString();
	}
}
