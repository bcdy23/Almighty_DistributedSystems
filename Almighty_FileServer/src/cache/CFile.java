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

    private long lngLastModified;
    private long lngLastServerModified;

    private long lngFileLength;

    public long getFileLength() {
        return lngFileLength;
    }

}
