package comm;

import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;
import static comm.CNetworkManager.marshallString;
import io.CFileFactory;
import io.CFileFactory.IO_STATUS;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CServerManager {

    public static byte[] performOperation(byte[] pAryData) throws IOException {

        int offset = 0;
    	
        // Need to provide start offset
        ECommand objCommand = ECommand.getCommand(unmarshallInt(pAryData, 0));
        offset += 4;
        
        // Byte arraylist for storing results
        ArrayList<Byte> lstBytes = new ArrayList<>();
        String result = "";
        
        switch(objCommand) {
        
		case ACK:
			break;
			
		case CREATE:
			result = createFile(pAryData, offset);
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
			StringBuilder sb = new StringBuilder();
			result = readFromFile(pAryData, offset, sb);
			addToResult(lstBytes, marshallString(result));
			addToResult(lstBytes, marshallString(sb.toString()));
			break;
			
		case UPDATE:
			break;
			
		case WRITE:
			
			break;
			
		default:
			break;
        }
        
        return convertResult(lstBytes);
    }
    
    private static String createFile(byte[] pAryData, int offset) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	
    	if(CFileFactory.createFile(strPathName, "")) {
        	return "File created successfully!";
        }
    	
    	return "Create File Error: File already exists!";
    }
    
    private static String readFromFile(byte[] pAryData, int offset, StringBuilder sb) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	offset += strPathName.length() + 4;
    	
    	int fileOffset = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	int numBytesToRead = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	IO_STATUS ioStatus = CFileFactory.readFromFile(
    			strPathName, fileOffset, numBytesToRead, sb);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			return "READ ERROR: FILE NOT FOUND LA!";
		case OFFSET_EXCEEDS_LENGTH:
			return "READ ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.";
		case SUCCESS:
			return "Read data from '" + strPathName + "' successfully.";
		default:
			return "";
		}
    }
    
    private static String writeToFile(byte[] pAryData, int offset) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	offset += strPathName.length() + 4;
    	
    	int fileOffset = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	int numBytesToRead = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	/*IO_STATUS ioStatus = CFileFactory.readFromFile(
    			strPathName, fileOffset, numBytesToRead, sb);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			return "READ ERROR: FILE NOT FOUND LA!";
		case OFFSET_EXCEEDS_LENGTH:
			return "READ ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.";
		case SUCCESS:
			return "Read data from '" + strPathName + "' successfully.";
		default:
			return "";
		}*/
    }
    
    private static void addToResult(ArrayList<Byte> lstBytes, byte[] arrBytes) {
    	
    	for(byte b : arrBytes) {
    		lstBytes.add(b);
    	}
    }
    
    private static byte[] convertResult(ArrayList<Byte> lstBytes) {
    	
    	byte[] arrBytes = new byte[lstBytes.size()];
    	for(int i = 0; i < lstBytes.size(); i++) {
    		arrBytes[i] = lstBytes.get(i);
    	}
    	
    	return arrBytes;
    }
}
