package com.homework.requirement3;

import java.util.Random;

public class FaultyConnection implements Connection {
    private final Random random;
    private final double failureProbability;

    public FaultyConnection(Random random, double failureProbability) {
        this.random = (random == null) ? new Random() : random;
        if (failureProbability < 0 || failureProbability > 1) {
            throw new IllegalArgumentException("failureProbability must be in [0,1]");
        }
        this.failureProbability = failureProbability;
    }

    @Override
    public void execute(String command) {
        if (random.nextDouble() < failureProbability) {
            throw new ConnectionException("Faulty connection failed");
        }
    }

    @Override
    public void close() {
    }
}