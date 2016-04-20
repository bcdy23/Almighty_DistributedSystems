package io;

import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import settings.CSettingManager;

public class CFileFactory {

    private static Path objFolderPath = null;

    private CFileFactory() {

    }

    public static void createFile(String pStrFile, String pStrContents) throws IOException {

        objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));

        Path objFilePath = objFolderPath.resolve(pStrFile);
        if (Files.notExists(objFilePath.getParent())) {
            createFolder(objFilePath.getParent());
        }

        Files.write(objFilePath, pStrContents.getBytes());
    }

    public static void createFolder(Path pObjFilePath) throws IOException {

        Files.createDirectories(pObjFilePath);
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
        if (!Files.exists(objFilePath)) {
            return IO_STATUS.FILE_NOT_FOUND;
        }

        InputStream is = getFile_InputStream(objFilePath);
        byte[] bytesArr = new byte[numBytes];

        // Reset input stream, tries to skip 'offset' number of bytes
        if (is.skip(offset) < offset) {

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
        if (!Files.exists(objFilePath)) {
            return IO_STATUS.FILE_NOT_FOUND;
        }

        // Gets the SeekableByteChannel for writing to file
        SeekableByteChannel sbc = Files.newByteChannel(objFilePath,
                StandardOpenOption.READ, StandardOpenOption.WRITE);

        // Tries to offset the current position in the file
        if (offset > sbc.size()) {

            sbc.close();
            return IO_STATUS.OFFSET_EXCEEDS_LENGTH;
        } else {
            sbc.position(offset);
        }

        long remaining = sbc.size() - sbc.position();
        ByteBuffer rbb = ByteBuffer.allocate((int) remaining);
        sbc.read(rbb);
        String remainingStr = new String(rbb.array());

        // Writes to the file
        data += remainingStr;
        sbc.position(offset);
        sbc.write(ByteBuffer.wrap(data.getBytes()));

        sbc.close();
        return IO_STATUS.SUCCESS;
    }

    public static IO_STATUS deleteFromFile(String pathname, int offset, int numBytes) throws IOException {

        objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePath = objFolderPath.resolve(pathname);
        if (!Files.exists(objFilePath)) {
            return IO_STATUS.FILE_NOT_FOUND;
        }

        // Gets the SeekableByteChannel for writing to file
        SeekableByteChannel sbc = Files.newByteChannel(objFilePath,
                StandardOpenOption.READ, StandardOpenOption.WRITE);

        // Tries to offset the current position in the file
        if (offset > sbc.size()) {

            sbc.close();
            return IO_STATUS.OFFSET_EXCEEDS_LENGTH;
        } else {
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
        /*System.out.println("# of deleted bytes: " + numDeletedBytes);*/

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
        if (!Files.exists(objFilePathOld)) {
            return IO_STATUS.FILE_NOT_FOUND;
        }

        // Check if a file with similar name exists at the target destination
        if (Files.exists(objFilePathNew)) {
            return IO_STATUS.FILE_NAME_ALREADY_EXISTS;
        }

        // Rename
        if (objFilePathOld.getParent().equals(objFilePathNew.getParent())) {

            Files.move(objFilePathOld, objFilePathOld.resolveSibling(
                    objFilePathNew.getFileName()));
        } // Move
        else {

            if (Files.notExists(objFilePathNew.getParent())) {
                createFolder(objFilePathNew.getParent());
            }

            Files.move(objFilePathOld, objFilePathNew.getParent().resolve(
                    objFilePathNew.getFileName()));
        }

        return IO_STATUS.SUCCESS;
    }

    public static IO_STATUS findFile(String pathname) throws IOException {

        objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePath = objFolderPath.resolve(pathname);
        if (!Files.exists(objFilePath)) {
            return IO_STATUS.FILE_NOT_FOUND;
        }

        return IO_STATUS.SUCCESS;
    }

    public static long getLastModifiedTime(String pathname) throws IOException {

        objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePath = objFolderPath.resolve(pathname);
        BasicFileAttributes attrs = Files.readAttributes(objFilePath, BasicFileAttributes.class);

        return attrs.lastModifiedTime().toMillis();
    }

    public static int getFileSize(String pathname) throws IOException {

        objFolderPath = Paths.get(CSettingManager.getSetting("File_Location"));
        Path objFilePath = objFolderPath.resolve(pathname);
        BasicFileAttributes attrs = Files.readAttributes(objFilePath, BasicFileAttributes.class);

        return ((int) attrs.size());
    }

    public enum IO_STATUS {
        SUCCESS,
        FILE_NOT_FOUND,
        OFFSET_EXCEEDS_LENGTH,
        FILE_NAME_ALREADY_EXISTS;
    }
}
