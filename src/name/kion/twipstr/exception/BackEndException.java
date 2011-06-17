/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr.exception;

/**
 * @author kion
 */
public class BackEndException extends Exception {

	private static final long serialVersionUID = 9176826087411867976L;
	
	public BackEndException(String message) {
		super(message);
	}

	public BackEndException(String message, Throwable cause) {
		super(message, cause);
	}

}
