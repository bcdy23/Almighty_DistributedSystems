/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import client.CClientManager;

import comm.CNetworkManager;
import comm.CUDPClient;
import static comm.CUDPClient.connectionEstablish;
import comm.ECommand;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Bryden
 */
public class FileClient {

    private static Scanner sc = new Scanner(System.in);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        String strServerAdd = "172.22.248.33";

//        byte[] arrBytes = CServerManager.performOperation(
//                CClientManager.handleReadOperation(
//                        "subDir1_2/abcdefg.txt", 0, 5));
//
//        int offset = 0;
//        String resultStr = CNetworkManager.unmarshallString(arrBytes, offset).toString();
//        offset += 4 + resultStr.length();
//
//        String readContents = CNetworkManager.unmarshallString(arrBytes, offset).toString();
//
//        System.out.println(resultStr);
//        System.out.println(readContents);
//
//        arrBytes = CServerManager.performOperation(
//                CClientManager.handleRenameOperation(
//                        "subDir1_1/abc.txt", "subDir1_1/abc123.txt"));
//
//        resultStr = CNetworkManager.unmarshallString(arrBytes, 0).toString();
//        System.out.println(resultStr);
        System.out.println("Initalizing system..\n");

        connectionEstablish(strServerAdd);

        System.out.println("\nSystem initialization completed!");

        System.out.println("\nWelcome to Almighty Distributed File System");

        int intChoice;
        String strFile;

        int intOffset;
        int intCount;

        byte[] aryOutput;

        do {
            displayMainMenu();
            intChoice = getIntChoice();

            switch (ECommand.getCommand(intChoice)) {
                case READ:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    intCount = getIntChoice();

                    aryOutput = CClientManager.handleReadOperation(strFile, intOffset, intCount);

                    byte[] data = CUDPClient.sendData(strServerAdd, aryOutput);

                    int z = CNetworkManager.unmarshallInt(data, 0);

                    String x = CNetworkManager.unmarshallString(data, 4).toString();

                    String y = CNetworkManager.unmarshallString(data, x.length() + 8).toString();

                    System.out.println(z);
                    System.out.println(x);
                    System.out.println(y);
                    System.out.println(CNetworkManager.unmarshallLong(data, x.length() + y.length() + 12));

                    break;
                case WRITE:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    String strData = getStringChoice();

                    aryOutput = CClientManager.handleWriteOperation(strFile, intOffset, strData);

                    CUDPClient.sendData(strServerAdd, aryOutput);

                    break;
                case DELETE:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    intCount = getIntChoice();

                    aryOutput = CClientManager.handleDeleteOperation(strFile, intOffset, intCount);

                    CUDPClient.sendData(strServerAdd, aryOutput);
                    break;
                case CREATE:
                    strFile = getStringChoice();

                    aryOutput = CClientManager.handleCreateOperation(strFile);

                    CUDPClient.sendData(strServerAdd, aryOutput);

                    break;
                case MONITOR:
                    strFile = getStringChoice();
                    intCount = getIntChoice();

                    aryOutput = CClientManager.handleMonitorOperation(strFile, intCount);

                    CUDPClient.sendData(strServerAdd, aryOutput);
                    break;
                case MOVE:
                    strFile = getStringChoice();
                    String strFileNew = getStringChoice();

                    aryOutput = CClientManager.handleRenameOperation(strFile, strFileNew);

                    CUDPClient.sendData(strServerAdd, aryOutput);
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

    private static int getIntChoice() {
        System.out.print("Please enter your choice: ");
        int intChoice = sc.nextInt();
        sc.nextLine();

        return intChoice;
    }

    private static String getStringChoice() {
        System.out.print("Please enter String value: ");

        return sc.nextLine();
    }

}
