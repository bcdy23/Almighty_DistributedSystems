package cache;

import java.util.HashMap;

/**
 *
 * @author Bryden
 */
public class CFileCacheManager {

    public static final int intBlockSize = 32;

    public static final HashMap<String, HashMap<Integer, byte[]>> objCache = new HashMap<>();

    public static final HashMap<String, CFile> objFileCache = new HashMap<>();

    public static boolean fileInCache(String pStrFileName) {
        return objFileCache.containsKey(pStrFileName);
    }

    public static long getFileSize(String pStrFileName) {
        if (!objFileCache.containsKey(pStrFileName)) {
            return -1;
        }

        return objFileCache.get(pStrFileName).getFileLength();
    }

    public static byte[] getCacheBlock(String strFileName, int pIntBlockLocation) {
        if (!objCache.containsKey(strFileName)) {
            return null;
        }

        if (!objCache.get(strFileName).containsKey(pIntBlockLocation)) {
            return null;
        }

        return objCache.get(strFileName).get(pIntBlockLocation);
    }

}
