package com.razie.sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.razie.pub.base.exceptions.CommRtException;
import com.razie.pub.base.files.SSFilesRazie;
import com.razie.pub.base.log.Log;

/**
 * file utilities
 * 
 * TODO SS rewrite to simplify
 */
public class TempFilesRazie extends SSFilesRazie {
	/**
	 * copy a file from a URL source (file:/ or http:/), text or binary, to a
	 * destination file
	 * 
	 * @param urlSrc
	 * @param fileDest
	 */
	public static void copyFile(String urlSrc, String fileDest) {
		try {
			URL aurl = null;
			if (urlSrc.startsWith("file:") || urlSrc.startsWith("http:")) {
				aurl = new URL(urlSrc);
			} else {
				File file = new File(urlSrc);
				aurl = file.toURL();
			}
			InputStream s = aurl.openStream();
			// do the tragic ...
			File destTmpFile = new File(fileDest + ".TMP");
			if (!destTmpFile.getParentFile().exists())
				destTmpFile.getParentFile().mkdirs();
			destTmpFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(destTmpFile);
			byte[] buf = new byte[4 * 1024 + 1];
			int n;
			while ((n = s.read(buf, 0, 4096)) > 0) {
				fos.write(buf, 0, n);
			}
			fos.close();
			s.close();

			File destFile = new File(fileDest);
			if (!destTmpFile.renameTo(destFile)) {
				destFile.delete();
				if (!destTmpFile.renameTo(destFile)) {
					throw new CommRtException("Cannot rename...");
				}
			}
		} catch (MalformedURLException e) {
			RuntimeException iex = new IllegalArgumentException();
			iex.initCause(e);
			throw iex;
		} catch (IOException e1) {
			throw new CommRtException("Copy from: " + urlSrc + " to: "
					+ fileDest, e1);
		}
	}

	public static final Log logger = Log.Factory.create("", TempFilesRazie.class
			.getName());

	/**
	 * Creates the parent directories if the fileNm contains a complete or
	 * relative path (e.q. fileNm = "test/file.txt" -> creates "test" directory)
	 * 
	 * Note: It's independent of operating system (windows or unix)!
	 */
	public static boolean makeDirectory(String fileNm) {
		if (fileNm != null) {
			fileNm = fileNm.replace('\\', '/');
			File file = new File(fileNm);
			return file.mkdirs();
		}
		return false;
	}

	// ------------------------------------------------------------------------
	public static String getPathFromFile(String fullFileNm) {
		String s = null;
		if (fullFileNm == null || fullFileNm.length() == 0) {
			return s;
		}

		int idx = fullFileNm.lastIndexOf('/');
		if (idx == -1) {
			idx = fullFileNm.lastIndexOf('\\');
		}
		if (idx >= 0) {
			s = fullFileNm.substring(0, idx + 1);
		}
		return s;
	}
}
