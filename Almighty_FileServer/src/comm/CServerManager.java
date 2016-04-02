/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

import static comm.CNetworkManager.unmarshallInt;
import static comm.CNetworkManager.unmarshallString;

/**
 *
 * @author Bryden
 */
public class CServerManager {

    public static String performOperation(byte[] pAryData) {

        
        //Need to provide start offset
        ECommand objCommand = ECommand.getCommand(unmarshallInt(pAryData));

        
        String strPathName = unmarshallString(pAryData).toString();
        
        return "";
    }

}
