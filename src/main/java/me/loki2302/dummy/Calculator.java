package me.loki2302.dummy;

public class Calculator {
    private final Adder adder;
    private final Subtractor subtractor;

    public Calculator(Adder adder, Subtractor subtractor) {
        this.adder = adder;
        this.subtractor = subtractor;
    }

    public int add(int x, int y) {
        return adder.add(x, y);
    }

    public int subtract(int x, int y) {
        return subtractor.subtract(x, y);
    }
}
