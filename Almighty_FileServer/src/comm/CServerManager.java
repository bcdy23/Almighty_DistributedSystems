package comm;

import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CServerManager {

    public static String performOperation(byte[] pAryData) {

        
        // Need to provide start offset
        ECommand objCommand = ECommand.getCommand(unmarshallInt(pAryData));

        
        String strPathName = unmarshallString(pAryData).toString();
        
        return "";
    }
    
    
}
