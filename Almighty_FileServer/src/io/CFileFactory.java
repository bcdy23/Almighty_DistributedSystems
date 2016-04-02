package io;

import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import settings.CSettingManager;

/**
 *
 * @author King Chody
 */
public class CFileFactory {

    private static Path objFolderPath = null;

    private CFileFactory() {

    }

    /*public static boolean isHiddenFile(Path pObjFilePath) {
        try {
            return Files.isHidden(pObjFilePath);
        } catch (IOException ex) {
            return false;
        }
    }*/

    /*public static void createFile(Path pObjFilePath, String pStrContents) throws IOException {
    	
        Files.write(pObjFilePath, pStrContents.getBytes());
    }*/

    /**
     * Returns true if file created succcessfully, false if file already exists.
     * 
     * @param pStrFile		String representation of the file path
     * @param pStrContents	Contents to be written to the file
     * @return				True if file created successfully, false if file already exists
     * @throws IOException	
     */
    public static boolean createFile(String pStrFile, String pStrContents) throws IOException {
    	
        objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        
        Path objFilePath = objFolderPath.resolve(pStrFile);
        if(Files.notExists(objFilePath.getParent())) {
        	createFolder(objFilePath.getParent());
        }
        
        if(Files.exists(objFilePath)) {
        	return false;
        }
        
        Files.write(objFilePath, pStrContents.getBytes());
        return true;
    }

    public static void createFolder(Path pObjFilePath) throws IOException {
    	
    	Files.createDirectories(pObjFilePath);
        //Files.createDirectory(pObjFilePath);
    }

    /*public static void renameFile(Path pObjFilePath, String pStrNewName) throws IOException {
        Files.move(pObjFilePath, pObjFilePath.resolveSibling(pStrNewName), StandardCopyOption.ATOMIC_MOVE);
    }

    public static void renameFolder(Path pObjFilePath, String pStrNewName) throws IOException {
        Files.move(pObjFilePath, pObjFilePath.resolveSibling(pStrNewName), StandardCopyOption.ATOMIC_MOVE);
    }*/

