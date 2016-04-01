/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import comm.CUDPClient;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 * @author Bryden
 */
public class TestClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException {
        // TODO code application logic here

        CUDPClient.sendData("172.22.248.33", "GOD Gosu");
    }

}
