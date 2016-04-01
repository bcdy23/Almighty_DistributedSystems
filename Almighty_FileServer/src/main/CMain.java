package main;

import io.CFileFactory;
import io.CFileFactory.IO_STATUS;

import java.io.IOException;

import settings.CSettingManager;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CMain {

    public static void main(String... pAryArgs) throws IOException {

        /*CFileFactory.createFile("subDir1_1/subDir2_1/subDir3_2/abc.txt",
        		CSettingManager.getSetting("Welcome_Message"));*/
        
        System.out.println(CSettingManager.getSetting("Welcome_Message"));
        
		IO_STATUS ioStatus = CFileFactory.writeToFile(
				"subDir1_1/subDir2_1/subDir3_2/abc.txt", 47, " AWESOME");
		
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
        
        /*StringBuilder sb = new StringBuilder();
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
			System.out.println(sb.toString());
			break;
		}*/
    }
}
