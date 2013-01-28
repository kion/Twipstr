/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr;

import javax.swing.UIManager;

import name.kion.twipstr.backend.BackEnd;
import name.kion.twipstr.gui.FrontEnd;
import name.kion.twipstr.gui.NotificationService;


/**
 * @author kion
 */
public class Twipstr {
	
	public static void main(String args[]) {
		try {
			System.setProperty("swing.aatext", "true");
			System.setProperty("awt.useSystemAAFontSettings", "on");
			try {
				String laf = BackEnd.loadPreferences().get(Constants.PROPERTY_LAF, Constants.DEFAULT_LAF);
				UIManager.setLookAndFeel(Constants.SUPPORTED_LAFS.get(laf));
			} catch (Throwable cause) {
				// ignore (automatic fallback to system/default Look-And-Feel)
				cause.printStackTrace(System.err);
			}
			BackEnd.init();
			FrontEnd.init();
		} catch (Throwable cause) {
			NotificationService.errorMessage("Failed to init/start app!", cause);
		}
	}
	
}
