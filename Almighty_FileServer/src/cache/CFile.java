package cache;

public class CFile {

    private long lngLastValidate;
    private long lngLastServerModified;

    private long lngFileLength;

    public long getFileLength() {
        return lngFileLength;
    }

    public long getLocalLastValidate() {
        return lngLastValidate;
    }

    public long getServerLastModified() {
        return lngLastServerModified;
    }

    public void setLocalLastValidate() {
        lngLastValidate = System.currentTimeMillis();
    }

    public void setServerLastModified(long pLngServer) {
        lngLastServerModified = pLngServer;
    }

}
