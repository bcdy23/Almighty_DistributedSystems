/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Random;

/**
 *
 * @author Bryden
 */
public class CRandomGenerator {

    public static int getInt(int pIntMin, int pIntMax) {
        Random objRandom = new Random();
        
        return objRandom.nextInt(pIntMax - pIntMin + 1) + pIntMin;
    }

}
