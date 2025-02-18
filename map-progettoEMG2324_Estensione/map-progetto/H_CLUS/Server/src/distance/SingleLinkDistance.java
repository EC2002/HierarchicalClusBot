package distance;

import clustering.Cluster;
import data.Data;
import data.Example;
import data.InvalidSizeException;

/**
 * Classe che implementa la distanza Single Link tra due cluster.
 *
 * La distanza Single Link (o distanza minima) tra due cluster è definita come la distanza minima tra qualsiasi coppia
 * di esempi, uno appartenente al primo cluster e l'altro al secondo cluster.
 * Questo metodo è utile negli algoritmi di clustering gerarchico per determinare quali cluster unire.
 *
 * @author Elisa Vittoria Cosmai
 */
public class SingleLinkDistance implements ClusterDistance {

	/**
	 * Calcola la distanza Single Link (minima) tra due cluster.
	 *
	 * La distanza minima viene determinata confrontando tutte le coppie di esempi,
	 * uno dal primo cluster e l'altro dal secondo cluster, e restituendo il valore minimo.
	 *
	 * @param c1 Primo cluster da confrontare.
	 * @param c2 Secondo cluster da confrontare.
	 * @param d  Dataset da cui recuperare gli esempi.
	 * @return La distanza minima tra due cluster.
	 * @throws InvalidSizeException Se le dimensioni degli esempi non corrispondono durante il calcolo.
	 */
	@Override
	public double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException {
		double min = Double.MAX_VALUE;

		// Itera sugli esempi del primo cluster
		for (Integer index1 : c1) {
			Example e1 = d.getExample(index1);

			// Calcola la distanza tra e1 e ogni esempio del secondo cluster
			for (Integer index2 : c2) {
				Example e2 = d.getExample(index2);
				double distance = e1.distance(e2);

				// Aggiorna la distanza minima
				if (distance < min) {
					min = distance;
				}
			}
		}

		// Restituisce la distanza minima trovata
		return min;
	}
}
