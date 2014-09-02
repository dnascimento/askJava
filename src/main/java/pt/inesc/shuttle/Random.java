package pt.inesc.shuttle;

import voldemort.undoTracker.SRD;

public class Random {

    public java.util.Random random;

    public Random(SRD srd) {
        random = new java.util.Random(srd.rid);
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public Float nextFloat() {
        return random.nextFloat();
    }

    public double nextGaussian() {
        return random.nextGaussian();
    }

    public int nextInt() {
        return random.nextInt();
    }

    public long nextLong() {
        return random.nextLong();
    }
}
