package comm;

import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;
import static comm.CNetworkManager.marshallString;
import static comm.CNetworkManager.marshallLong;

import io.CFileFactory;
import io.CFileFactory.IO_STATUS;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CServerManager {

    public static byte[] performOperation(byte[] pAryData, String pStrAddr) throws IOException {

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
			createFile(pAryData, offset, lstBytes);
			break;
			
		case DELETE:
			deleteFromFile(pAryData, offset, lstBytes);
			break;
			
		case ERROR:
			break;
			
		case MONITOR:
			break;
			
		case MOVE:
			moveOrRenameFile(pAryData, offset, lstBytes);
			break;
			
		case READ:
			readFromFile(pAryData, offset, lstBytes);
			break;
			
		case UPDATE:
			break;
			
		case WRITE:
			writeToFile(pAryData, offset, lstBytes);
			break;
			
		default:
			break;
        }
        
        return convertResult(lstBytes);
    }
    
    private static void createFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	
    	if(CFileFactory.createFile(strPathName, "")) {
    		addToResult(lstBytes, "SUCCESS");
        	addToResult(lstBytes, "File created successfully!");
        }
    	
    	addToResult(lstBytes, "ERROR");
    	addToResult(lstBytes, "Create File Error: File already exists!");
    }
    
    private static void readFromFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	offset += strPathName.length() + 4;
    	
    	int fileOffset = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	int numBytesToRead = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	StringBuilder sb = new StringBuilder();
    	IO_STATUS ioStatus = CFileFactory.readFromFile(
    			strPathName, fileOffset, numBytesToRead, sb);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "READ ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "READ ERROR: OFFSET EXCEEDS LENGTH LA!");
			break;
		case SUCCESS:
			addToResult(lstBytes, "SUCCESS");
			addToResult(lstBytes, "Read data from '" + strPathName + "' successfully.");
			addToResult(lstBytes, sb.toString());
			long lastModifiedTime = CFileFactory.getLastModifiedTime(strPathName);
			addToResult(lstBytes, marshallLong(lastModifiedTime));
			break;
		default:
			break;
		}
    }
    
    private static void writeToFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	offset += strPathName.length() + 4;
    	
    	int fileOffset = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	String data = unmarshallString(pAryData, offset).toString();
    	
    	IO_STATUS ioStatus = CFileFactory.writeToFile(strPathName, fileOffset, data);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "WRITE ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "WRITE ERROR: OFFSET EXCEEDS LENGTH LA!");
			break;
		case SUCCESS:
			addToResult(lstBytes, "SUCCESS");
			addToResult(lstBytes, "Written data to '" + strPathName + "' successfully.");
			break;
		default:
			break;
		}
    }
    
    private static void deleteFromFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	offset += strPathName.length() + 4;
    	
    	int fileOffset = unmarshallInt(pAryData, offset);
    	offset += 4;
    	
    	int numBytesToDelete = unmarshallInt(pAryData, offset);
    	
    	IO_STATUS ioStatus = CFileFactory.deleteFromFile(
    			strPathName, fileOffset, numBytesToDelete);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "DELETE ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "DELETE ERROR: OFFSET EXCEEDS LENGTH LA!");
			break;
		case SUCCESS:
			addToResult(lstBytes, "SUCCESS");
			addToResult(lstBytes, "Deleted data from '" + strPathName + "' successfully.");
			break;
		default:
			break;
		}
    }
    
    private static void moveOrRenameFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {
    	
    	String strPathNameOld = unmarshallString(pAryData, offset).toString();
    	offset += strPathNameOld.length() + 4;
    	
    	String strPathNameNew = unmarshallString(pAryData, offset).toString();
    	
    	IO_STATUS ioStatus = CFileFactory.moveOrRenameFile(strPathNameOld, strPathNameNew);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "MOVE/RENAME ERROR: FILE NOT FOUND LA!");
			break;
		case FILE_NAME_ALREADY_EXISTS:
			addToResult(lstBytes, "ERROR");
			addToResult(lstBytes, "MOVE/RENAME ERROR: File with same name exists at destination.");
			break;
		case SUCCESS:
			addToResult(lstBytes, "SUCCESS");
			addToResult(lstBytes, "Moved/Renamed from '" + strPathNameOld + "' to '"
					+ strPathNameNew + "' successfully.");
			break;
		default:
			break;
		}
    }
    
    private static void addToResult(ArrayList<Byte> lstBytes, String str) {
    	addToResult(lstBytes, marshallString(str));
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
