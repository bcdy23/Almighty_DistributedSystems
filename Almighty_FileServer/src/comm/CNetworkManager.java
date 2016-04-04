package comm;

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

    public static long unmarshallLong(byte[] pAryData, int pIntOffset) {

        long intValue = (long) pAryData[pIntOffset + 7] & 0xFF;
        intValue += ((long) pAryData[pIntOffset + 6] & 0xFF) << 8;
        intValue += ((long) pAryData[pIntOffset + 5] & 0xFF) << 16;
        intValue += ((long) pAryData[pIntOffset + 4] & 0xFF) << 24;
        intValue += ((long) pAryData[pIntOffset + 3] & 0xFF) << 32;
        intValue += ((long) pAryData[pIntOffset + 2] & 0xFF) << 40;
        intValue += ((long) pAryData[pIntOffset + 1] & 0xFF) << 48;
        intValue += ((long) pAryData[pIntOffset] & 0xFF) << 56;

        return intValue;
    }

    public static byte[] marshallLong(long pIntData) {

        byte[] aryData = new byte[8];

        for (int i = 7; i >= 0; i--) {
            aryData[i] = (byte) (pIntData & 0xff);
            pIntData >>>= 8;
        }

        return aryData;
    }

    public static StringBuilder unmarshallString(byte[] pAryData, int pIntOffset) {

        int intLength = unmarshallInt(pAryData, pIntOffset);

        StringBuilder objSB = new StringBuilder();

        int intCount = 0;

        pIntOffset += 4;

        while (intCount < intLength) {
            objSB.append((char) pAryData[pIntOffset + intCount]);
            intCount++;
        }

        return objSB;
    }

    public static byte[] marshallString(String pStrData) {

        int intLength = pStrData.length();

        byte[] aryData = new byte[intLength + 4];

        System.arraycopy(marshallInt(intLength), 0, aryData, 0, 4);

        System.arraycopy(pStrData.getBytes(), 0, aryData, 4, pStrData.length());

        return aryData;
    }

}
