/**
 * Created on Oct 6, 2012
 */
package name.kion.twipstr.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author kion
 */
public class ResourceLoader {

	private ResourceLoader() {
        // hidden default constructor
	}
	
	public static String loadAsText(String path) {
		String text = null;
		try {
	        InputStream is = ResourceLoader.class.getResourceAsStream(path);
	        BufferedInputStream bis = new BufferedInputStream(is);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        byte[] buffer = new byte[1024];
	        int br;
	        while ((br = bis.read(buffer)) > 0) {
	            baos.write(buffer, 0, br);
	        }
	        baos.close();
	        bis.close();
	        text = new String(baos.toByteArray(), "UTF-8");
		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
		}
		return text;
	}

}
