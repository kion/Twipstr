/**
 * Created on Jan 20, 2009
 */
package name.kion.twipstr.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

import name.kion.twipstr.util.CommonUtils;


/**
 * @author kion
 */
public class NotificationService {

    public static void errorMessage(Throwable cause) {
    	errorMessage(cause, null);
    }
    
    public static void errorMessage(Throwable cause, Component cmp) {
    	cause.printStackTrace(System.err);
        JOptionPane.showMessageDialog(cmp, CommonUtils.getFailureDetails(cause), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void errorMessage(String message) {
    	errorMessage(message, null, null);
    }

    public static void errorMessage(String message, Component cmp) {
    	errorMessage(message, null, cmp);
    }

    public static void errorMessage(String message, Throwable cause) {
    	errorMessage(message, cause, null);
    }
    
    public static void errorMessage(String message, Throwable cause, Component cmp) {
    	if (cause != null) {
    		cause.printStackTrace(System.err);
    	}
        JOptionPane.showMessageDialog(cmp, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void message(String message, int messageType) {
    	message(message, messageType, null);
    }
    
    public static void message(String message, int messageType, Component cmp) {
        JOptionPane.showMessageDialog(cmp, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
}
