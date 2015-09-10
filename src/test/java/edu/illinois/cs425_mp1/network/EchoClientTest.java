package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.LogCommand;
import edu.illinois.cs425_mp1.types.LogRequest;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.types.ShutdownRequest;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Wesley on 9/6/15.
 */
public class EchoClientTest {

    public static void main(String args[]) {

        String target = "127.0.0.1";
        int port = 6753;
        P2PSender client = new P2PSender(target, port);

        int numberOfMessage = 10;
        ArrayList<Request> lis = new ArrayList<Request>();
        for(int i = 0; i < numberOfMessage; i++){
            lis.add(new LogRequest(LogCommand.GREP, Integer.valueOf(i).toString()));
        }
        lis.add(new ShutdownRequest());

        try {
            client.run();

            for (Request msg : lis) {
                client.send(msg);
            }


        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        try{
            Thread.sleep(5000);
            client.close();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
