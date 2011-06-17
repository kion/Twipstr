/**
 * Created on Jun 12, 2011
 */
package name.kion.twipstr.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ComponentEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;

import name.kion.twipstr.util.ImageUtils;

/**
 * @author kion
 */
public class StaticImagePanel extends ImagePanel {

    private static final long serialVersionUID = 1L;

    private Image origImage;
    
    public StaticImagePanel(Image img) {
    	this(null, img);
    }
    
    public StaticImagePanel(Color bgColor, Image image) {
        super(bgColor);
    	this.origImage = image;
    }
    
    @Override
    protected void componentResized(ComponentEvent e) {
		updateImage();
    }
    
	@Override
	public void ancestorAdded(AncestorEvent event) {
		updateImage();
	}

	private void updateImage() {
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
		        try {
					scaledImage = ImageUtils.getScaledImage(origImage, StaticImagePanel.this.getBounds().width, StaticImagePanel.this.getBounds().height);
		            MediaTracker mt = new MediaTracker(StaticImagePanel.this);
		            mt.addImage(scaledImage,0);
		            try {
		                mt.waitForID(0);
		            } catch(InterruptedException ie){}
		            if (mt.isErrorID(0)) {
		                setSize(0,0);
		                synchronized(this) {
		                    painted = true;
		                    notifyAll();
		                }
		                return;
		            }
		            if (!EventQueue.isDispatchThread() && Runtime.getRuntime().availableProcessors() == 1) {
		                synchronized(this) {
		                    while (!painted) {
		                        try { 
		                            wait(); 
		                        } catch (InterruptedException e) {}
		                    }
		                }
		            }
		        } catch (Throwable cause) {
		        	cause.printStackTrace(System.err);
		        } finally {
		            repaint();
		        }
			}
		});
	}

}
