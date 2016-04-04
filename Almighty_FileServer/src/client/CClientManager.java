package client;

import cache.CFile;
import cache.CFileCacheManager;
import comm.CNetworkManager;
import static comm.CNetworkManager.marshallInt;
import static comm.CNetworkManager.marshallString;
import comm.CUDPClient;
import comm.ECommand;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CClientManager {

    public static boolean validCache(String pStrFileName, String pStrServerAddr, long pLngFreshness) throws UnknownHostException, IOException {
        CFile objFileAttr = CFileCacheManager.getFileAtrr(pStrFileName);

        long lngDiff = System.currentTimeMillis() - objFileAttr.getLocalLastValidate();

        if (lngDiff < pLngFreshness) {
            return true;
        } else {

            byte[] output = CUDPClient.sendData(pStrServerAddr, handleLastModiOperation(pStrFileName));

            int intCode = CNetworkManager.unmarshallInt(output, 0);

            if (intCode == ECommand.ERROR.getCode()) {
                return false;
            }

            long lngServerLastModi = CNetworkManager.unmarshallLong(output, 4);

            objFileAttr.setLocalLastValidate();

            if (lngServerLastModi == objFileAttr.getServerLastModified()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static byte[] handleReadOperation(String pStrFile, int pIntOffset, int pIntBytes) {

        byte[] aryCommand = marshallInt(ECommand.READ.getCode());
        byte[] aryFile = marshallString(pStrFile);
        byte[] aryOffset = marshallInt(pIntOffset);
        byte[] aryCount = marshallInt(pIntBytes);

        List<byte[]> lstOutput = new ArrayList<>(4);

        lstOutput.add(aryCommand);
        lstOutput.add(aryFile);
        lstOutput.add(aryOffset);
        lstOutput.add(aryCount);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length + aryOffset.length + aryCount.length];

        int intOffset = 0;

        for (byte[] aryData : lstOutput) {
            System.arraycopy(aryData, 0, aryOutput, intOffset, aryData.length);

            intOffset += aryData.length;
        }

        return aryOutput;
    }

    public static byte[] handleWriteOperation(String pStrFile, int pIntOffset, String pStrData) {

        byte[] aryCommand = marshallInt(ECommand.WRITE.getCode());
        byte[] aryFile = marshallString(pStrFile);
        byte[] aryOffset = marshallInt(pIntOffset);
        byte[] aryCount = marshallString(pStrData);

        List<byte[]> lstOutput = new ArrayList<>(4);

        lstOutput.add(aryCommand);
        lstOutput.add(aryFile);
        lstOutput.add(aryOffset);
        lstOutput.add(aryCount);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length + aryOffset.length + aryCount.length];

        int intOffset = 0;

        for (byte[] aryData : lstOutput) {
            System.arraycopy(aryData, 0, aryOutput, intOffset, aryData.length);

            intOffset += aryData.length;
        }

        return aryOutput;
    }

    public static byte[] handleDeleteOperation(String pStrFile, int pIntOffset, int pIntBytes) {

        byte[] aryCommand = marshallInt(ECommand.DELETE.getCode());
        byte[] aryFile = marshallString(pStrFile);
        byte[] aryOffset = marshallInt(pIntOffset);
        byte[] aryCount = marshallInt(pIntBytes);

        List<byte[]> lstOutput = new ArrayList<>(4);

        lstOutput.add(aryCommand);
        lstOutput.add(aryFile);
        lstOutput.add(aryOffset);
        lstOutput.add(aryCount);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length + aryOffset.length + aryCount.length];

        int intOffset = 0;

        for (byte[] aryData : lstOutput) {
            System.arraycopy(aryData, 0, aryOutput, intOffset, aryData.length);

            intOffset += aryData.length;
        }

        return aryOutput;
    }

    public static byte[] handleCreateOperation(String pStrFile) {

        byte[] aryCommand = marshallInt(ECommand.CREATE.getCode());
        byte[] aryFile = marshallString(pStrFile);

        List<byte[]> lstOutput = new ArrayList<>(2);

        lstOutput.add(aryCommand);
        lstOutput.add(aryFile);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length];

        int intOffset = 0;

        for (byte[] aryData : lstOutput) {
            System.arraycopy(aryData, 0, aryOutput, intOffset, aryData.length);

            intOffset += aryData.length;
        }

        return aryOutput;
    }

    public static byte[] handleMonitorOperation(String pStrFile, int pIntInterval) {

        byte[] aryCommand = marshallInt(ECommand.MONITOR.getCode());
        byte[] aryFile = marshallString(pStrFile);
        byte[] aryCount = marshallInt(pIntInterval);

        List<byte[]> lstOutput = new ArrayList<>(3);

        lstOutput.add(aryCommand);
        lstOutput.add(aryFile);
        lstOutput.add(aryCount);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length + aryCount.length];

        int intOffset = 0;

        for (byte[] aryData : lstOutput) {
            System.arraycopy(aryData, 0, aryOutput, intOffset, aryData.length);

            intOffset += aryData.length;
        }

        return aryOutput;
    }

    public static byte[] handleRenameOperation(String pStrFile, String pStrFileNew) {

        byte[] aryCommand = marshallInt(ECommand.MOVE.getCode());
        byte[] aryFile = marshallString(pStrFile);
        byte[] aryFileNew = marshallString(pStrFileNew);

        List<byte[]> lstOutput = new ArrayList<>(3);

        lstOutput.add(aryCommand);
        lstOutput.add(aryFile);
        lstOutput.add(aryFileNew);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length + aryFileNew.length];

        int intOffset = 0;

        for (byte[] aryData : lstOutput) {
            System.arraycopy(aryData, 0, aryOutput, intOffset, aryData.length);

            intOffset += aryData.length;
        }

        return aryOutput;
    }

    public static byte[] handleLastModiOperation(String pStrFile) {

        byte[] aryCommand = marshallInt(ECommand.LASTMODI.getCode());
        byte[] aryFile = marshallString(pStrFile);

        List<byte[]> lstOutput = new ArrayList<>(2);

        lstOutput.add(aryCommand);
        lstOutput.add(aryFile);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length];

        int intOffset = 0;

        for (byte[] aryData : lstOutput) {
            System.arraycopy(aryData, 0, aryOutput, intOffset, aryData.length);

            intOffset += aryData.length;
        }

        return aryOutput;
    }

}