    public static void updateFile(Path pObjFilePath, String pStrContents) throws IOException {
        Files.write(pObjFilePath, pStrContents.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void deleteFile(Path pObjFilePath) throws IOException {
        Files.deleteIfExists(pObjFilePath);
    }

    public static void deleteFolder(Path pObjFilePath) throws IOException {
        Files.walkFileTree(pObjFilePath, new FileVisitor<Path>() {

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                System.out.println(exc.toString());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static InputStream getFile_InputStream(String pStrPath) throws IOException {
        return getFile_InputStream(Paths.get(pStrPath));
    }

    private static InputStream getFile_InputStream(Path pObjPath) throws IOException {
        return Files.newInputStream(pObjPath, StandardOpenOption.READ);
    }
    
	public static IO_STATUS readFromFile(String pathname, int offset,
			int numBytes, StringBuilder sb) throws IOException {

		objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePath = objFolderPath.resolve(pathname);
        if(!Files.exists(objFilePath)) {
        	return IO_STATUS.FILE_NOT_FOUND;
        }
        
		InputStream is = getFile_InputStream(objFilePath);
		byte[] bytesArr = new byte[numBytes];
		
		// Reset input stream, tries to skip 'offset' number of bytes
		if(is.skip(offset) < offset) {
			
			is.close();
			return IO_STATUS.OFFSET_EXCEEDS_LENGTH;
		}
		
		// Read from the input stream, result appended to the StringBuilder
		int is_result = is.read(bytesArr, 0, numBytes);
		if (is_result != -1) {
			
			sb.append(new String(bytesArr));
		}

		is.close();
		return IO_STATUS.SUCCESS;
	}
	
	public static IO_STATUS writeToFile(String pathname, int offset, String data) throws IOException {

		objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePath = objFolderPath.resolve(pathname);
        if(!Files.exists(objFilePath)) {
        	return IO_STATUS.FILE_NOT_FOUND;
        }
		
        // Gets the SeekableByteChannel for writing to file
		SeekableByteChannel sbc = Files.newByteChannel(objFilePath,
				StandardOpenOption.READ, StandardOpenOption.WRITE);
		
		// Tries to offset the current position in the file
		if(offset > sbc.size()) {
			
			sbc.close();
			return IO_STATUS.OFFSET_EXCEEDS_LENGTH;
		}
		else {
			sbc.position(offset);
		}
		
		long remaining = sbc.size() - sbc.position();
		ByteBuffer rbb = ByteBuffer.allocate((int) remaining);
		sbc.read(rbb);
		String remainingStr = new String(rbb.array());
		
		// Writes to the file
		data +=  remainingStr;
		sbc.position(offset);
		sbc.write(ByteBuffer.wrap(data.getBytes()));
		
		sbc.close();
		return IO_STATUS.SUCCESS;
	}
	
	public static IO_STATUS deleteFromFile(String pathname, int offset, int numBytes) throws IOException {

		objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePath = objFolderPath.resolve(pathname);
        if(!Files.exists(objFilePath)) {
        	return IO_STATUS.FILE_NOT_FOUND;
        }
		
        // Gets the SeekableByteChannel for writing to file
		SeekableByteChannel sbc = Files.newByteChannel(objFilePath,
				StandardOpenOption.READ, StandardOpenOption.WRITE);
		
		// Tries to offset the current position in the file
		if(offset > sbc.size()) {
			
			sbc.close();
			return IO_STATUS.OFFSET_EXCEEDS_LENGTH;
		}
		else {
			sbc.position(offset);
		}
		
		long remaining = sbc.size() - sbc.position();
		ByteBuffer rbb = ByteBuffer.allocate((int) remaining);
		sbc.read(rbb);
		String strAfterOffset = new String(rbb.array());
		
		// Remove from the contents the specified number of bytes
		if (numBytes > strAfterOffset.length()) {
			strAfterOffset = "";
		} else {
			strAfterOffset = strAfterOffset.substring(numBytes);
		}
		
		long numDeletedBytes = remaining - strAfterOffset.length();
		System.out.println("# of deleted bytes: " + numDeletedBytes);
		
		// Writes to the file (Moving the contents to replace deleted contents)
		sbc.position(offset);
		sbc.write(ByteBuffer.wrap(strAfterOffset.getBytes()));
		
		// Truncate based on actual number of deleted bytes
		sbc.truncate(sbc.size() - numDeletedBytes);
		
		sbc.close();
		return IO_STATUS.SUCCESS;
	}
	
	public static IO_STATUS moveOrRenameFile(String pathnameOld, String pathnameNew) throws IOException {
		
		objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePathOld = objFolderPath.resolve(pathnameOld);
        Path objFilePathNew = objFolderPath.resolve(pathnameNew);
        
        // Check if the specified file exists
        if(!Files.exists(objFilePathOld)) {
        	return IO_STATUS.FILE_NOT_FOUND;
        }
        
        // Check if a file with similar name exists at the target destination
        if(Files.exists(objFilePathNew)) {
        	return IO_STATUS.FILE_NAME_ALREADY_EXISTS;
        }
        
        // Rename
        if(objFilePathOld.getParent().equals(objFilePathNew.getParent())) {
        	
        	Files.move(objFilePathOld, objFilePathOld.resolveSibling(
        			objFilePathNew.getFileName()));
        }
        // Move
        else {
        	
        	if(Files.notExists(objFilePathNew.getParent())) {
            	createFolder(objFilePathNew.getParent());
            }
        	
        	Files.move(objFilePathOld, objFilePathNew.getParent().resolve(
        			objFilePathOld.getFileName()));
        }
        
        return IO_STATUS.SUCCESS;
	}
    
    public enum IO_STATUS {
    	
    	SUCCESS,
    	FILE_NOT_FOUND,
    	OFFSET_EXCEEDS_LENGTH,
    	FILE_NAME_ALREADY_EXISTS;
    }
}
