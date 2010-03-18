package org.lzy.fwswaper.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.util.logging.Logger;

//import java.util.logging.Logger;

public final class CommonUtils {

	//	private static final Logger log =
	//		Logger.getLogger(CommonUtils.class.getName());

	// Can not instantiation. Factory pattern.
	private CommonUtils() {
		return;
	}

	public static byte[] int2bytes(int i) {

		byte[] res = new byte[4];

		for (int idx = 0; idx < 4; idx++)
			res[idx] = (byte) ((i >> (idx * 8)) & 0xff);

		return res;
	}

	public static boolean comparebytes(byte[] x, byte[] y, int start, int len) {

		if (x == null || y == null)
			return false;
		else if (start < 0 || len < 1)
			return false;
		else if (start >= x.length)
			return false;

		if (x.length < len || y.length < len)
			return false;

		for (int i = 0; i < len; i++)
			if (x[start + i] != y[i])
				return false;

		return true;
	}

	/**
	 * Returns the index within x of the first occurrence
	 *  of an any byte inside specified y, starting the search at the specified index. 
	 * @param start
	 * the index to start the search from, inclusive. 
	 * @return
	 * the index of the first occurrence of the byte inside y in the x.
	 */
	public static int findByte(byte[] x, byte[] y, int start) {

		if (x == null)
			return -1;

		return findByte(x, y, start, x.length);
	}

	/**
	 * Returns the index within x of the first occurrence
	 *  of an any byte inside specified y, starting the search at the specified index. 
	 * @param start
	 * the index to start the search from, inclusive.
	 * @param len
	 * the number of bytes to search in x.
	 * @return
	 * the index of the first occurrence of the byte inside y in the x.
	 */
	public static int findByte(byte[] x, byte[] y, int start, int len) {

		if (x == null || y == null)
			return -1;
		else if (start < 0 || len < 1)
			return -1;
		else if (x.length == 0)
			return -1;

		int end = Math.min(x.length - 1, start + len);

		if (start >= end)
			return -1;
		else if (y.length == 0)
			return -1;

		int pos_y = 0;

		for (int pos_x = start; pos_x < end; pos_x++) {
			for (pos_y = 0; pos_y < y.length; pos_y++) {
				if (x[pos_x] == y[pos_y])
					return pos_x;
			}
		}

		return -1;
	}

	/**
	 * Returns the index within x of the first occurrence
	 *  of the specified y, starting the search at the specified index. 
	 * @param start
	 * the index to start the search from, inclusive. 
	 * @return
	 * the index of the first occurrence of the y in the x.
	 */
	public static int findBytes(byte[] x, byte[] y, int start) {

		if (x == null)
			return -1;

		return findBytes(x, y, start, x.length);
	}

	/**
	 * Returns the index within x of the first occurrence
	 *  of the specified y, starting the search at the specified index. 
	 * @param start
	 * the index to start the search from, inclusive.
	 * @param len
	 * the number of bytes to search in x.
	 * @return
	 * the index of the first occurrence of the y in the x.
	 */
	public static int findBytes(byte[] x, byte[] y, int start, int len) {

		if (x == null || y == null)
			return -1;
		else if (start < 0 || len < 1)
			return -1;
		else if (x.length == 0)
			return -1;
		else if (x.length - start < y.length)
			return -1;

		int end = Math.min(x.length - 1, start + len);

		if (start >= end)
			return -1;
		else if (y.length == 0 || y.length > len)
			return -1;

		for (int pos = start; pos < end - y.length; pos++) {
			if (comparebytes(x, y, pos, y.length))
				return pos;
			else if (x[pos] == y[0])
				pos += y.length - 1; // '-1' for 'pos++' above.
		}

		return -1;
	}

	public static void writeBufferToStream(OutputStream os, byte[] buff)
			throws IOException {

		if (os == null)
			throw new IllegalArgumentException("Output stream is null");

		try {
			os.write(buff);
			os.flush();
		} finally {
			if (os != null)
				os.close();
		}
	}

	public static byte[] readStreamToBuffer(InputStream is) throws IOException {
		return readStreamToBuffer(is, 4096);
	}

	public static byte[] readStreamToBuffer(InputStream is, int buff_size)
			throws IOException {

		if (is == null)
			return new byte[0];

		// IO buffer.
		byte[] buff = new byte[buff_size];
		int iopos = 0;

		ByteArrayOutputStream osBuff = new ByteArrayOutputStream();

		try {
			while ((iopos = is.read(buff)) > -1)
				osBuff.write(buff, 0, iopos);

			osBuff.flush();

			return osBuff.toByteArray();
		} finally {
			if (is != null)
				is.close();

			if (osBuff != null)
				osBuff.close();
		}
	}
}
