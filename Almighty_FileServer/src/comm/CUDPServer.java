/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author Bryden
 */
public class CUDPServer {

    protected DatagramSocket socket = null;

    public CUDPServer() throws SocketException {
        socket = new DatagramSocket(4445);
    }

    public void execute() throws IOException {

        byte[] aryBuffer = new byte[256];

        DatagramPacket objPacket = new DatagramPacket(aryBuffer, aryBuffer.length);
        socket.receive(objPacket);
        
        String received = new String(objPacket.getData(), 0, objPacket.getLength());
        System.out.println("Recieved : " + received);

        String objResponse = "Hello Prof Gosu";

        InetAddress objAddress = objPacket.getAddress();

        int intPort = objPacket.getPort();

        objPacket = new DatagramPacket(objResponse.getBytes(), objResponse.length(), objAddress, intPort);
        
        socket.send(objPacket);
        
        socket.close();

    }
}
