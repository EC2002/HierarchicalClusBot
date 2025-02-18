package clustering;

import data.Data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


/**
 * La classe Cluster rappresenta un gruppo di dati all'interno di un clustering.
 * Ogni cluster è composto da un insieme di indici che corrispondono alle posizioni
 * degli esempi all'interno del dataset {@link Data}.
 * La classe utilizza un {@link TreeSet} per mantenere gli indici ordinati.
 *
 * Implementa {@link Iterable} per consentire l'iterazione sugli indici,
 * {@link Cloneable} per supportare la clonazione dell'oggetto e
 * {@link Serializable} per permettere la serializzazione del cluster.
 *
 * @author Elisa Vittoria Cosmai
 */
public class Cluster implements Iterable<Integer>, Cloneable, Serializable {
	/**
	 * Set di interi che rappresenta gli indici degli esempi appartenenti al cluster.
	 * L'uso di un {@link TreeSet} garantisce l'ordine naturale degli elementi.
	 */
	private Set<Integer> clusteredData =new TreeSet<>();

	/**
	 * Aggiunge un indice al cluster.
	 *
	 * @param id indice dell'esempio da aggiungere al cluster
	 */
	void addData(int id){
		clusteredData.add(id);
	}

	/**
	 * Restituisce il numero di elementi contenuti nel cluster.
	 *
	 * @return dimensione del cluster
	 */
	public int getSize() {
		return clusteredData.size();
	}

	/**
	 * Restituisce un iteratore sugli indici degli elementi contenuti nel cluster.
	 *
	 * @return iteratore sugli indici del cluster
	 */
	public Iterator<Integer> iterator() {
		return clusteredData.iterator();
	}

	/**
	 * Crea una copia del cluster esistente.
	 * La clonazione è profonda per garantire che l'oggetto clonato sia indipendente dall'originale.
	 *
	 * @return una copia del cluster corrente
	 * @throws CloneNotSupportedException se la clonazione non è supportata
	 */
	@Override
	public Cluster clone() throws CloneNotSupportedException {
		Cluster clone;
		try {
			clone = (Cluster) super.clone();
			//noinspection unchecked
			clone.clusteredData = (Set<Integer>) ((TreeSet<Integer>) this.clusteredData).clone();
		} catch (CloneNotSupportedException e) {
			throw new CloneNotSupportedException("Errore nella clonazione");
		}

		return clone;
	}

	/**
	 * Unisce il cluster corrente con un altro cluster e restituisce un nuovo cluster risultante dalla fusione.
	 *
	 * @param c il cluster da unire
	 * @return nuovo cluster contenente gli elementi di entrambi i cluster
	 */
	Cluster mergeCluster(Cluster c) {
		Cluster newC = new Cluster();
		Iterator<Integer> it1 = this.iterator();
		Iterator<Integer> it2 = c.iterator();

		while (it1.hasNext()) {
			newC.addData(it1.next());
		}
		while (it2.hasNext()) {
			newC.addData(it2.next());
		}

		return newC;
	}


	/**
	 * Restituisce una rappresentazione testuale del cluster, mostrando gli indici contenuti.
	 *
	 * @return stringa contenente gli indici separati da virgole
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		Iterator<Integer> it = this.iterator();

		if (it.hasNext())
			str.append(it.next());

		while (it.hasNext())
			str.append(",").append(it.next());

		return str.toString();
	}

	/**
	 * Restituisce una rappresentazione del cluster, mostrando i dati associati agli indici nel dataset.
	 *
	 * @param data il dataset di riferimento
	 * @return stringa con i dati degli esempi nel cluster
	 */
	public String toString(Data data) {
		StringBuilder str = new StringBuilder();

		for (Integer clusteredDatum : clusteredData)
			str.append("<[").append(data.getExample(clusteredDatum)).append("]>");

		return str.toString();
	}


}
