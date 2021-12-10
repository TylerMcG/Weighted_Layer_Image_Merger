/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) {
            return this;
        }
        total += Math.pow(weight,2);
        map.put(total, result);
        return this;
    }
    /*
    Returns a key-value mapping associated with the least key strictly greater
    than the given key based on a random double(0-1) times the total weight
    */
    public E next() throws NullPointerException {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    public void output(){
        map.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
        });
    }
}