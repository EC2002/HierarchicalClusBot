package distance;

import clustering.Cluster;
import data.Data;
import data.InvalidSizeException;

/**
 * Interfaccia che definisce il metodo per calcolare la distanza tra due cluster.
 *
 * Le classi che implementano questa interfaccia devono fornire una specifica metrica
 * per calcolare la distanza tra due cluster in base ai dati forniti.
 *
 * @author Elisa Vittoria Cosmai
 */
public interface ClusterDistance {
	/**
	 * Calcola la distanza tra due cluster utilizzando una metrica specifica.
	 *
	 * @param c1 Primo cluster da confrontare.
	 * @param c2 Secondo cluster da confrontare.
	 * @param d  Dataset contenente i dati degli esempi nei cluster.
	 * @return La distanza calcolata tra i due cluster.
	 * @throws InvalidSizeException Se la dimensione del cluster è minore di 2.
	 */
	public double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException;
}
