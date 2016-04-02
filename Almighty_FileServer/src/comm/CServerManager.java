package comm;

import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;
import static comm.CNetworkManager.marshallInt;
import static comm.CNetworkManager.marshallString;

import io.CFileFactory;

import java.io.IOException;

import java.util.ArrayList;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CServerManager {

    public static String performOperation(byte[] pAryData) throws IOException {

        int offset = 0;
    	
        // Need to provide start offset
        ECommand objCommand = ECommand.getCommand(unmarshallInt(pAryData, 0));
        offset += 4;
        
        // Byte arraylist for storing results
        ArrayList<Byte> lstBytes = new ArrayList<>();
        switch(objCommand) {
        
		case ACK:
			break;
			
		case CREATE:
			String result = createFile(pAryData, offset);
			addToResult(lstBytes, marshallInt(4 + result.length()));
			addToResult(lstBytes, marshallString(result));
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
        
        return "";
    }
    
    private static String createFile(byte[] pAryData, int offset) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	
    	if(CFileFactory.createFile(strPathName, "")) {
        	return "File created successfully!";
        }
    	
    	return "Create File Error: File already exists!";
    }
    
    private static void addToResult(ArrayList<Byte> lstBytes, byte[] arrBytes) {
    	
    	for(byte b : arrBytes) {
    		lstBytes.add(b);
    	}
    }
}
