package com.andrewpmsmith.movabletype.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Serializer {

	public static Object deserialize(byte[] stream) {
		ByteArrayInputStream bis = new ByteArrayInputStream(stream);
		ObjectInput in = null;
		Object deserialisedObject = null;

		try {
			in = new ObjectInputStream(bis);
			deserialisedObject = in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					bis.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return deserialisedObject;
	}

	public static byte[] serialize(Serializable objectToSerialize) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] serializedObject = null;

		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(objectToSerialize);
			serializedObject = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return serializedObject;
	}

	// Suppress default constructor for noninstantiability
	private Serializer() {
		throw new AssertionError();
	}

}
