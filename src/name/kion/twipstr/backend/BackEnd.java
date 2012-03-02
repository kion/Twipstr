/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr.backend;

import java.awt.Component;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import name.kion.twipstr.Constants;
import name.kion.twipstr.Twipstr;
import name.kion.twipstr.exception.BackEndException;
import name.kion.twipstr.gui.NotificationService;
import name.kion.twipstr.util.AppManager;
import name.kion.twipstr.util.Validator;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Jmp;

/**
 * @author kion
 */
public class BackEnd {
	
	private static Twitter twitter;
	
	private static ImageUpload imageUpload;
	
	private static Set<File> attachedMediaFiles;

	// API configuration
	private static int maxMediaPerUpload;
	private static int charactersReservedPerMedia;
	
	private BackEnd() {
		// hidden default constructor
	}

	public static void init() throws BackEndException {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			AccessToken accessToken = BackEnd.loadAccessToken();
			if (accessToken == null) {
				twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
				RequestToken requestToken = twitter.getOAuthRequestToken();
				while (null == accessToken) {
					String authorizationURL = requestToken.getAuthorizationURL();
					AppManager.getInstance().handleAddress(authorizationURL);
					String pin = JOptionPane.showInputDialog(
							null,
							new Component[] {
								new JTextField(authorizationURL),
								new JLabel(
										"<html>OAuth has been requested via your default browser<br/>" + 
										"(if page hasn't been opened in browser automatically,<br/>" +
										"copy URL in the text box above and paste it to your browser's address bar manually).<br/><br/>" + 
										"Allow access for Twipstr and enter the PIN below:"
									)
							},
							"Twipstr :: Allow access to your account",
							JOptionPane.INFORMATION_MESSAGE);
					try {
						if (!Validator.isNullOrBlank(pin)) {
							accessToken = twitter.getOAuthAccessToken(requestToken, pin);
						} else {
							accessToken = twitter.getOAuthAccessToken();
						}
						BackEnd.storeAccessToken(accessToken);
					} catch (TwitterException te) {
						if (401 == te.getStatusCode()) {
							System.out.println("Unable to get the access token!");
						}
						throw te;
					}
				}
			} else {
				twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
				twitter.setOAuthAccessToken(accessToken);
			}
			BackEnd.twitter = twitter;
			loadAPIConfiguration();
		} catch (Throwable cause) {
			throw new BackEndException("Failed to initialize access token!", cause);
		}
	}

	private static AccessToken loadAccessToken() throws BackEndException {
		try {
			AccessToken accessToken = null;
			Preferences prefs = loadPreferences();
			if (prefs != null) {
				String s = prefs.get(Constants.PROPERTY_ACCESS_TOKEN, null);
				if (s != null) {
					String[] ss = s.split(":");
					accessToken = new AccessToken(ss[0], ss[1]);
				}
			}
		    return accessToken;
		} catch (Throwable cause) {
			throw new BackEndException("Failed to load access token!", cause);
		}
	}

	private static void storeAccessToken(AccessToken accessToken) throws BackEndException {
		try {
			Preferences prefs = loadPreferences();
			if (accessToken != null) {
				prefs.put(Constants.PROPERTY_ACCESS_TOKEN, accessToken.getToken() + ":" + accessToken.getTokenSecret());
			} else {
				prefs.remove(Constants.PROPERTY_ACCESS_TOKEN);
			}
			storePreferences(prefs);
		} catch (Throwable cause) {
			throw new BackEndException("Failed to store access token!", cause);
		}
	}
	
	private static void loadAPIConfiguration() throws TwitterException {
		maxMediaPerUpload = twitter.getAPIConfiguration().getMaxMediaPerUpload();
		charactersReservedPerMedia = twitter.getAPIConfiguration().getCharactersReservedPerMedia();
	}
	
	public static Preferences loadPreferences() {
		Preferences prefs = null;
		try {
			prefs = Preferences.userNodeForPackage(Twipstr.class);
		} catch (Throwable cause) {
			// ignore
			cause.printStackTrace();
		}
		return prefs;
	}
	
	public static void storePreferences(Preferences prefs) {
		try {
			prefs.flush();
		} catch (Throwable cause) {
			// ignore
			cause.printStackTrace();
		}
	}
	
	public static String shortenURL(String url) throws BackEndException {
		try {
			Preferences prefs = loadPreferences();
			String username = prefs.get(Constants.PROPERTY_USERPREF_BITLY_USERNAME, Constants.BITLY_TWIPSTR_USERNAME);
			String apiKey = prefs.get(Constants.PROPERTY_USERPREF_BITLY_API_KEY, Constants.BITLY_TWIPSTR_API_KEY);
			return Jmp.as(username, apiKey).call(Bitly.shorten(url)).getShortUrl();
		} catch (Throwable cause) {
			throw new BackEndException("Failed to shorten URL!", cause);
		}
	}
	
	public static void resetImageUploadService() {
		imageUpload = null;
	}
	
	public static boolean usingSeparateImageUploading() {
		Preferences prefs = loadPreferences();
		String mpName = prefs.get(Constants.PROPERTY_USERPREF_MEDIA_PROVIDER, Constants.DEFAULT_MEDIA_PROVIDER);
		if (MediaProvider.TWITTER.getName().equals(mpName)) {
			return false;
		}
		return true;
	}
	
	public static String uploadImage(File imageFile) throws BackEndException {
		try {
			if (imageUpload == null) {
				Preferences prefs = loadPreferences();
				String mpName = prefs.get(Constants.PROPERTY_USERPREF_MEDIA_PROVIDER, Constants.DEFAULT_MEDIA_PROVIDER);
				ConfigurationBuilder confBuilder = new ConfigurationBuilder();
				confBuilder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
				confBuilder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
				confBuilder.setOAuthAccessToken(twitter.getOAuthAccessToken().getToken());
				confBuilder.setOAuthAccessTokenSecret(twitter.getOAuthAccessToken().getTokenSecret());
				if (MediaProvider.TWITPIC.getName().equals(mpName)) {
					confBuilder.setMediaProviderAPIKey(Constants.TWITPIC_TWIPSTR_API_KEY);
				} else if (MediaProvider.PLIXI.getName().equals(mpName)) {
					confBuilder.setMediaProviderAPIKey(Constants.PLIXI_TWIPSTR_API_KEY);
				}
				confBuilder.setMediaProvider(mpName);
				Configuration config = confBuilder.build();
				imageUpload = new ImageUploadFactory(config).getInstance();
			}
			return imageUpload.upload(imageFile);
		} catch (Throwable cause) {
			throw new BackEndException("Failed to upload image! You may try to choose another image-upload service in preferences.", cause);
		}
	}
	
	public static int attachMedia(File mediaFile) throws BackEndException {
		if (attachedMediaFiles == null) {
			attachedMediaFiles = new HashSet<File>();
		}
		if (attachedMediaFiles.size() < maxMediaPerUpload) {
			attachedMediaFiles.add(mediaFile);
			return charactersReservedPerMedia;
		} else {
			throw new BackEndException("You can't attach more media files, max number of media attachments allowed: " + maxMediaPerUpload + 
										"\nYou may try to choose another image-upload service in preferences.");
		}
	}
	
	public static int cancelMedia(String mediaFilePath) {
		if (attachedMediaFiles != null) {
			for (File f : attachedMediaFiles) {
				if (f.getAbsolutePath().equals(mediaFilePath)) {
					attachedMediaFiles.remove(f);
					break;
				}
			}
		}
		if (attachedMediaFiles.isEmpty()) {
			attachedMediaFiles = null;
		}
		return charactersReservedPerMedia;
	}
	
	public static boolean updateStatus(String status) throws BackEndException {
		try {
			StatusUpdate statusUpdate = new StatusUpdate(status);
			if (attachedMediaFiles != null) {
				for (File mediaFile : attachedMediaFiles) {
					// it should theoretically be possible to attach more than one media file
					statusUpdate.setMedia(mediaFile);
				}
			}
			twitter.updateStatus(statusUpdate);
			attachedMediaFiles = null;
			return true;
		} catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				NotificationService.errorMessage("It seems that access token is outdated! Reseting...", te);
				try {
					storeAccessToken(null); // clear outdated access token
					init(); // re-initialize
				} catch (BackEndException bee) {
					NotificationService.errorMessage("Failed to re-initialize access token!", bee);
				}
			} else {
				throw new BackEndException("Failed to update status!", te);
			}
			return false;
		} catch (Throwable cause) {
			throw new BackEndException("Failed to update status!", cause);
		}
	}

}
