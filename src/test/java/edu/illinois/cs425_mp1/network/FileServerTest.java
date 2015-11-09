package edu.illinois.cs425_mp1.network;

/**
 * Created by Wesley on 11/2/15.
 */
public class FileServerTest {

    public static void main(String[] args) throws Exception {

        int port = 6753;
        FileListener lis = new FileListener(port);

        lis.run();

    }





}

