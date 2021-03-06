package main;

import cache.CFileCacheManager;
import client.CClientManager;

import comm.CNetworkManager;
import comm.CUDPClient;
import static comm.CUDPClient.connectionEstablish;
import comm.CUDPServer_MultiCast;
import comm.ECommand;

import java.io.IOException;
import java.util.Scanner;

public class FileClient {

    private static Scanner sc = new Scanner(System.in);

    private static int displayServerReadResponse(byte[] pAryData, String pStrFileName, int pIntOffset, int pIntCount) {
        int intCode = CNetworkManager.unmarshallInt(pAryData, 0);

        String strMsg = CNetworkManager.unmarshallString(pAryData, 4).toString();

        System.out.println(strMsg);

        if (intCode == ECommand.ACK.getCode()) {

            String strReadData = CNetworkManager.unmarshallString(pAryData, strMsg.length() + 8).toString();

            if (pIntOffset + pIntCount >= strReadData.length()) {
                System.out.println("Data Read : " + strReadData.substring(pIntOffset));
            } else {
                System.out.println("Data Read : " + strReadData.substring(pIntOffset, pIntOffset + pIntCount));
            }

            CFileCacheManager.setFileCache(pStrFileName, strReadData, CNetworkManager.unmarshallLong(pAryData, strReadData.length() + strMsg.length() + 12));
        }

        return intCode;
    }

    private static int displayServerWriteResponse(byte[] pAryData, String pStrFileName, int pIntOffset, String pStrData) {
        int intCode = CNetworkManager.unmarshallInt(pAryData, 0);

        String strMsg = CNetworkManager.unmarshallString(pAryData, 4).toString();

        System.out.println(strMsg);

        if (intCode == ECommand.ACK.getCode()) {

            long lngLastModi = CNetworkManager.unmarshallLong(pAryData, strMsg.length() + 8);

            if (CFileCacheManager.fileInCache(pStrFileName)) {
                StringBuilder objOut = new StringBuilder(CFileCacheManager.getCacheBlock(pStrFileName, 0));

                objOut.insert(pIntOffset, pStrData);

                CFileCacheManager.setFileCache(pStrFileName, objOut.toString(), lngLastModi, false);
            }

        }

        return intCode;
    }

    private static int displayServerDeleteResponse(byte[] pAryData, String pStrFileName, int pIntOffset, int pIntCount) {
        int intCode = CNetworkManager.unmarshallInt(pAryData, 0);

        String strMsg = CNetworkManager.unmarshallString(pAryData, 4).toString();

        System.out.println(strMsg);

        if (intCode == ECommand.ACK.getCode()) {

            long lngLastModi = CNetworkManager.unmarshallLong(pAryData, strMsg.length() + 8);

            if (CFileCacheManager.fileInCache(pStrFileName)) {
                StringBuilder objOut = new StringBuilder(CFileCacheManager.getCacheBlock(pStrFileName, 0));

                objOut.delete(pIntOffset, pIntOffset + pIntCount);

                CFileCacheManager.setFileCache(pStrFileName, objOut.toString(), lngLastModi, false);
            }

        }

        return intCode;
    }

