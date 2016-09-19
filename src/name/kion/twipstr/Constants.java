/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import name.kion.twipstr.util.FontUtils;
import name.kion.twipstr.util.ResourceLoader;

/**
 * @author kion
 */
public interface Constants {
	
	public static final String APP_INFO_NAME_AND_VERSION = "~ Twipstr 1.3.5 ~";
	public static final String APP_INFO_URL = "http://twipstr.sf.net";
	public static final String APP_INFO_AUTHOR = "© R. Kasianenko | kion";
	public static final String APP_INFO_AUTHOR_URL = "http://kion.name";
	public static final String APP_INFO_HELP = 
		"<html>" +
			"<br/><b>Editing tips:</b>" +
			"<br/><br/>• {Ctrl+Z} to undo" +
			"<br/>• {Ctrl+Y} to redo" +
		"</html>";
	
	public static final int MAX_STATUS_LENGTH = 140;
    
	public static final Properties PROPERTIES = ResourceLoader.loadProperties("/name/kion/twipstr/config.properties");

	public static final String CONSUMER_KEY = PROPERTIES.getProperty("CONSUMER_KEY");
	public static final String CONSUMER_SECRET = PROPERTIES.getProperty("CONSUMER_SECRET");
	
	public static final String PROPERTY_ACCESS_TOKEN = "ACCESS_TOKEN";
    
	public static final String BITLY_TWIPSTR_USERNAME = PROPERTIES.getProperty("BITLY_TWIPSTR_USERNAME");
	public static final String BITLY_TWIPSTR_API_KEY = PROPERTIES.getProperty("BITLY_TWIPSTR_API_KEY");
	
    public static final String DEFAULT_SYMBOLS =
    		ResourceLoader.loadAsText("/".concat(Constants.class.getPackage().getName().replaceAll("\\.", "/")).concat("/res/").concat("symbols.txt"));

	public static final String DEFAULT_LAF = "System/Default";
	@SuppressWarnings("serial")
	public static final Map<String, String> SUPPORTED_LAFS = new LinkedHashMap<String, String>(){{
		put(DEFAULT_LAF, UIManager.getSystemLookAndFeelClassName());
		for (LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
			put(lafInfo.getName(), lafInfo.getClassName());
		}
	}};
    
    public static final int DEFAULT_WINDOW_WIDTH = 1000;
    public static final int DEFAULT_WINDOW_HEIGHT = 300;
    
    public static final String PROPERTY_WINDOW_COORDINATE_X = "WINDOW_COORDINATE_X";
    public static final String PROPERTY_WINDOW_COORDINATE_Y = "WINDOW_COORDINATE_Y";
    public static final String PROPERTY_WINDOW_WIDTH = "WINDOW_WIDTH";
    public static final String PROPERTY_WINDOW_HEIGHT = "WINDOW_HEIGHT";
    
    public static final String PROPERTY_DIVIDER_LOCATION = "DIVIDER_LOCATION";
    public static final String PROPERTY_SYMBOLS_ENABLED = "SYMBOLS_ENABLED";

    public static final String PROPERTY_FONT_SIZE = "FONT_SIZE";
    
    public static final String URL_SEPARATOR = "\n";
    
    public static final String SYMBOL_GROUP_SEPARATOR_PATTERN = "\n+";
    public static final String SYMBOL_SEPARATOR_PATTERN = "\\s+";

    public static final String PROPERTY_IMAGE_URLS = "IMAGE_URLS";

    public static final String PROPERTY_PREFIX_IMAGE = "IMG_";

    public static final String PROPERTY_TEXT = "TEXT";

    public static final String PROPERTY_PREFIX_SYMBOLS = "SYMBOLS_";

    public static final String PROPERTY_LAST_IMG_DIR = "LAST_IMG_DIR";
    
    public static final String PROPERTY_USERPREF_CLOSE_WINDOW_AFTER_SUCCESSFUL_STATUS_UPDATE = "CLOSE_WINDOW_AFTER_SUCCESSFUL_STATUS_UPDATE";
    public static final String PROPERTY_USERPREF_BITLY_USERNAME = "BITLY_USERNAME";
    public static final String PROPERTY_USERPREF_BITLY_API_KEY = "BITLY_API_KEY";
	public static final String PROPERTY_LAF = "LOOK_AND_FEEL";

    public static final Font FONT_SMALL = FontUtils.getFont("symbola", 12);
    public static final Font FONT_BIG = FontUtils.getFont("symbola", 28);
    
    public static final int LENGTH_WARNING = 30;
    public static final int LENGTH_LIMIT = 10;
	
	public static final Color TEXT_BG_COLOR = Color.WHITE;
	public static final Color IMG_BG_COLOR = Color.WHITE;

	public static final Color COLOR_SYMBOL_HIGHLIGHT = new Color(98, 127, 125);

	public static final Color COLOR_OK = new Color(0, 136, 0);
    public static final Color COLOR_WARNING = new Color(255, 128, 0);
    public static final Color COLOR_CLOSE_TO_LIMIT = new Color(201, 0, 0);
    public static final Color COLOR_OVER_LIMIT = new Color(255, 0, 0);

}
