package edu.illinois.cs425_mp1.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Wesley on 9/6/15.
 */
public class EchoClient {

    public static void main(String args[]) {

        String target = "127.0.0.1";
        int port = 6753;
        P2PSender client = new P2PSender(target, port);

        try {
            System.out.println("Trying to Connect Server @ " + target);
            client.run();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                client.send(line + "\r\n");

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    client.close();
                    break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
