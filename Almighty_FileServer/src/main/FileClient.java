/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import client.CClientManager;
import comm.CNetworkManager;
import comm.CServerManager;
import static comm.CNetworkManager.marshallString;
import static comm.CNetworkManager.unmarshallString;
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
    	
    	byte[] arrBytes = CServerManager.performOperation(
    			CClientManager.handleReadOperation(
    					"subDir1_2/abcdefg.txt", 0, 5));
    	
    	int offset = 0;
    	String resultStr = CNetworkManager.unmarshallString(arrBytes, offset).toString();
    	offset += 4 + resultStr.length();
    	
    	String readContents = CNetworkManager.unmarshallString(arrBytes, offset).toString();
    	
    	System.out.println(resultStr);
    	System.out.println(readContents);
    	
    	arrBytes = CServerManager.performOperation(
    			CClientManager.handleRenameOperation(
    					"subDir1_1/abc.txt", "subDir1_3/abc.txt"));
    	
    	/*resultStr = CNetworkManager.unmarshallString(arrBytes, 0).toString();
    	System.out.println(resultStr);*/
    	
        /*System.out.println("Initalizing system..\n");

        System.out.println("\nSystem initialization completed!");

        System.out.println("\nWelcome to Almighty Distributed File System");

        int intChoice;
        String strFile;

        do {
            displayMainMenu();
            intChoice = getIntChoice();

            switch (ECommand.getCommand(intChoice)) {
                case READ:

                    strFile = getStringChoice();

                    int intOffset = getIntChoice();

                    String strData = getStringChoice();

                    break;
                case WRITE:

                    break;
                case DELETE:

                    break;
                case CREATE:
                    strFile = getStringChoice();

                    
                    
                    break;
                case MONITOR:

                    break;
                default:
                    System.out.println("Invalid Choice");
            }

        } while (intChoice != 6);

        System.out.println("\nThank you for using the application.");*/
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
        System.out.println("5. Monitor File Changes");
        System.out.println("6. Exit the application");
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
