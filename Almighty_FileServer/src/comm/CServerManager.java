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
			result = deleteFromFile(pAryData, offset);
			addToResult(lstBytes, marshallString(result));
			break;
			
		case ERROR:
			break;
			
		case MONITOR:
			break;
			
		case MOVE:
			result = moveOrRenameFile(pAryData, offset);
			addToResult(lstBytes, marshallString(result));
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
			result = writeToFile(pAryData, offset);
			addToResult(lstBytes, marshallString(result));
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
    	
    	String data = unmarshallString(pAryData, offset).toString();
    	
    	IO_STATUS ioStatus = CFileFactory.writeToFile(strPathName, fileOffset, data);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			return "WRITE ERROR: FILE NOT FOUND LA!";
		case OFFSET_EXCEEDS_LENGTH:
			return "WRITE ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.";
		case SUCCESS:
			return "Written data to '" + strPathName + "' successfully.";
		default:
			return "";
		}
    }
    
    private static String deleteFromFile(byte[] pAryData, int offset) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	offset += strPathName.length() + 4;
    	
    	int fileOffset = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	int numBytesToDelete = unmarshallInt(pAryData, offset);
    	
    	IO_STATUS ioStatus = CFileFactory.deleteFromFile(
    			strPathName, fileOffset, numBytesToDelete);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			return "DELETE ERROR: FILE NOT FOUND LA!";
		case OFFSET_EXCEEDS_LENGTH:
			return "DELETE ERROR: OFFSET EXCEEDS LENGTH LA! Please contact King Chody for further assistance.";
		case SUCCESS:
			return "Deleted data from '" + strPathName + "' successfully.";
		default:
			return "";
		}
    }
    
    private static String moveOrRenameFile(byte[] pAryData, int offset) throws IOException {
    	
    	String strPathNameOld = unmarshallString(pAryData, offset).toString();
    	offset += strPathNameOld.length() + 4;
    	
    	String strPathNameNew = unmarshallString(pAryData, offset).toString();
    	
    	IO_STATUS ioStatus = CFileFactory.moveOrRenameFile(strPathNameOld, strPathNameNew);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			return "MOVE/RENAME ERROR: FILE NOT FOUND LA!";
		case FILE_NAME_ALREADY_EXISTS:
			return "MOVE/RENAME ERROR: File with same name exists at destination.";
		case SUCCESS:
			return "Moved/Renamed from '" + strPathNameOld + "' to '"
					+ strPathNameNew + "' successfully.";
		default:
			return "";
		}
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
