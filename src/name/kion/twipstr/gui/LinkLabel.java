/**
 * Created on Feb 29, 2008
 */
package name.kion.twipstr.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import name.kion.twipstr.util.AppManager;


/**
 * @author kion
 */
public class LinkLabel extends JLabel {
    private static final long serialVersionUID = 1L;
    
    public LinkLabel(final String text) {
        this(text, text);
    }
    
    public LinkLabel(final String text, final String address) {
        super();
        setText("<html>" + text + "");
        setForeground(Color.BLUE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    AppManager.getInstance().handleAddress(address);
                } catch (Throwable cause) {
                    // ignore, do nothing
                    cause.printStackTrace(System.err);
                }
            }
        });
    }

}
