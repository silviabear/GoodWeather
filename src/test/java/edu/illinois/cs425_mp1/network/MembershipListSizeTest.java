package edu.illinois.cs425_mp1.network;

import edu.illinois.cs425_mp1.types.MembershipList;
import edu.illinois.cs425_mp1.types.Node;
import edu.illinois.cs425_mp1.types.NodeStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Wesley on 10/4/15.
 */
public class MembershipListSizeTest {

    public static void main(String[] args) throws Exception{
        MembershipList lis = new MembershipList();
        for(int i = 0 ; i< 4; i++){
            lis.add(new Node("abc", NodeStatus.NONE), i);
        }

        System.out.println(sizeof(lis));
    }

    public static int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }

}
