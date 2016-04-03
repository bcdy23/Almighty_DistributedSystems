/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm;

import static comm.CNetworkManager.marshallInt;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import util.CRandomGenerator;

/**
 *
 * @author Bryden
 */
public class CUDPClient {

    private static int intSeqId = 0;

    private static final int intFailure = 5;

    public static byte[] connectionEstablish(String pStrAdd) throws SocketException, UnknownHostException, IOException {
        byte[] aryData = marshallInt(ECommand.CONN.getCode());
        byte[] arySeq = CNetworkManager.marshallInt(intSeqId);

        byte[] arySent = new byte[aryData.length + arySeq.length];

        System.arraycopy(arySeq, 0, arySent, 0, arySeq.length);
        System.arraycopy(aryData, 0, arySent, arySeq.length, aryData.length);

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(2000);
        // send request
        byte[] buf = new byte[1024];

        InetAddress address = InetAddress.getByName(pStrAdd);
        DatagramPacket packet = new DatagramPacket(arySent, arySent.length, address, 4445);

        socket.send(packet);

        DatagramPacket response = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                socket.receive(response);

                byte[] aryOutput = new byte[response.getData().length];

                System.arraycopy(response.getData(), 0, aryOutput, 0, response.getData().length);
                socket.close();

                return aryOutput;

            } catch (SocketTimeoutException e) {
                // resend
                socket.send(packet);
            }
        }
    }

    public static byte[] sendData(String pStrAdd, byte[] pAryData) throws SocketException, UnknownHostException, IOException {

        byte[] arySeq = CNetworkManager.marshallInt(intSeqId);

        byte[] arySent = new byte[pAryData.length + arySeq.length];

        System.arraycopy(arySeq, 0, arySent, 0, arySeq.length);

        System.arraycopy(pAryData, 0, arySent, arySeq.length, pAryData.length);

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(2000);
        // send request
        byte[] buf = new byte[1024];

        InetAddress address = InetAddress.getByName(pStrAdd);
        DatagramPacket packet = new DatagramPacket(arySent, arySent.length, address, 4445);

        if (CRandomGenerator.getInt(1, 10) > intFailure) {
            socket.send(packet);
        } else {
            System.out.println("Sent packet dropped");
        }

        DatagramPacket response = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                socket.receive(response);

                if (CRandomGenerator.getInt(1, 10) < intFailure) {
                    System.out.println("Recieved packet dropped");
                    socket.send(packet);
                } else {
                    byte[] aryOutput = new byte[response.getData().length];

                    System.arraycopy(response.getData(), 0, aryOutput, 0, response.getData().length);
                    socket.close();

                    intSeqId++;

                    return aryOutput;
                }

            } catch (SocketTimeoutException e) {
                // resend
                if (CRandomGenerator.getInt(1, 10) > intFailure) {
                    socket.send(packet);
                } else {
                    System.out.println("Sent packet dropped");
                }
            }
        }
    }
}
