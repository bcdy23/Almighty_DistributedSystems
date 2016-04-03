/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

/**
 *
 * @author Bryden
 */
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
