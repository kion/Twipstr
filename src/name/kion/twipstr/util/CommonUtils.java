/**
 * Created on Mar 18, 2008
 */
package name.kion.twipstr.util;



/**
 * @author kion
 */
public class CommonUtils {

    public static String getFailureDetails(Throwable t) {
        StringBuffer msg = new StringBuffer();
        while (t != null) {
            if (t.getMessage() != null) {
                msg.append("\n" + t.getMessage());
            }
            t = t.getCause();
        }
        return msg.toString();
    }

}
