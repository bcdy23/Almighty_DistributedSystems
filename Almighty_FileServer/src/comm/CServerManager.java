package comm;

import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;
import static comm.CNetworkManager.unmarshallLong;
import static comm.CNetworkManager.marshallInt;
import static comm.CNetworkManager.marshallString;
import static comm.CNetworkManager.marshallLong;

import io.CFileFactory;
import io.CFileFactory.IO_STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author King Chody & Gosu the Minion
 */
public class CServerManager {
	
	// IP Address - <Seq Num, Cached result>
	private static HashMap<String, HashMap<Integer, byte[]>> _serverClientsCache = new HashMap<>();

    public static byte[] performOperation(byte[] pAryData, String pStrAddr) throws IOException {

        int offset = 0;
        
        // Get the sequence number
        int seqNumber = unmarshallInt(pAryData, offset);
        offset += 4;
        
        HashMap<Integer, byte[]> _clientCache = _serverClientsCache.get(pStrAddr);
        if(_clientCache == null) {
        	_clientCache = new HashMap<>();
        	_serverClientsCache.put(pStrAddr, _clientCache);
        }
    	
        // Need to provide start offset
        ECommand objCommand = ECommand.getCommand(unmarshallInt(pAryData, offset));
        offset += 4;
        
        // Print client IP, seq number, and action to be performed.
        System.out.printf("IP: %-15s\tSeq #: %-8d\tAction: %-12s%n",
        		pStrAddr, seqNumber, objCommand.toString());
        
        // Byte arraylist for storing results
        ArrayList<Byte> lstBytes = new ArrayList<>();
        byte[] arrBytes = null;
        
        switch(objCommand) {
        
        case CONN:
         	_clientCache.clear();
         	
         	addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
         	arrBytes = convertResult(lstBytes);
         	
         	System.out.println("Client '" + pStrAddr + "' connected to server.\n");
        	break;
        
		case ACK:
			break;
			
		case CREATE:
			createFile(pAryData, offset, lstBytes);
			
			arrBytes = convertResult(lstBytes);
			printCodeMsg(arrBytes);
			break;
			
		case DELETE:
			// Non-Idempotent
			arrBytes = _clientCache.get(seqNumber);
			if(arrBytes != null) {
				
				System.out.println("One-update semantics, retrieving from cache..");
				printCodeMsg(arrBytes);
				return arrBytes;
			}
			
			// Perform action
			deleteFromFile(pAryData, offset, lstBytes);
			
			arrBytes = convertResult(lstBytes);
			_clientCache.put(seqNumber, arrBytes);
			printCodeMsg(arrBytes);
			
			break;
			
		case ERROR:
			break;
			
		case MONITOR:
			break;
			
		case MOVE:
			// Non-Idempotent
			arrBytes = _clientCache.get(seqNumber);
			if(arrBytes != null) {
				
				System.out.println("One-update semantics, retrieving from cache..");
				printCodeMsg(arrBytes);
				return arrBytes;
			}
			
			// Perform action
			moveOrRenameFile(pAryData, offset, lstBytes);
			
			arrBytes = convertResult(lstBytes);
			_clientCache.put(seqNumber, arrBytes);
			printCodeMsg(arrBytes);
			
			break;
			
		case READ:
			readFromFile(pAryData, offset, lstBytes);
			
			arrBytes = convertResult(lstBytes);
			printCodeMsgContents(arrBytes);
			break;
			
		case UPDATE:
			break;
			
		case WRITE:
			// Non-Idempotent
			arrBytes = _clientCache.get(seqNumber);
			if(arrBytes != null) {
				
				System.out.println("One-update semantics, retrieving from cache..");
				printCodeMsg(arrBytes);
				return arrBytes;
			}
			
			// Perform action
			writeToFile(pAryData, offset, lstBytes);
			
			arrBytes = convertResult(lstBytes);
			_clientCache.put(seqNumber, arrBytes);
			printCodeMsg(arrBytes);
			break;
			
		case LASTMODI:
			getLastModified(pAryData, offset, lstBytes);
			
			arrBytes = convertResult(lstBytes);
			printCodeLastModi(arrBytes);
			break;
			
		default:
			break;
        }
        
        return arrBytes;
    }
    
