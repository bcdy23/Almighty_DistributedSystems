package main;

import io.CFileFactory;
import io.CFileFactory.READ_STATUS;

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
        
        StringBuilder sb = new StringBuilder();
		READ_STATUS rs = CFileFactory.readFromFile(
				"subDir1_1/subDir2_1/subDir3_2/abc.txt", 11, 5, sb);
		
		switch (rs) {
		case FILE_NOT_FOUND:
			System.out.println("READ ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			System.out.println("READ ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.");
			break;
		case SUCCESS:
			System.out.println(sb.toString());
			break;
		}
    }
}
