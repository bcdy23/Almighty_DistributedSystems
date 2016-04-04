package util;

import java.util.Random;

public class CRandomGenerator {

    public static int getInt(int pIntMin, int pIntMax) {
        Random objRandom = new Random();
        
        return objRandom.nextInt(pIntMax - pIntMin + 1) + pIntMin;
    }
}