    private static int displayServerResponse(byte[] pAryData, ECommand pObjCommand) {
        int intCode = CNetworkManager.unmarshallInt(pAryData, 0);

        String strMsg = CNetworkManager.unmarshallString(pAryData, 4).toString();

        System.out.println(strMsg);

        long lngLastModi;

        if (intCode == ECommand.ACK.getCode()) {

        }

        return intCode;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

//        String strServerAdd = "172.22.248.33";
        String strServerAdd = "127.0.0.1";
        System.out.println("Initalizing system..\n");

        connectionEstablish(strServerAdd);

        int intFreshness = getIntChoice("Set Freshness Interval (sec) ");

        System.out.println("\nSystem initialization completed!");

        System.out.println("\nWelcome to Almighty Distributed File System");

        int intChoice;
        String strFile;

        int intOffset;
        int intCount;

        byte[] aryOutput;
        byte[] data;
        do {
            displayMainMenu();
            intChoice = getIntChoice();

            switch (ECommand.getCommand(intChoice)) {
                case READ:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    intCount = getIntChoice();

                    int intCount2 = intCount;

                    if (CFileCacheManager.fileInCache(strFile)) {

                        if (CClientManager.validCache(strFile, strServerAdd, intFreshness * 1000)) {
                            System.out.println("Reading from Cache");

                            String strData = CFileCacheManager.getCacheBlock(strFile, 0);

                            if (intOffset > strData.length()) {
                                System.out.println("Invalid Offset");
                            } else if (intOffset + intCount >= strData.length()) {
                                System.out.println(CFileCacheManager.getCacheBlock(strFile, 0).substring(intOffset));
                            } else {
                                System.out.println(CFileCacheManager.getCacheBlock(strFile, 0).substring(intOffset, intOffset + intCount));
                            }

                            break;
                        }
                    } else {
                        intCount2 = intCount;
                        intCount = 850;
                    }
                    aryOutput = CClientManager.handleReadOperation(strFile, intOffset, intCount);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    if (data != null) {
                        displayServerReadResponse(data, strFile, intOffset, intCount2);
                    }
                    break;
                case WRITE:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    String strData = getStringChoice();

                    aryOutput = CClientManager.handleWriteOperation(strFile, intOffset, strData);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    if (data != null) {
                        displayServerWriteResponse(data, strFile, intOffset, strData);
                    }

                    break;
                case DELETE:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    intCount = getIntChoice();

                    aryOutput = CClientManager.handleDeleteOperation(strFile, intOffset, intCount);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    if (data != null) {
                        displayServerDeleteResponse(data, strFile, intOffset, intCount);
                    }
                    break;
                case CREATE:
                    strFile = getStringChoice();

                    aryOutput = CClientManager.handleCreateOperation(strFile);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    if (data != null) {
                        displayServerResponse(data, ECommand.CREATE);
                    }
                    break;
                case MONITOR:
                    strFile = getStringChoice();
                    intCount = getIntChoice();

                    aryOutput = CClientManager.handleMonitorOperation(strFile, intCount);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    if (data != null) {
                        int intCode = displayServerResponse(data, ECommand.MONITOR);

                        if (intCode == ECommand.ACK.getCode()) {
                            new CUDPServer_MultiCast().execute(strFile, intCount);
                        }
                    }
                    //Start Mointor
                    //displayServerResponse(data, ECommand.WRITE);
                    break;
                case MOVE:
                    strFile = getStringChoice();
                    String strFileNew = getStringChoice();

                    aryOutput = CClientManager.handleRenameOperation(strFile, strFileNew);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    if (data != null) {
                        displayServerResponse(data, ECommand.MOVE);
                    }
                    break;
                case ACK:
                    break;
                case LASTMODI:

                    break;
                case EXIT:
                    break;
                default:
                    System.out.println("Invalid Choice");
                    break;
            }

        } while (intChoice != 99);

        System.out.println("\nThank you for using the application.");
    }

    private static void displayMainMenu() {
        System.out.println("\n" + new String(new char[50]).replace("\0", "="));
        System.out.print("|" + new String(new char[20]).replace("\0", " "));
        System.out.print("Main Menu");
        System.out.println(new String(new char[19]).replace("\0", " ") + "|");
        System.out.println(new String(new char[50]).replace("\0", "="));

        System.out.println("1. Read File Data");
        System.out.println("2. Write File Data");
        System.out.println("3. Delete File Data");
        System.out.println("4. Create New File");
        System.out.println("5. Move/Rename File");
        System.out.println("6. Monitor File");
        System.out.println("99. Exit the application");
    }

    private static int getIntChoice(String pStrQns) {
        System.out.print(pStrQns);
        int intChoice = sc.nextInt();
        sc.nextLine();

        return intChoice;
    }

    private static int getIntChoice() {
        return getIntChoice("Please enter your choice: ");
    }

    private static String getStringChoice() {
        System.out.print("Please enter String value: ");

        return sc.nextLine();
    }

}
