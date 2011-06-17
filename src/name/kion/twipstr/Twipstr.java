/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr;

import javax.swing.UIManager;

import name.kion.twipstr.backend.BackEnd;
import name.kion.twipstr.gui.FrontEnd;
import name.kion.twipstr.gui.NotificationService;


import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * @author kion
 */
public class Twipstr {
	
	public static void main(String args[]) {
		try {
			System.setProperty("swing.aatext", "true");
			System.setProperty("awt.useSystemAAFontSettings", "on");
			try {
				UIManager.setLookAndFeel(new NimbusLookAndFeel());
			} catch (Throwable cause) {
				// ignore, default Look-And-Feel will be used
				cause.printStackTrace();
			}
			BackEnd.init();
			FrontEnd.init();
		} catch (Throwable cause) {
			NotificationService.errorMessage("Failed to init/start app!", cause);
		}
	}
	
}
