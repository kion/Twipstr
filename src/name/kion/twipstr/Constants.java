/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import name.kion.twipstr.util.ResourceLoader;
import twitter4j.media.MediaProvider;

/**
 * @author kion
 */
public interface Constants {
	
	public static final String APP_INFO_NAME = "~ Twipstr ~";
	public static final String APP_INFO_NAME_AND_VERSION = "~ Twipstr 1.2.0 ~";
	public static final String APP_INFO_URL = "http://twipstr.sf.net";
	public static final String APP_INFO_AUTHOR = "© R. Kasianenko | kion";
	public static final String APP_INFO_AUTHOR_URL = "http://kion.name";
	public static final String APP_INFO_HELP = 
		"<html>" +
			"<br/><b>Editing tips:</b>" +
			"<br/><br/>• {Ctrl+Z} to undo" +
			"<br/>• {Ctrl+Y} to redo" +
		"</html>";

	public static final String CONSUMER_KEY = "RGyto8KxCFIWgMW3joYNiQ";
	public static final String CONSUMER_SECRET = "1NCV3Ky5qklc8q9ZMk2WJfSNn2w5b4EX7HrhPt1pcP4";
	
	public static final String PROPERTY_ACCESS_TOKEN = "ACCESS_TOKEN";
    
	public static final String BITLY_TWIPSTR_USERNAME = "twipstr";
	public static final String BITLY_TWIPSTR_API_KEY = "R_bfb4f98b9f8fc792909e5024295d98b1";
	
	public static final String DEFAULT_MEDIA_PROVIDER = MediaProvider.TWITTER.name();
	public static final String[] SUPPORTED_MEDIA_PROVIDERS = new String[]{
		DEFAULT_MEDIA_PROVIDER,
		MediaProvider.YFROG.name(),
		MediaProvider.TWITPIC.name(),
		MediaProvider.TWITGOO.name(),
		MediaProvider.TWIPPLE.name(),
		MediaProvider.MOBYPICTURE.name(),
		MediaProvider.IMG_LY.name(),
		MediaProvider.PLIXI.name(),
		MediaProvider.POSTEROUS.name()
	};
	
	public static final String TWITPIC_TWIPSTR_API_KEY = "9e03e2868fc10ef51329bc698b65e227";
	public static final String PLIXI_TWIPSTR_API_KEY = "038b9de7-d718-44d4-b248-e9674b29b7d6";
	public static final String MOBYPICTURE_TWIPSTR_API_KEY = "pctMliDQDWLimJNo";
	
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
    
    public static final int DEFAULT_WINDOW_WIDTH = 965;
    public static final int DEFAULT_WINDOW_HEIGHT = 195;
    
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
	public static final String PROPERTY_USERPREF_MEDIA_PROVIDER = "MEDIA_PROVIDER";
	public static final String PROPERTY_LAF = "LOOK_AND_FEEL";

    public static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 28);
    
    public static final int LENGTH_WARNING = 30;
    public static final int LENGTH_LIMIT = 10;
	
	public static final Color TEXT_BG_COLOR = Color.WHITE;
	public static final Color IMG_BG_COLOR = Color.WHITE;

	public static final Color COLOR_SYMBOL_HIGHLIGHT = new Color(98, 127, 125);

	public static final Color COLOR_OK = new Color(0, 136, 0);
    public static final Color COLOR_WARNING = new Color(255, 255, 0);
    public static final Color COLOR_CLOSE_TO_LIMIT = new Color(201, 0, 0);
    public static final Color COLOR_OVER_LIMIT = new Color(255, 0, 0);

}
