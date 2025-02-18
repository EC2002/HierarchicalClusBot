package distance;

import clustering.Cluster;
import data.Data;
import data.Example;
import data.InvalidSizeException;

/**
 * Classe che implementa la distanza Average Link tra due cluster.
 *
 * La distanza media (Average Link) tra due cluster viene calcolata come la somma
 * delle distanze tra tutti gli esempi dei due cluster, divisa per il prodotto
 * delle dimensioni dei cluster.
 *
 * @author Elisa Vittoria Cosmai
 */
public class AverageLinkDistance implements ClusterDistance {

    /**
     * Calcola la distanza media (Average Link) tra due cluster.
     * <p>
     * La distanza media viene ottenuta calcolando la somma di tutte le distanze tra gli esempi
     * del primo cluster e gli esempi del secondo cluster, divisa per il prodotto delle dimensioni
     * dei due cluster.
     *
     * @param c1 Primo cluster da confrontare.
     * @param c2 Secondo cluster da confrontare.
     * @param d  Dataset da cui recuperare gli esempi.
     * @return La distanza media (Average Link) tra i due cluster.
     * @throws InvalidSizeException Se le dimensioni degli esempi non corrispondono durante il calcolo.
     */
    @Override
    public double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException {
        double sum = 0.0;

        // Itera sugli esempi del primo cluster
        for (Integer index1 : c1) {
            Example e1 = d.getExample(index1);

            // Calcola la distanza tra e1 e ogni esempio del secondo cluster
            for (Integer index2 : c2) {
                Example e2 = d.getExample(index2);
                sum += e1.distance(e2);
            }
        }

        // Restituisce la distanza media
        return sum / (c1.getSize() * c2.getSize());
    }
}
