/**
 * Created on Jun 12, 2011
 */
package name.kion.twipstr.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MediaTracker;
import java.awt.event.ComponentEvent;

import javax.swing.event.AncestorEvent;

import name.kion.twipstr.util.GIFDecoder;
import name.kion.twipstr.util.ImageUtils;

/**
 * @author kion
 */
public class AnimatedGIFImagePanel extends ImagePanel {

    private static final long serialVersionUID = 1L;
    
    private Dimension scaleDim;

	private volatile boolean paused = false;

	public AnimatedGIFImagePanel(GIFDecoder gd) {
		this(null, gd);
	}
	
    public AnimatedGIFImagePanel(Color bgColor, GIFDecoder gd) {
        super(bgColor);
		runAnimation(gd);
    }
    
	@Override
	public void componentResized(ComponentEvent e) {
		if (scaleDim == null) {
			scaleDim = new Dimension(AnimatedGIFImagePanel.this.getBounds().width, AnimatedGIFImagePanel.this.getBounds().height);
		} else {
			scaleDim.setSize(AnimatedGIFImagePanel.this.getBounds().width, AnimatedGIFImagePanel.this.getBounds().height);
		}
	}

	@Override
	public void ancestorAdded(AncestorEvent event) {
		synchronized (AnimatedGIFImagePanel.this) {
	    	paused = false;
	    	AnimatedGIFImagePanel.this.notifyAll();
		}
	}
	@Override
	public void ancestorRemoved(AncestorEvent event) {
    	if (!paused) {
    		paused = true;
    	}
	}

	private void runAnimation(final GIFDecoder gd) {
        Thread frameSwitcher = new Thread(new Runnable() {
			@Override
			public void run() {
	        	try {
					while (true) { // infinite loop for animated GIF is OK
						int i = 0;
				        for (i = 0; i < gd.getFrameCount(); i++) {
				        	scaledImage = AnimatedGIFImagePanel.this.scaleDim == null 
				        				|| (AnimatedGIFImagePanel.this.scaleDim.width <= 0 || AnimatedGIFImagePanel.this.scaleDim.height <= 0) ? 
					        			gd.getFrame(i) 
					        			: 
					        			ImageUtils.getScaledImage(
					        					gd.getFrame(i), 
					        					AnimatedGIFImagePanel.this.scaleDim.width, 
					        					AnimatedGIFImagePanel.this.scaleDim.height);
				            MediaTracker mt = new MediaTracker(AnimatedGIFImagePanel.this);
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
				            if (!EventQueue.isDispatchThread()
				                    && Runtime.getRuntime().availableProcessors() == 1) {
				                synchronized(this) {
				                    while (!painted) {
				                        try { 
				                            wait(); 
				                        } catch (InterruptedException e) {}
				                    }
				                }
				            }
				            AnimatedGIFImagePanel.this.repaint();
				            int delay = gd.getDelay(i);
				            if (delay == 0) delay = 100;
				            for (int j = 0; j < delay; j++) {
								synchronized (AnimatedGIFImagePanel.this) {
									while (paused) {
										try {
											AnimatedGIFImagePanel.this.wait();
										} catch(InterruptedException e) {}
									}
									Thread.sleep(1);
								}	
				            }
				        }
					}
				} catch (Throwable cause) {
					cause.printStackTrace(System.err);
				}
			}
		});
		frameSwitcher.start();
    }
    
}
