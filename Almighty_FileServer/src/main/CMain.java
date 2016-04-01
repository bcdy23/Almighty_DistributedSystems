package main;

import io.CFileFactory;
import java.io.IOException;
import settings.CSettingManager;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CMain {

    public static void main(String... pAryArgs) throws IOException {

        CFileFactory.createFile("subDir1_1/subDir2_1/subDir3_2/abc.txt",
        		CSettingManager.getSetting("Welcome_Message"));
        
        System.out.println(CSettingManager.getSetting("Welcome_Message"));
    }
}
