package com.andrewpmsmith.movabletype.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serializer {
	
	public static Object deserialize(byte[] stream) {
		ByteArrayInputStream bis = new ByteArrayInputStream(stream);
		ObjectInput in = null;
		Object o;
		try {
			in = new ObjectInputStream(bis);
			o = in.readObject();
		} catch (Exception e) {
			o = null;
		} finally {
			try {
				bis.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return o;
	}
	
	public static byte[] serialize(Serializable o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] serialisedObject;
		
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(o);
			serialisedObject = bos.toByteArray();
		} catch (IOException e) {
			
			serialisedObject = null;
			e.printStackTrace();
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return serialisedObject;
	}
	
	// Suppress default constructor for noninstantiability
    private Serializer() {
        throw new AssertionError();
    }
	
}