    private static void createFile(byte[] pAryData, int offset, ArrayList<Byte> lstBytes) throws IOException {
    	
    	String strPathName = unmarshallString(pAryData, offset).toString();
    	
    	CFileFactory.createFile(strPathName, "");
    		
		addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
		addToResult(lstBytes, "File '" + strPathName + "' created successfully!");
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
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "READ ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "READ ERROR: OFFSET EXCEEDS LENGTH LA!");
			break;
		case SUCCESS:
			addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
			addToResult(lstBytes, ("Read data from '" + strPathName + "' successfully."));
			addToResult(lstBytes, sb.toString());
			
			long lastModifiedTime = CFileFactory.getLastModifiedTime(strPathName);
			addToResult(lstBytes, marshallLong(lastModifiedTime));
			
			long fileSize = CFileFactory.getFileSize(strPathName);
			addToResult(lstBytes, marshallLong(fileSize));
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
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "WRITE ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "WRITE ERROR: OFFSET EXCEEDS LENGTH LA!");
			break;
		case SUCCESS:
			addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
			addToResult(lstBytes, "Written data to '" + strPathName + "' successfully.");
			
			long fileSize = CFileFactory.getFileSize(strPathName);
			addToResult(lstBytes, marshallLong(fileSize));
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
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "DELETE ERROR: FILE NOT FOUND LA!");
			break;
		case OFFSET_EXCEEDS_LENGTH:
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "DELETE ERROR: OFFSET EXCEEDS LENGTH LA!");
			break;
		case SUCCESS:
			addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
			addToResult(lstBytes, "Deleted data from '" + strPathName + "' successfully.");
			
			long fileSize = CFileFactory.getFileSize(strPathName);
			addToResult(lstBytes, marshallLong(fileSize));
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
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "MOVE/RENAME ERROR: FILE NOT FOUND LA!");
			break;
		case FILE_NAME_ALREADY_EXISTS:
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, "MOVE/RENAME ERROR: File with same name exists at destination.");
			break;
		case SUCCESS:
			addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
			addToResult(lstBytes, "Moved/Renamed from '" + strPathNameOld + "' to '"
					+ strPathNameNew + "' successfully.");
			break;
		default:
			break;
		}
    }
    
	private static void getLastModified(byte[] pAryData, int offset,
			ArrayList<Byte> lstBytes) throws IOException {

		String strPathName = unmarshallString(pAryData, offset).toString();
    	IO_STATUS ioStatus = CFileFactory.findFile(strPathName);
		
		switch (ioStatus) {
		case FILE_NOT_FOUND:
			addToResult(lstBytes, marshallInt(ECommand.ERROR.getCode()));
			addToResult(lstBytes, marshallLong(0));
			break;
		case SUCCESS:
			addToResult(lstBytes, marshallInt(ECommand.ACK.getCode()));
			addToResult(lstBytes, marshallLong(
					CFileFactory.getLastModifiedTime(strPathName)));
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
    
    private static void printCodeMsg(byte[] arrBytes) {
    	
    	int un_offset = 0;
		
		int un_code = unmarshallInt(arrBytes, un_offset);
		String str_un_code = ECommand.getCommand(un_code).toString();
		un_offset += 4;
		
		String un_msg = unmarshallString(arrBytes, un_offset).toString();
		
		System.out.printf("Result: %-12s\tMsg: %-40s%n%n", str_un_code, un_msg);
    }
    
    private static void printCodeMsgContents(byte[] arrBytes) {
    	
    	int un_offset = 0;
		
		int un_code = unmarshallInt(arrBytes, un_offset);
		String str_un_code = ECommand.getCommand(un_code).toString();
		un_offset += 4;
		
		String un_msg = unmarshallString(arrBytes, un_offset).toString();
		un_offset += (4 + un_msg.length());
		
		String un_contents = unmarshallString(arrBytes, un_offset).toString();
		
		System.out.printf("Result: %-12s\tMsg: %-40s\tContents: %-80s%n%n",
				str_un_code, un_msg, un_contents);
    }
    
    private static void printCodeLastModi(byte[] arrBytes) {
    	
    	int un_offset = 0;
		
		int un_code = unmarshallInt(arrBytes, un_offset);
		String str_un_code = ECommand.getCommand(un_code).toString();
		un_offset += 4;
		
		long un_lastModi = unmarshallLong(arrBytes, un_offset);
		
		System.out.printf("Result: %-12s\tLast Modified: %-20d%n%n", str_un_code, un_lastModi);
    }
}
