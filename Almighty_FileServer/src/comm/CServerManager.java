package comm;

import cache.CFileCacheManager;
import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;
import static comm.CNetworkManager.unmarshallLong;
import static comm.CNetworkManager.marshallInt;
import static comm.CNetworkManager.marshallString;
import static comm.CNetworkManager.marshallLong;
import io.CFileFactory;
import io.CFileFactory.IO_STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import client.CClientManager;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CServerManager {

    // IP Address - <Seq Num, Cached result>
    private static HashMap<String, HashMap<Integer, byte[]>> _serverClientsCache = new HashMap<>();

    // File pathname - <IP Address, Server Time>
    private static HashMap<String, HashMap<String, Long>> _serverFilesMonitor = new HashMap<>();

    public static void main(String[] args) throws IOException {

        String[] ipAddress = new String[]{"0.0.0.1", "0.0.0.2", "0.0.0.3"};
        int seqNum = 0;

        for (String ip : ipAddress) {

            byte[] aryData = marshallInt(ECommand.CONN.getCode());
            byte[] arySeq = marshallInt(seqNum++);

            byte[] arySent = combine(arySeq, aryData);

            performOperation(arySent, ip);
        }

        byte[] aryOutput = CClientManager.handleCreateOperation("abc.txt");
        byte[] arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[2]);

        aryOutput = CClientManager.handleWriteOperation("abc.txt", 0, "Hello World!");
        arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[2]);

        aryOutput = CClientManager.handleReadOperation("abc.txt", 0, 100);
        arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[2]);

        aryOutput = CClientManager.handleMonitorOperation("abc.txt", 2000);
        arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[0]);

        aryOutput = CClientManager.handleMonitorOperation("abc.txt", 5000);
        arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[1]);

        // Client 1 & 2 updated
        aryOutput = CClientManager.handleWriteOperation("abc.txt", 12, " Bryden is so shady!");
        arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[2]);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.out.println("3 seconds has elapsed on the server......\n");

        aryOutput = CClientManager.handleRenameOperation("abc.txt", "def.txt");
        arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[2]);

        // Client 2 updated
        aryOutput = CClientManager.handleDeleteOperation("def.txt", 0, 13);
        arySeq = marshallInt(seqNum++);
        aryOutput = combine(arySeq, aryOutput);
        performOperation(aryOutput, ipAddress[2]);
    }

    private static byte[] combine(byte[] seq, byte[] data) {

        byte[] arySent = new byte[data.length + seq.length];

        System.arraycopy(seq, 0, arySent, 0, seq.length);
        System.arraycopy(data, 0, arySent, seq.length, data.length);

        return arySent;
    }

    public static byte[] performOperation(byte[] pAryData, String pStrAddr) throws IOException {

        int offset = 0;

        // Get the sequence number
        int seqNumber = unmarshallInt(pAryData, offset);
        offset += 4;

        HashMap<Integer, byte[]> clientCache = _serverClientsCache.get(pStrAddr);
        if (clientCache == null) {
            clientCache = new HashMap<>();
            _serverClientsCache.put(pStrAddr, clientCache);
        }

        // Check the command
        ECommand objCommand = ECommand.getCommand(unmarshallInt(pAryData, offset));
        offset += 4;

        // Print client IP, seq number, and action to be performed.
        System.out.printf("IP: %-15s\tSeq #: %-8d\tAction: %-12s%n",
                pStrAddr, seqNumber, objCommand.toString());

        // Byte arraylist for storing results
        ArrayList<Byte> lstBytes = new ArrayList<>();
        byte[] arrBytes = null;

        switch (objCommand) {

            case CONN:
                clientCache.clear();

                addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
                arrBytes = convertResult(lstBytes);

                System.out.println("Client '" + pStrAddr + "' connected to server.\n");

                break;

            case ACK:
                break;

            case CREATE:
                createFile(pAryData, offset, lstBytes);

                arrBytes = convertResult(lstBytes);
                printCodeMsg(arrBytes);

                break;

            case DELETE:
                // Non-Idempotent
                arrBytes = clientCache.get(seqNumber);
                if (arrBytes != null) {

                    System.out.println("One-update semantics, retrieving from cache..");
                    printCodeMsg(arrBytes);
                    return arrBytes;
                }

                // Perform action
                deleteFromFile(pAryData, offset, lstBytes);

                arrBytes = convertResult(lstBytes);
                clientCache.put(seqNumber, arrBytes);
                printCodeMsg(arrBytes);

                // Update clients monitoring this file (if any)
                update(unmarshallString(pAryData, offset).toString(), pStrAddr, arrBytes);

                break;

            case ERROR:
                break;

            case MONITOR:
                // Monitors a specified file
                monitorFile(pAryData, offset, lstBytes, pStrAddr);

                arrBytes = convertResult(lstBytes);
                printCodeMsg(arrBytes);

                break;

            case MOVE:
                // Non-Idempotent
                arrBytes = clientCache.get(seqNumber);
                if (arrBytes != null) {

                    System.out.println("One-update semantics, retrieving from cache..");
                    printCodeMsg(arrBytes);
                    return arrBytes;
                }

                // Perform action
                moveOrRenameFile(pAryData, offset, lstBytes);

                arrBytes = convertResult(lstBytes);
                clientCache.put(seqNumber, arrBytes);
                printCodeMsg(arrBytes);

                break;

            case READ:
                readFromFile(pAryData, offset, lstBytes);

                arrBytes = convertResult(lstBytes);
                printCodeMsgContents(arrBytes);

                break;

            case UPDATE:
                // Do what sia?
                break;

            case WRITE:
                // Non-Idempotent
                arrBytes = clientCache.get(seqNumber);
                if (arrBytes != null) {

                    System.out.println("One-update semantics, retrieving from cache..");
                    printCodeMsg(arrBytes);
                    return arrBytes;
                }

                // Perform action
                writeToFile(pAryData, offset, lstBytes);

                arrBytes = convertResult(lstBytes);
                clientCache.put(seqNumber, arrBytes);
                printCodeMsg(arrBytes);

                // Update clients monitoring this file (if any)
                update(unmarshallString(pAryData, offset).toString(), pStrAddr, arrBytes);

                break;

            case LASTMODI:
                getLastModified(pAryData, offset, lstBytes);

                arrBytes = convertResult(lstBytes);
                printCodeLastModi(arrBytes);

                break;

            default:
                break;
        }

        return arrBytes;
    }

    private static void createFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {

        String strPathName = unmarshallString(pAryData, offset).toString();

        CFileFactory.createFile(strPathName, "");

        addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
        addToResult(lstBytes, "File '" + strPathName + "' created successfully!");
    }

    private static void readFromFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {

        String strPathName = unmarshallString(pAryData, offset).toString();
        offset += strPathName.length() + 4;

        int fileOffset = unmarshallInt(pAryData, offset);
        offset += 4;

        int numBytesToRead = unmarshallInt(pAryData, offset);
        offset += 4;

        StringBuilder sb = new StringBuilder();
        IO_STATUS ioStatus = CFileFactory.readFromFile(
                strPathName, fileOffset, numBytesToRead, sb);

        switch (ioStatus) {
            case FILE_NOT_FOUND:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "READ ERROR: FILE NOT FOUND LA!");
                addToResult(lstBytes, "");
                break;
            case OFFSET_EXCEEDS_LENGTH:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "READ ERROR: OFFSET EXCEEDS LENGTH LA!");
                addToResult(lstBytes, "");
                break;
            case SUCCESS:
                addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
                addToResult(lstBytes, ("Read data from '" + strPathName + "' successfully."));
                addToResult(lstBytes, sb.toString());

                long lastModifiedTime = CFileFactory.getLastModifiedTime(strPathName);
                addToResult(lstBytes, marshallLong(lastModifiedTime));

                long fileSize = CFileFactory.getFileSize(strPathName);
                addToResult(lstBytes, marshallLong(fileSize));
                break;
            default:
                break;
        }
    }

    private static void writeToFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {

        String strPathName = unmarshallString(pAryData, offset).toString();
        offset += strPathName.length() + 4;

        int fileOffset = unmarshallInt(pAryData, offset);
        offset += 4;

        String data = unmarshallString(pAryData, offset).toString();

        IO_STATUS ioStatus = CFileFactory.writeToFile(strPathName, fileOffset, data);

        switch (ioStatus) {
            case FILE_NOT_FOUND:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "WRITE ERROR: FILE NOT FOUND LA!");
                break;
            case OFFSET_EXCEEDS_LENGTH:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "WRITE ERROR: OFFSET EXCEEDS LENGTH LA!");
                break;
            case SUCCESS:
                addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
                addToResult(lstBytes, "Written data to '" + strPathName + "' successfully.");

                long fileSize = CFileFactory.getFileSize(strPathName);
                addToResult(lstBytes, marshallLong(fileSize));
                break;
            default:
                break;
        }
    }

    private static void deleteFromFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {

        String strPathName = unmarshallString(pAryData, offset).toString();
        offset += strPathName.length() + 4;

        int fileOffset = unmarshallInt(pAryData, offset);
        offset += 4;

        int numBytesToDelete = unmarshallInt(pAryData, offset);

        IO_STATUS ioStatus = CFileFactory.deleteFromFile(
                strPathName, fileOffset, numBytesToDelete);

        switch (ioStatus) {
            case FILE_NOT_FOUND:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "DELETE ERROR: FILE NOT FOUND LA!");
                break;
            case OFFSET_EXCEEDS_LENGTH:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "DELETE ERROR: OFFSET EXCEEDS LENGTH LA!");
                break;
            case SUCCESS:
                addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
                addToResult(lstBytes, "Deleted data from '" + strPathName + "' successfully.");

                long fileSize = CFileFactory.getFileSize(strPathName);
                addToResult(lstBytes, marshallLong(fileSize));
                break;
            default:
                break;
        }
    }

    private static void moveOrRenameFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {

        String strPathNameOld = unmarshallString(pAryData, offset).toString();
        offset += strPathNameOld.length() + 4;

        String strPathNameNew = unmarshallString(pAryData, offset).toString();

        IO_STATUS ioStatus = CFileFactory.moveOrRenameFile(strPathNameOld, strPathNameNew);

        switch (ioStatus) {
            case FILE_NOT_FOUND:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "MOVE/RENAME ERROR: FILE NOT FOUND LA!");
                break;
            case FILE_NAME_ALREADY_EXISTS:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, "MOVE/RENAME ERROR: File with same name exists at destination.");
                break;
            case SUCCESS:
                addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
                addToResult(lstBytes, "Moved/Renamed from '" + strPathNameOld + "' to '"
                        + strPathNameNew + "' successfully.");

                monitorMovedOrRenamed(strPathNameOld, strPathNameNew);
                break;
            default:
                break;
        }
    }

    private static void getLastModified(byte[] pAryData, int offset,
            ArrayList<Byte> lstBytes) throws IOException {

        String strPathName = unmarshallString(pAryData, offset).toString();
        IO_STATUS ioStatus = CFileFactory.findFile(strPathName);

        switch (ioStatus) {
            case FILE_NOT_FOUND:
                addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
                addToResult(lstBytes, marshallLong(0));
                break;
            case SUCCESS:
                addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
                addToResult(lstBytes, marshallLong(
                        CFileFactory.getLastModifiedTime(strPathName)));
                break;
            default:
                break;
        }
    }

    /**
     * NOTE: Seq number, Cmd, File path name, Period (ms) *
     */
    private static void monitorFile(byte[] pAryData, int offset,
            ArrayList<Byte> lstBytes, String strIPAddr) throws IOException {

        String strPathName = unmarshallString(pAryData, offset).toString();
        offset += strPathName.length() + 4;

        // Period in milliseconds (int)
        int period = unmarshallInt(pAryData, offset);

        if (CFileFactory.findFile(strPathName) == IO_STATUS.FILE_NOT_FOUND) {
            addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
            addToResult(lstBytes, ("ERROR : File " + strPathName + " not found"));
        } else {
            HashMap<String, Long> fileMonitor = _serverFilesMonitor.get(strPathName);
            if (fileMonitor == null) {
                fileMonitor = new HashMap<>();
                _serverFilesMonitor.put(strPathName, fileMonitor);
            }

            fileMonitor.put(strIPAddr, System.currentTimeMillis() + period);

            addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
            addToResult(lstBytes, ("Client '" + strIPAddr + "' monitoring '"
                    + strPathName + "' for " + period + " ms."));
        }
    }

    private static void update(String strPathName, String strIPAddr, byte[] arrBytes) throws IOException {

        HashMap<String, Long> fileMonitor = _serverFilesMonitor.get(strPathName);
        if (fileMonitor == null) {
            return;
        }

        // Remove expired monitors for the specified file
        long currSysTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iter = fileMonitor.entrySet().iterator();
        while (iter.hasNext()) {

            Map.Entry<String, Long> mapEntry = iter.next();
            if (mapEntry.getValue() < currSysTime) {

                iter.remove();
            }
        }

        // Remove from list of monitored files, and do nothing
        if (fileMonitor.isEmpty()) {
            _serverFilesMonitor.remove(strPathName);
            return;
        }

        int up_offset = 0;

        int up_code = unmarshallInt(arrBytes, up_offset);
        up_offset += 4;

        // Changes were made successfully to a monitored file
        if (ECommand.getCommand(up_code) == ECommand.ACK) {

            // Get the (possibly) updated file contents
            StringBuilder sb = new StringBuilder();
            CFileFactory.readFromFile(
                    strPathName, 0, CFileFactory.getFileSize(strPathName), sb);

            // NOTE: strIPAddr contains IP address of client who made the changes
            System.out.println("File at '" + strPathName + "' changed by client '"
                    + strIPAddr + "'. Updating all clients monitoring this file.");
            System.out.println("The updated contents: " + sb.toString() + "\n");

            // Update all clients monitoring this file
            Set<String> lstClients = fileMonitor.keySet();

            String strText = strPathName + "' has been modified by " + strIPAddr;

            byte[] aryOutput = marshallString(strText);
            byte[] aryContents = marshallString(sb.toString());
            byte[] aryModi = marshallLong(CFileFactory.getLastModifiedTime(strPathName));

            byte[] aryFinalOutput = new byte[aryOutput.length + aryContents.length + aryModi.length];

            System.arraycopy(aryOutput, 0, aryFinalOutput, 0, aryOutput.length);
            System.arraycopy(aryContents, 0, aryFinalOutput, aryOutput.length, aryContents.length);
            System.arraycopy(aryModi, 0, aryFinalOutput, aryOutput.length + aryContents.length, aryModi.length);

            for (String ip : lstClients) {
                System.out.println("Sending data");
                CUDPClient.sendData(ip, aryFinalOutput, 2222);
            }

            System.out.println();
        }
    }

    private static void monitorMovedOrRenamed(String strPathNameOld, String strPathNameNew) {

        HashMap<String, Long> fileMonitor = _serverFilesMonitor.get(strPathNameOld);
        if (fileMonitor != null) {

            _serverFilesMonitor.remove(strPathNameOld);
            _serverFilesMonitor.put(strPathNameNew, fileMonitor);
        }
    }

    private static void addToResult(ArrayList<Byte> lstBytes, String str) {
        addToResult(lstBytes, marshallString(str));
    }

    private static void addToResult(ArrayList<Byte> lstBytes, byte[] arrBytes) {

        for (byte b : arrBytes) {
            lstBytes.add(b);
        }
    }

    private static byte[] convertResult(ArrayList<Byte> lstBytes) {

        byte[] arrBytes = new byte[lstBytes.size()];
        for (int i = 0; i < lstBytes.size(); i++) {
            arrBytes[i] = lstBytes.get(i);
        }

        return arrBytes;
    }

    private static void printCodeMsg(byte[] arrBytes) {

        int un_offset = 0;

        int un_code = unmarshallInt(arrBytes, un_offset);
        String str_un_code = ECommand.getCommand(un_code).toString();
        un_offset += 4;

        String un_msg = unmarshallString(arrBytes, un_offset).toString();

        System.out.printf("Result: %-12s\tMsg: %-40s%n%n", str_un_code, un_msg);
    }

    private static void printCodeMsgContents(byte[] arrBytes) {

        int un_offset = 0;

        int un_code = unmarshallInt(arrBytes, un_offset);
        ECommand cmd = ECommand.getCommand(un_code);
        String str_un_code = cmd.toString();
        un_offset += 4;

        String un_msg = unmarshallString(arrBytes, un_offset).toString();
        un_offset += (4 + un_msg.length());

        System.out.printf("Result: %-12s\tMsg: %-40s",
                str_un_code, un_msg);

        if (cmd == ECommand.ACK) {

            String un_contents = unmarshallString(arrBytes, un_offset).toString();
            System.out.printf("\tContents: %-80s", un_contents);
        }
        System.out.printf("%n%n");
    }

    private static void printCodeLastModi(byte[] arrBytes) {

        int un_offset = 0;

        int un_code = unmarshallInt(arrBytes, un_offset);
        String str_un_code = ECommand.getCommand(un_code).toString();
        un_offset += 4;

        long un_lastModi = unmarshallLong(arrBytes, un_offset);

        System.out.printf("Result: %-12s\tLast Modified: %-20d%n%n", str_un_code, un_lastModi);
    }

    public static byte[] performMonitoringOperation(String pStrFileName, byte[] pAryData, String pStrAddr) throws IOException {

        ArrayList<Byte> lstBytes = new ArrayList<>();
        byte[] arrBytes = null;

        String strMsg = unmarshallString(pAryData, 4).toString();
        String strData = unmarshallString(pAryData, 8 + strMsg.length()).toString();
        long lngModi = unmarshallLong(pAryData, 12 + strMsg.length() + strData.length());

        System.out.println(strMsg);
        System.out.println("New Contents " + strData);

        CFileCacheManager.setFileCache(pStrFileName, strData, lngModi);

        addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));

        return convertResult(lstBytes);

    }
}
