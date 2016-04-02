/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

/**
 *
 * @author Bryden
 */
public class CNetworkManager {

    public static int unmarshallInt(byte[] pAryData, int pIntOffset) {

        int intValue = (int) pAryData[pIntOffset + 3] & 0xFF;
        intValue += ((int) pAryData[pIntOffset + 2] & 0xFF) << 8;
        intValue += ((int) pAryData[pIntOffset + 1] & 0xFF) << 16;
        intValue += ((int) pAryData[pIntOffset] & 0xFF) << 24;

        return intValue;
    }

    public static byte[] marshallInt(int pIntData) {

        byte[] aryData = new byte[4];

        for (int i = 3; i >= 0; i--) {
            aryData[i] = (byte) (pIntData & 0xff);
            pIntData >>>= 8;
        }

        return aryData;
    }

    public static StringBuilder unmarshallString(byte[] pAryData, int pIntOffset) {

        StringBuilder objSB = new StringBuilder();

        return objSB;
    }

    public static byte[] marshallString(String pStrData) {

        int intLength = pStrData.length();

        byte[] aryData = new byte[intLength + 4];

        return new byte[4];
    }

}
