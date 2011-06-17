/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr.exception;

/**
 * @author kion
 */
public class TwitterInitException extends Exception {

	private static final long serialVersionUID = -6384648588943375625L;

	public TwitterInitException(String message) {
		super(message);
	}

	public TwitterInitException(String message, Throwable cause) {
		super(message, cause);
	}

}
