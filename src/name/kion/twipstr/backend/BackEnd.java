/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr.backend;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Jmp;

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
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

/**
 * @author kion
 */
public class BackEnd {
	
	private static Twitter twitter;
	
    private static ImageUpload imageUpload;
    
	private static Set<File> attachedMediaFiles;

	private BackEnd() {
		// hidden default constructor
	}

	public static void init() throws BackEndException {
		try {
			boolean twitterAuthorized = false;
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
			AccessToken accessToken = BackEnd.loadAccessToken();
			if (accessToken != null) {
				try {
					twitter.setOAuthAccessToken(accessToken);
					twitter.verifyCredentials();
					twitterAuthorized = true;
				} catch (TwitterException e) {
					if (e.getStatusCode() == 401) {
						twitter.setOAuthAccessToken(null);
					}
				}
			}
			if (!twitterAuthorized) {
				RequestToken requestToken = twitter.getOAuthRequestToken();
				String authURL = requestToken.getAuthorizationURL();
				StringSelection stringSelection = new StringSelection(authURL);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				try {
					AppManager.getInstance().handleAddress(authURL);
				} catch (Throwable cause) {
					// ignore
				}
				String pin = JOptionPane.showInputDialog(
						null,
						new Component[] {
							new JTextField(authURL),
							new JLabel(
									"<html>OAuth has been requested via your default browser.<br/>" + 
									"If page hasn't been opened in browser automatically,<br/>" +
									"paste URL provided above (copied to clipboard) to your browser's address bar manually.<br/><br/>" + 
									"Authorize app to use your Twitter account and enter generated PIN below:"
								)
						},
						"Twipstr :: Authorize",
						JOptionPane.INFORMATION_MESSAGE);
				if (Validator.isNullOrBlank(pin)) {
					System.exit(0);
				} else {
					accessToken = twitter.getOAuthAccessToken(requestToken, pin);
					try {
						twitter.setOAuthAccessToken(accessToken);
						twitter.verifyCredentials();
						twitterAuthorized = true;
					} catch (TwitterException te) {
						if (te.getStatusCode() == 401) {
							twitter.setOAuthAccessToken(null);
						}
					}
				}
			}
			if (twitterAuthorized) {
				BackEnd.storeAccessToken(accessToken);
				BackEnd.twitter = twitter;
			} else {
				NotificationService.errorMessage("Application has not been authorized!");
				System.exit(0);
			}
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
	
	public static String uploadImage(File imageFile) throws BackEndException {
		try {
			if (imageUpload == null) {
				ConfigurationBuilder confBuilder = new ConfigurationBuilder();
				confBuilder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
				confBuilder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
				confBuilder.setOAuthAccessToken(twitter.getOAuthAccessToken().getToken());
				confBuilder.setOAuthAccessTokenSecret(twitter.getOAuthAccessToken().getTokenSecret());
				confBuilder.setMediaProvider(MediaProvider.TWITTER.name());
				Configuration config = confBuilder.build();
				imageUpload = new ImageUploadFactory(config).getInstance();
			}
			return imageUpload.upload(imageFile);
		} catch (Throwable cause) {
			throw new BackEndException("Failed to upload image! You may try to choose another image-upload service in preferences.", cause);
		}
	}
	
	public static void attachMedia(File mediaFile) throws BackEndException {
		if (attachedMediaFiles == null) {
			attachedMediaFiles = new HashSet<File>();
		}
		attachedMediaFiles.add(mediaFile);
	}
	
	public static void cancelMedia(String mediaFilePath) {
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
	}
	
	public static boolean updateStatus(String status) throws BackEndException {
		try {
			StatusUpdate statusUpdate = new StatusUpdate(status);
			if (attachedMediaFiles != null && !attachedMediaFiles.isEmpty()) {
				long[] mediaIds = new long[attachedMediaFiles.size()];
				int idx = 0;
				for (File mediaFile : attachedMediaFiles) {
					UploadedMedia media = twitter.uploadMedia(mediaFile);
					mediaIds[idx++] = media.getMediaId();
				}
				statusUpdate.setMediaIds(mediaIds);
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
