package me.loki2302.dummy;

public class Subtractor {
    private final Negator negator;
    private final Adder adder;

    public Subtractor(Negator negator, Adder adder) {
        this.negator = negator;
        this.adder = adder;
    }

    public int subtract(int x, int y) {
        int minusY = negator.negate(y);
        return adder.add(x, minusY);
    }
}
