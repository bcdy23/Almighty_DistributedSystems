/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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
    public static void main(String[] args) {
        System.out.println("Initalizing system..\n");

        System.out.println("\nSystem initialization completed!");

        System.out.println("\nWelcome to Almighty Distributed File System");

        int intChoice;

        do {
            displayMainMenu();
            intChoice = getChoice();
            
            
            
        } while (intChoice != 6);

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
        System.out.println("5. Monitor File Changes");
        System.out.println("6. Exit the application");
    }

    private static int getChoice() {
        System.out.println("Please enter your choice: ");
        int intChoice = sc.nextInt();
        sc.nextLine();

        return intChoice;
    }

}
