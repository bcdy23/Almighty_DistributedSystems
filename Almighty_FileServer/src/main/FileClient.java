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

    private static void displayServerResponse(byte[] pAryData, ECommand pObjCommand) {
        int intCode = CNetworkManager.unmarshallInt(pAryData, 0);

        String strMsg = CNetworkManager.unmarshallString(pAryData, 4).toString();

        System.out.println(strMsg);

        if (intCode == ECommand.ACK.getCode()) {

            if (pObjCommand == ECommand.READ) {
                String strReadData = CNetworkManager.unmarshallString(pAryData, strMsg.length() + 8).toString();

                System.out.println("DATA READ : " + strReadData);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

//        String strServerAdd = "172.22.248.33";
        String strServerAdd = "127.0.0.1";
        System.out.println("Initalizing system..\n");

        connectionEstablish(strServerAdd);

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

                    aryOutput = CClientManager.handleReadOperation(strFile, intOffset, intCount);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    displayServerResponse(data, ECommand.READ);

                    break;
                case WRITE:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    String strData = getStringChoice();

                    aryOutput = CClientManager.handleWriteOperation(strFile, intOffset, strData);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    displayServerResponse(data, ECommand.WRITE);

                    break;
                case DELETE:

                    strFile = getStringChoice();
                    intOffset = getIntChoice();
                    intCount = getIntChoice();

                    aryOutput = CClientManager.handleDeleteOperation(strFile, intOffset, intCount);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    displayServerResponse(data, ECommand.DELETE);
                    break;
                case CREATE:
                    strFile = getStringChoice();

                    aryOutput = CClientManager.handleCreateOperation(strFile);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    displayServerResponse(data, ECommand.CREATE);

                    break;
                case MONITOR:
                    strFile = getStringChoice();
                    intCount = getIntChoice();

                    aryOutput = CClientManager.handleMonitorOperation(strFile, intCount);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    //Start Mointor
                    //displayServerResponse(data, ECommand.WRITE);
                    break;
                case MOVE:
                    strFile = getStringChoice();
                    String strFileNew = getStringChoice();

                    aryOutput = CClientManager.handleRenameOperation(strFile, strFileNew);

                    data = CUDPClient.sendData(strServerAdd, aryOutput);

                    displayServerResponse(data, ECommand.MOVE);
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
