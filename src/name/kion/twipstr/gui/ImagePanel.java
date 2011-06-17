/**
 * Created on Jun 18, 2011
 */
package name.kion.twipstr.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * @author kion
 */
public abstract class ImagePanel extends JPanel {
	
    private static final long serialVersionUID = 1L;

	protected Color bgColor;

    protected Image scaledImage;

    protected boolean painted = false;
    
    protected ImagePanel(Color bgColor) {
		super();
        this.bgColor = bgColor;
    	addComponentListener(new ComponentAdapter() {
    		@Override
    		public void componentResized(ComponentEvent event) {
    			super.componentResized(event);
    			ImagePanel.this.componentResized(event);
    		}
		});
		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				ImagePanel.this.ancestorAdded(event);
			}
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				ImagePanel.this.ancestorRemoved(event);
			}
			@Override
			public void ancestorMoved(AncestorEvent event) {
				ImagePanel.this.ancestorMoved(event);
			}
		});
	}

	public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
    	if (scaledImage != null) {
    		if (bgColor != null) {
	    		g.setColor(bgColor);
	    		g.fillRect(0, 0, getWidth(), getHeight());
    		}
            g.drawImage(scaledImage, (getWidth() - scaledImage.getWidth(this)) / 2, (getHeight() - scaledImage.getHeight(this)) / 2, this);
    	}
        if (!painted) {
            painted = true;
            synchronized(this) { 
                notifyAll(); 
            }
        }
    }
    
    protected void componentResized(ComponentEvent e){
    	// do nothing by default
    };

    protected void ancestorAdded(AncestorEvent event){
    	// do nothing by default
    };

    protected void ancestorRemoved(AncestorEvent event){
    	// do nothing by default
    };

    protected void ancestorMoved(AncestorEvent event){
    	// do nothing by default
    };
    
}
