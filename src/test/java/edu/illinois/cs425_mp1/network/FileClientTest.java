package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.FileRequest;

/**
 * Created by Wesley on 11/2/15.
 */
public class FileClientTest {

    public static void main(String args[]) {

        String target = "127.0.0.1";
        int port = 6753;
        FileSender client = new FileSender(target, port);


        try {
            client.run();
//            FileRequest req = new FileRequest(Command.PUT, "testdata/a/b/c/savefile.lol");
//            req.fillBufferOnLocal("pom.xml");
            client.sendFile("data/test500mb", "output");

        } catch(NullPointerException e){

        }
        catch (Exception e){

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
