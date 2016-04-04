package main;

import comm.CUDPServer;
import java.io.IOException;
import java.net.SocketException;

public class AlmightyFileServer {

    public static void main(String[] args) throws SocketException, IOException {
    	
    	try {
	        CUDPServer objServer = new CUDPServer();
	        objServer.execute();
    	}
    	catch (SocketException e) {
    		e.printStackTrace();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
