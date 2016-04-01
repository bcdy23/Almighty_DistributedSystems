/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import comm.CUDPServer;
import java.io.IOException;
import java.net.SocketException;

/**
 *
 * @author Bryden
 */
public class TestServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException, IOException {
        // TODO code application logic here      
        CUDPServer objServer = new CUDPServer();
        
        objServer.execute();    
    }
    
}
