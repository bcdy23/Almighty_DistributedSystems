package comm;

import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CServerManager {

    public static String performOperation(byte[] pAryData) {

        int offset = 0;
    	
        // Need to provide start offset
        ECommand objCommand = ECommand.getCommand(unmarshallInt(pAryData, 0));
        offset += 4;
        
        switch(objCommand) {
		case ACK:
			break;
		case CREATE:
			break;
		case DELETE:
			break;
		case ERROR:
			break;
		case MONITOR:
			break;
		case MOVE:
			break;
		case READ:
			break;
		case UPDATE:
			break;
		case WRITE:
			break;
		default:
			break;
        }
        String strPathName = unmarshallString(pAryData, offset).toString();
        
        return "";
    }
    
    
}
