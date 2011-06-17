/**
 * Oct 31, 2006
 */
package name.kion.twipstr.util;


/**
 * @author kion
 *
 */
public class Validator {
    
    private Validator() {
        // hidden default constructor
    }
	
	public static boolean isNullOrBlank(Object obj) {
		boolean isNullOrBlank = false;
		if (obj == null) {
			isNullOrBlank = true;
		} else {
			String str = null;
			if (obj instanceof String) {
				str = (String) obj;
			} else if (obj instanceof StringBuffer) {
				str = ((StringBuffer) obj).toString();
			}
			if (str != null) {
				if ("".equals(str) || str.matches("(?s)\\s+")) {
					isNullOrBlank = true;
				}
			}
		}
		return isNullOrBlank;
	}

}
