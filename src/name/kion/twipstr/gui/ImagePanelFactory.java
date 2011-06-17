/**
 * Created on Jun 18, 2011
 */
package name.kion.twipstr.gui;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import name.kion.twipstr.util.GIFDecoder;
import name.kion.twipstr.util.ImageUtils;

/**
 * @author kion
 */
public class ImagePanelFactory {
	
	private ImagePanelFactory() {
		// hidden default constructor
	}
	
	public static ImagePanel buildScalableImagePanel(File imageFile) throws IOException {
		return buildImagePanel(imageFile, null);
	}
	
	public static ImagePanel buildImagePanel(File imageFile, Color bgColor) throws IOException {
		ImagePanel imagePanel = null;
		if (ImageUtils.isAnimatedGIFImage(imageFile)) {
			GIFDecoder gd = new GIFDecoder();
			FileInputStream fis = new FileInputStream(imageFile);
			gd.read(fis);
			fis.close();
			if (gd.getFrameCount() > 1) {
				imagePanel = new AnimatedGIFImagePanel(bgColor, gd);
			} else {
				gd = null;
			}
		}
		if (imagePanel == null) {
			imagePanel = new StaticImagePanel(bgColor, Toolkit.getDefaultToolkit().getImage(imageFile.toURI().toURL()));
		}
		return imagePanel;
	}

}
