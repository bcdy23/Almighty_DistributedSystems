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
import java.net.UnknownHostException;

/**
 *
 * @author Bryden
 */
public class CUDPClient {

    public static void sendData(String pStrAdd, String pStrData) throws SocketException, UnknownHostException, IOException {
        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        
        InetAddress address = InetAddress.getByName(pStrAdd);
        DatagramPacket packet = new DatagramPacket(pStrData.getBytes(), pStrData.length(), address, 4445);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Recieved : " + received);

        socket.close();
    }
}
