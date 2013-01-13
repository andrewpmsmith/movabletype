package com.andrewpmsmith.movabletype.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.app.Activity;
import android.content.Context;

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

	public static boolean serializeToFile(Context mContext,
			Serializable objectToSerialize, String filename) {

		boolean ret = false;
		ObjectOutputStream out = null;
		FileOutputStream fos = null;
		GZIPOutputStream gz = null;
		try {

			fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			gz = new GZIPOutputStream(fos);
			out = new ObjectOutputStream(gz);
			out.writeObject(objectToSerialize);
			ret = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (gz != null)
					gz.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;

	}

	public static Object deserializeFromFile(Activity activity, String filename) {

		Object deserialisedObject = null;
		FileInputStream fis = null;
		GZIPInputStream gz = null;
		ObjectInputStream in = null;

		try {

			fis = activity.openFileInput(filename);
			gz = new GZIPInputStream(fis);
			in = new ObjectInputStream(gz);
			deserialisedObject = in.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				if (gz != null)
					gz.close();
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return deserialisedObject;
	}

	// Suppress default constructor for noninstantiability
	private Serializer() {
		throw new AssertionError();
	}

}
