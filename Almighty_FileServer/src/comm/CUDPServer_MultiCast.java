package comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class CUDPServer_MultiCast {

    protected DatagramSocket socket = null;

    public CUDPServer_MultiCast() throws SocketException {
        socket = new DatagramSocket(2222);
    }

    public void execute(String pStrFileNme, long pLngPeriod) throws IOException {

        long lngTimeStart = System.currentTimeMillis();

        socket.setSoTimeout(2000);

        while (System.currentTimeMillis() <= pLngPeriod + lngTimeStart) {

            byte[] aryBuffer = new byte[1024];

            DatagramPacket objPacket = new DatagramPacket(aryBuffer, aryBuffer.length);
            try {
                socket.receive(objPacket);

                byte[] aryOutput = CServerManager.performMonitoringOperation(pStrFileNme, objPacket.getData(), objPacket.getAddress().getHostAddress());

                InetAddress objAddress = objPacket.getAddress();

                int intPort = objPacket.getPort();

                objPacket = new DatagramPacket(aryOutput, aryOutput.length, objAddress, intPort);

                socket.send(objPacket);
            } catch (SocketTimeoutException e) {
                // timeout exception.
            }
        }
        socket.close();

    }
}
