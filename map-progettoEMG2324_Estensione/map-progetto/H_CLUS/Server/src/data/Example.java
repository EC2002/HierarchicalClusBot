package data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * La classe Example rappresenta un'entità esempio come un vettore di valori reali.
 *
 * La classe consente di gestire e manipolare un insieme di valori numerici,
 * supportando operazioni come l'aggiunta di valori e il calcolo della distanza euclidea.
 *
 * Implementa {@link Iterable} per consentire l'iterazione sui valori contenuti nell'esempio.
 *
 * @author Elisa Vittoria Cosmai
 */
public class Example implements Iterable<Double>{
    /** Vettore di valori reali che rappresenta l'esempio */
    private final List<Double> example; //vettore di valori reali

    /**
     * Costruisce un'istanza di Example inizializzando una lista vuota di valori reali.
     */
    public Example(){
        example = new LinkedList<>();
    }

    /**
     * Restituisce un iteratore per scorrere i valori contenuti nell'esempio.
     *
     * @return Un iteratore che scorre i valori numerici presenti nell'istanza.
     */
    public Iterator<Double> iterator(){
        return example.iterator();
    }

    /**
     * Aggiunge un valore numerico alla lista dell'esempio.
     *
     * @param v Valore da aggiungere alla lista.
     */
    public void add(Double v){
        example.add(v);
    }

    /**
     * Calcola la distanza euclidea tra l'istanza corrente e un'altra istanza di {@link Example}.
     *
     * @param newE L'altra istanza di {@link Example} con cui confrontare la distanza.
     * @return Il valore della distanza euclidea tra i due vettori.
     * @throws InvalidSizeException Se i due vettori hanno dimensioni diverse.
     */
    public double distance(Example newE) throws InvalidSizeException{
        if(example.size() != newE.example.size())
            throw new InvalidSizeException("Gli esempi hanno dimensioni diverse");

        double sum = 0.0;
        Iterator<Double> iterator1 = example.iterator();
        Iterator<Double> iterator2 = newE.iterator();

        while (iterator1.hasNext() && iterator2.hasNext()) {
            double diff = iterator1.next() - iterator2.next();
            sum += Math.pow(diff, 2);
        }

        return sum;
    }

    /**
     * Restituisce una rappresentazione testuale dei valori contenuti nell'istanza.
     * I valori sono separati da una virgola.
     *
     * @return Una stringa contenente i valori del vettore numerico.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        Iterator<Double> iterator = iterator();

        if (iterator.hasNext())
            s.append(iterator.next());

        while (iterator.hasNext())
            s.append(",").append(iterator.next());

        return s.toString();
    }
}

