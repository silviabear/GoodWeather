package edu.illinois.cs425_mp1.network;

/**
 * This is the test for UDP connection
 * Created by Wesley on 10/2/15.
 */
public class EchoUDPServerTest {
    public static void main(String[] args) {
        int port = 6753;
        UDPListener lis = new UDPListener(port);
        lis.run();

    }
}
