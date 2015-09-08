package edu.illinois.cs425_mp1.network;

/**
 * Created by Wesley on 9/6/15.
 */
public class EchoServerTest {

    public static void main(String[] args){
        int port = 6753;
        Listener lis = new Listener(port);
        try{
            lis.run();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
