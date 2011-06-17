/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author kion
 */
public class TwitterStatusDocument extends PlainDocument {

	private static final long serialVersionUID = 8965138264795255409L;
	
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null || str.equals("\n")) return;
		super.insertString(offset, str, attr);
	}

}
