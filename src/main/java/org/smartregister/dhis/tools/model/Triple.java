package org.smartregister.dhis.tools.model;

public class Triple<A, B, C> {

    private A a;
    private B b;
    private C c;

    public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
        Triple<A, B, C> triple = new Triple<>();
        triple.a = a;
        triple.b = b;
        triple.c = c;
        return triple;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }
}
