package org.lzy.fwswaper.swaper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

public class DirectlySwaper implements ISwaper {

	private static final Logger log =
		Logger.getLogger(DirectlySwaper.class.getName());

	public void swap(HttpURLConnection swaperConn, HttpServletResponse resp)
			throws Exception {

		OutputStream os = null;
		InputStream is = null;

		try {
			
			is = swaperConn.getInputStream();
			os = resp.getOutputStream();

			byte[] buff = new byte[resp.getBufferSize()];
			int size = 0;

			while ((size = is.read(buff)) != -1)
				os.write(buff, 0, size);

		} finally {
			
			if (is != null)
				is.close();

			if (os != null) {
				os.flush();
				os.close();
			}
		}

		log.info(String.format("Swap '%s' well done by '%s'.",
				swaperConn.getURL(), this.getClass().getSimpleName()));
	}
}
