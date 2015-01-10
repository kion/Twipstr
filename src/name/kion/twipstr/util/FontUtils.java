package name.kion.twipstr.util;

import java.awt.Font;
import java.io.InputStream;

import name.kion.twipstr.Constants;

public class FontUtils {
	
	private FontUtils() {
        // hidden default constructor
	}
	
    public static Font getFont(String name, float size) {
    	try (InputStream is = Constants.class.getResourceAsStream("/name/kion/twipstr/res/" + name + ".ttf")) {
			return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
		} catch (Throwable cause) {
			// fall back to default font
			return new Font(Font.SANS_SERIF, Font.PLAIN, 28);
		}
    }

}
