/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

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

        while (true) {
            byte[] aryBuffer = new byte[1024];

            DatagramPacket objPacket = new DatagramPacket(aryBuffer, aryBuffer.length);
            socket.receive(objPacket);

            byte[] aryOutput = CServerManager.performOperation(objPacket.getData(), objPacket.getAddress().getHostAddress());

            InetAddress objAddress = objPacket.getAddress();

            int intPort = objPacket.getPort();

            objPacket = new DatagramPacket(aryOutput, aryOutput.length, objAddress, intPort);

            socket.send(objPacket);
        }
        //socket.close();

    }
}
