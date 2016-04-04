package cache;

import java.util.HashMap;

public class CFileCacheManager {

    public static final int intBlockSize = 32;

    public static final HashMap<String, HashMap<Integer, String>> objCache = new HashMap<>();

    public static final HashMap<String, CFile> objFileCache = new HashMap<>();

    public static void setFileCache(String pStrFileName, String pAryData, long pLngLastModi) {
        CFile objFile = new CFile();

        objFile.setServerLastModified(pLngLastModi);
        objFile.setLocalLastValidate();

        objFileCache.put(pStrFileName, objFile);

        HashMap<Integer, String> objHM = new HashMap<>();

        objHM.put(0, pAryData);

        objCache.put(pStrFileName, objHM);
    }

    public static String getFileCache(String pStrFileName) {
        return objCache.get(pStrFileName).get(0);
    }

    public static boolean fileInCache(String pStrFileName) {
        return objFileCache.containsKey(pStrFileName);
    }

    public static long getFileSize(String pStrFileName) {
        if (!objFileCache.containsKey(pStrFileName)) {
            return -1;
        }

        return objFileCache.get(pStrFileName).getFileLength();
    }

    public static String getCacheBlock(String strFileName, int pIntBlockLocation) {
        if (!objCache.containsKey(strFileName)) {
            return null;
        }

        if (!objCache.get(strFileName).containsKey(pIntBlockLocation)) {
            return null;
        }

        return objCache.get(strFileName).get(pIntBlockLocation);
    }

    public static CFile getFileAtrr(String pStrFileName) {
        return objFileCache.get(pStrFileName);
    }

}
