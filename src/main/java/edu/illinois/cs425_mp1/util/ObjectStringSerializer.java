package edu.illinois.cs425_mp1.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

final public class ObjectStringSerializer {

	/**
	 * Serialize an object into a string
	 * @param ob
	 * @return the string ob encoded into
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static String objectToString(Object ob) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ob);
        oos.flush();
        oos.close();
        
        return new String(Base64.encodeBase64(baos.toByteArray()));
	}
	
	/**
	 * Deserialize an object from a string
	 * @param str
	 * @return the object decoded from str
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object stringToObject(String str) throws IOException, ClassNotFoundException {
		byte [] data = Base64.decodeBase64(str.getBytes());
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
	}
}
