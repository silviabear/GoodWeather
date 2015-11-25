package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.FileRequest;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.instrument.Instrumentation;

/**
 * Created by Wesley on 11/8/15.
 */
public class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }

    public static void main(String[] agrs) throws Exception{
        FileRequest freq = new FileRequest(Command.DELETE, "wtf");
        freq.fillBufferOnLocal("data/xaa");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(freq);
        oos.close();
        System.out.print(baos.size());
    }
}