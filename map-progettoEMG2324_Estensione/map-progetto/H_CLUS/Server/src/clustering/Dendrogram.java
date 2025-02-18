package clustering;

import data.Data;

import java.io.Serializable;

/**
 * La classe Dendrogram rappresenta un dendrogramma utilizzato per modellare gerarchie di cluster
 * in un'analisi di clustering gerarchico.
 * Il dendrogramma è costituito da diversi livelli, ciascuno contenente un insieme di cluster ({@link ClusterSet}).
 *
 * Implementa {@link Serializable} per supportare la serializzazione dell'oggetto.
 *
 * @author Elisa Vittoria Cosmai
 */
class Dendrogram implements Serializable {
    /**
     * Array che rappresenta i livelli del dendrogramma, ciascuno contenente un {@link ClusterSet}.
     */
    private final ClusterSet[] tree;

    /**
     * Costruisce un dendrogramma con una profondità specificata.
     *
     * @param depth La profondità del dendrogramma, che rappresenta il numero di livelli.
     * @throws InvalidDepthException Se la profondità specificata è minore o uguale a zero.
     */
    Dendrogram(int depth) throws InvalidDepthException {
        if (depth <= 0) {
            throw new InvalidDepthException("Profondità non valida!\n");
        }
        tree = new ClusterSet[depth];
    }

    /**
     * Imposta un {@link ClusterSet} in un livello specifico del dendrogramma.
     *
     * @param c Il ClusterSet da assegnare.
     * @param level Il livello del dendrogramma in cui inserire il ClusterSet.
     */
    void setClusterSet(ClusterSet c, int level) {
        tree[level] = c;
    }

    /**
     * Restituisce il {@link ClusterSet} associato a un livello specifico del dendrogramma.
     *
     * @param level Il livello da cui recuperare il ClusterSet.
     * @return Il ClusterSet al livello specificato, oppure {@code null} se non è stato assegnato alcun ClusterSet.
     */
    ClusterSet getClusterSet(int level) {
        return tree[level];
    }

    /**
     * Restituisce la profondità totale del dendrogramma, ovvero il numero di livelli presenti.
     *
     * @return Il numero totale di livelli nel dendrogramma.
     */
    int getDepth() {
        return tree.length;
    }

    /**
     * Restituisce una rappresentazione testuale del dendrogramma, includendo i livelli e i relativi ClusterSet.
     *
     * @return Una stringa che rappresenta la struttura del dendrogramma.
     */
    @Override
    public String toString() {
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < tree.length; i++) {
            v.append("level").append(i).append(":\n");
            if (tree[i] != null) {
                v.append(tree[i]);
            } else {
                v.append("null");
            }
            v.append("\n");
        }
        return v.toString();
    }

    /**
     * Restituisce una rappresentazione testuale del dendrogramma, includendo i dati associati ai cluster in ogni livello.
     *
     * @param data Il dataset utilizzato per il clustering.
     * @return Una stringa che rappresenta il dendrogramma con i dati degli esempi raggruppati.
     */
    public String toString(Data data) {
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < tree.length; i++) {
            v.append("level").append(i).append(":\n");
            if (tree[i] != null) {
                v.append(tree[i].toString(data));
            } else {
                v.append("null");
            }
            v.append("\n");
        }
        return v.toString();
    }
}
