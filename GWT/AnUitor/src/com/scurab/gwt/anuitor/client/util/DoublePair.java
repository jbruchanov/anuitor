package com.scurab.gwt.anuitor.client.util;

import com.github.gwtd3.api.arrays.Array;

public class DoublePair {

    public final double first;
    public final double second;

    public DoublePair(double first, double second) {
        this.first = first;
        this.second = second;
    }

    public DoublePair swap() {
        return new DoublePair(second, first);
    }

    public Array<Double> toArray() {
        Array<Double> arr = Array.create();
        arr.push(first);
        arr.push(second);
        return arr;
    }

    public DoublePair multiply(double both) {
        return multiply(both, both);
    }

    public DoublePair multiply(double first, double second) {
        return new DoublePair(this.first * first, this.second * second);
    }
        
    public DoublePair plus(double both) {
        return plus(both, both);
    }
    
    public DoublePair plus(double first, double second) {
        return new DoublePair(this.first + first, this.second + second);
    }
}
