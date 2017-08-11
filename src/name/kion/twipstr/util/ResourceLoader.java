/**
 * Created on Oct 6, 2012
 */
package name.kion.twipstr.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author kion
 */
public class ResourceLoader {

	private ResourceLoader() {
        // hidden default constructor
	}
	
	public static String loadAsText(String path) {
		String text = null;
		try (BufferedInputStream bis = new BufferedInputStream(ResourceLoader.class.getResourceAsStream(path)); 
	         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
	        byte[] buffer = new byte[1024];
	        int br;
	        while ((br = bis.read(buffer)) > 0) {
	            baos.write(buffer, 0, br);
	        }
	        text = new String(baos.toByteArray(), "UTF-8");
		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
		}
		return text;
	}
	
	public static Properties loadProperties(String path) {
		Properties props = new Properties();
		try (InputStream is = ResourceLoader.class.getResourceAsStream(path)) {
			props.load(is);
		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
		}
		return props;
	}

}
