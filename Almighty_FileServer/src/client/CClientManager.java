/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import static comm.CNetworkManager.marshallInt;
import static comm.CNetworkManager.marshallString;
import comm.ECommand;

/**
 *
 * @author Bryden
 */
public class CClientManager {

    public static void handleReadOperation(String pStrFile, int pIntOffset, int pIntBytes) {

        byte[] aryCommand = marshallInt(ECommand.READ.getCode());
        byte[] aryFile = marshallString(pStrFile);
        byte[] aryOffset = marshallInt(pIntOffset);
        byte[] aryCount = marshallInt(pIntBytes);

        byte[] aryOutput = new byte[aryCommand.length + aryFile.length + aryOffset.length + aryCount.length];

        //System.arraycopy(marshallInt(intLength), 0, aryData, 0, 4);

        //System.arraycopy(pStrData.getBytes(), 0, aryData, 4, pStrData.length());

    }

}
