package edu.illinois.cs425_mp1.types;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Wesley on 11/4/15.
 */
public class FileRequest extends Request implements Serializable {

    private StringBuilder buffer = null;

    public FileRequest(Command c, String body){
        super(c, body);
        buffer = new StringBuilder();
    }

    public void fillBufferOnLocal(String path) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            String line = br.readLine();

            while (line != null) {
                buffer.append(line);
                buffer.append("\n");
                line = br.readLine();
            }
        } finally {
            br.close();
        }
    }

    public void emptyBuffer(){
        buffer = new StringBuilder();
    }

    public StringBuilder getBuffer(){
        return buffer;
    }
}
