package edu.illinois.cs425_mp1.types;

import edu.illinois.cs425_mp1.adapter.Adapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Wesley on 11/4/15.
 */
public class FileRequest extends Request implements Serializable {

    private StringBuilder buffer = null;

    private ArrayList<String> fileStore = null;

    public FileRequest(Command c, String body) {
        super(c, body);
        buffer = new StringBuilder();
    }

    public void fillBufferOnLocal(String path) throws IOException {
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

    public void emptyBuffer() {
        buffer = new StringBuilder();
    }

    /**
     * String is immutable object, can use copy constructor
     */
    public void fillStoreOnList() {
        fileStore = new ArrayList<String>(Adapter.getLocalDFSFileList());
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public ArrayList<String> getList() {
        return fileStore;
    }


}
