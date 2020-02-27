package org.smartregister.dhis.tools.model;

public class Pair<A,B> {
    public A first;
    public B second;

    public static <A,B> Pair<A,B> of(A a , B b){
        Pair<A,B> pair = new Pair<>();
        pair.first = a;
        pair.second = b;
        return pair;
    }
}
