package main;

import io.CFileFactory;
import io.CFileFactory.IO_STATUS;

import java.io.IOException;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CMain {

    public static void main(String... pAryArgs) throws IOException {

    	// Test create file
        if(CFileFactory.createFile("subDir1_2/abc.txt", "Hello World")) {
        	System.out.println("File created successfully!");
        }
        
        // Test delete from file
        IO_STATUS ioStatus = CFileFactory.deleteFromFile("subDir1_2/abc.txt", 11, 10);

        switch (ioStatus) {
        case FILE_NOT_FOUND:
        	System.out.println("DELETE ERROR: FILE NOT FOUND LA!");
        	break;
        case OFFSET_EXCEEDS_LENGTH:
        	System.out.println("DELETE ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.");
        	break;
        case SUCCESS:
        	System.out.println("DELETED data from file successfully!");
        	break;
        }
        
        /*// Test write to file
		IO_STATUS ioStatus = CFileFactory.writeToFile("subDir1_2/abc.txt", 11, " 4W3S0M3");
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			System.out.println("WRITE ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			System.out.println("WRITE ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.");
			break;
		case SUCCESS:
			System.out.println("Written data to file successfully!");
			break;
		}
        
		// Test read from file
        StringBuilder sb = new StringBuilder();
		ioStatus = CFileFactory.readFromFile(
				"subDir1_1/subDir2_1/subDir3_2/abc.txt", 0, 5, sb);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			System.out.println("READ ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			System.out.println("READ ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.");
			break;
		case SUCCESS:
			System.out.println("READ DATA: " + sb.toString());
			break;
		}*/
    }
}
