package org.lzy.fwswaper.htmlhandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream; //import java.util.logging.Logger;

import org.lzy.fwswaper.util.CommonUtils;

public final class HtmlHandlerHelper {

	//	private static final Logger log =
	//		Logger.getLogger(HtmlHandlerHelper.class.getName());

	public static String HttpProtocol = "http";
	public static String HttpsProtocol = "https";

	// Can not instantiation. Factory pattern.
	private HtmlHandlerHelper() {
		return;
	}

	public static boolean isHttpLikeProtocolUrl(URL url) {
		String protocol = url.getProtocol();

		return protocol.equals(HttpProtocol) || protocol.equals(HttpsProtocol);
	}

	/**
	 * Check target resource is html type by Content-Type header of HTTP.
	 * 
	 * http://www.ietf.org/rfc/rfc2047.txt
	 */
	public static boolean isHtmlContentType(HttpURLConnection conn) {
		return conn.getContentType().trim().toLowerCase().startsWith(
				"text/html");
	}

	/**
	 * Check target resource content is encoded by GZip by Content-Type header of HTTP.
	 * 
	 *  http://www.ietf.org/rfc/rfc2616.txt
	 */
	public static boolean guessIsGZipCompressed(HttpURLConnection conn,
			byte[] httpBuff) {

		boolean gzipped = false;

		String contentEncoding = conn.getContentEncoding();

		// Try to get 'gzip' token from content encoding inside http response head. 
		if ((contentEncoding != null) && (contentEncoding.length() > 0))
			gzipped = contentEncoding.trim().toLowerCase().contains("gzip");
		else
			gzipped = guessIsGZipCompressed(httpBuff);

		return gzipped;
	}

	public static boolean guessIsGZipCompressed(byte[] httpBuff) {

		boolean gzipped = false;

		if (httpBuff.length > 1) {
			// Try to match gzip magic code from http body buffer.  
			gzipped = CommonUtils.comparebytes(CommonUtils
					.int2bytes(GZIPInputStream.GZIP_MAGIC), httpBuff, 0, 2);
		}

		return gzipped;
	}

	/**
	 * Guess http body content charset by W3C character encodings spec. 
	 * 
	 * http://www.w3.org/International/O-charset
	 */
	public static String guessCharset(byte[] httpBuff) {

		// HTTP 1.1 default charset is ISO-8859-1 (Latin-1).
		String default_charset = "ISO-8859-1", charset = default_charset, charsetToken = "charset=";

		try {

			// This charset token finding is CASE SENSITIVE.
			int charset_token_pos = CommonUtils.findBytes(httpBuff,
					charsetToken.getBytes(charset), 0);

			if (charset_token_pos == -1)
				throw new Exception();

			int charset_value_start_pos = charset_token_pos
					+ charsetToken.length();

			if (CommonUtils.findByte(httpBuff, new byte[]{'\'', '\"'},
					charset_value_start_pos, 1) == charset_value_start_pos)
				charset_value_start_pos++;

			int charset_value_end_pos = CommonUtils.findByte(httpBuff,
					new byte[]{'\'', '\"', ';', ' '}, charset_value_start_pos);

			if (charset_value_end_pos == -1)
				throw new Exception();

			charset = new String(httpBuff, charset_value_start_pos,
					charset_value_end_pos - charset_value_start_pos, charset)
					.trim();

			// Testing charset encoding is supported.
			// Throw UnsupportedEncodingException if the named charset is not supported. 
			new String(new byte[]{' '}, 0, 1, charset);

		} catch (Exception e) {
			// Empty.
			// Default charset ISO-8859-1 (Latin-1) reserved.
			charset = default_charset;
		}

		return charset;
	}

	public static byte[] ungzip(byte[] gzippedBuff) throws IOException {

		if (!guessIsGZipCompressed(gzippedBuff))
			throw new IllegalArgumentException(
					"gzipped buffer argument content is not compressed by gzip.");

		return CommonUtils.readStreamToBuffer(new GZIPInputStream(
				new ByteArrayInputStream(gzippedBuff)));
	}

	public static byte[] gzip(byte[] ungzippedBuff) throws IOException {

		ByteArrayOutputStream osBuff = new ByteArrayOutputStream();

		try {
			CommonUtils.writeBufferToStream(new GZIPOutputStream(osBuff),
					ungzippedBuff);

			osBuff.flush();

			return osBuff.toByteArray();
		} finally {
			if (osBuff != null)
				osBuff.close();
		}
	}

	public static byte[] getHttpBodyBuffer(HttpURLConnection conn)
			throws IOException {
		return CommonUtils.readStreamToBuffer(conn.getInputStream());
	}
}
