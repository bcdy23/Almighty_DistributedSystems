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

    public static byte[] sendData(String pStrAdd, byte[] pAryData) throws SocketException, UnknownHostException, IOException {
        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[1024];

        InetAddress address = InetAddress.getByName(pStrAdd);
        DatagramPacket packet = new DatagramPacket(pAryData, pAryData.length, address, 4445);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        byte[] aryOutput = new byte[packet.getData().length];

        System.arraycopy(packet.getData(), 0, aryOutput, 0, packet.getData().length);
        socket.close();

        return aryOutput;
    }
}
