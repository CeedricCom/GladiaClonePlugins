package me.deltaorion.townymissionsv2.util;

import java.util.Random;

public class RandomHelper {

    private final Random random;

    public RandomHelper() {
        this.random = new Random();
    }

    public RandomHelper(long seed) {
        this.random = new Random(seed);
    }


    public int randomInt(int min, int max) {
        return random.nextInt(max + 1 - min) + min;
    }

    public double randomDouble(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}
