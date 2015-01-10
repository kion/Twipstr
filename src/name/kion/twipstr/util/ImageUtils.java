/**
 * Created on Mar 4, 2009
 */
package name.kion.twipstr.util;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author kion
 */
public class ImageUtils {
	
	private ImageUtils() {
        // hidden default constructor
	}

	public static Image getScaledImage(File file, int width, int height) throws Exception {
		return getScaledImage(Toolkit.getDefaultToolkit().getImage(file.toURI().toURL()), width, height);
	}

	public static Image getScaledImage(Image image, int width, int height) throws Exception {
		MediaTracker mediaTracker = new MediaTracker(new Container());
		mediaTracker.addImage(image, 0);
		mediaTracker.waitForID(0);
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double thumbRatio = (double) width / (double) height;
		double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			height = (int) (width / imageRatio);
		} else {
			width = (int) (height * imageRatio);
		}
		return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
	
	public static boolean isAnimatedGIFImage(File file) {
		boolean isAnimatedGIFImage = false;
		if (file.getName().toUpperCase().endsWith(".GIF")) {
			IIOMetadata metadata = getImageMetadata(file);
			String[] names = metadata.getMetadataFormatNames();
			if (names.length > 0) {
				Node root = metadata.getAsTree(names[0]);
				isAnimatedGIFImage = searchForMetadataNodeAttributeValue(root, "ApplicationExtension", "applicationID", "NETSCAPE");
			}
		}
		return isAnimatedGIFImage;
	}

	private static boolean searchForMetadataNodeAttributeValue(Node node, String nodeName, String attributeName, String attributeValue) {
		return searchForMetadataNodeAttributeValue(node, nodeName, attributeName, attributeValue, false);
	}
	
	private static boolean searchForMetadataNodeAttributeValue(Node node, String nodeName, String attributeName, String attributeValue, boolean found) {
		if (!found) {
			if (node.getNodeName().equals(nodeName)) {
				NamedNodeMap map = node.getAttributes();
				if (map != null) {
					int length = map.getLength();
					for (int i = 0; i < length; i++) {
						Node attr = map.item(i);
						if (attr.getNodeName().equals(attributeName) && attr.getNodeValue().equals(attributeValue)) {
							found = true;
							break;
						}
					}
				}
			} else {
				Node child = node.getFirstChild();
				while (child != null) {
					found = searchForMetadataNodeAttributeValue(child, nodeName, attributeName, attributeValue);
					if (found) {
						break;
					}
					child = child.getNextSibling();
				}
			}
		}
		return found;
	}
	
	public static IIOMetadata getImageMetadata(File file) {
		IIOMetadata metadata = null;
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				reader.setInput(iis, true);
				return reader.getImageMetadata(0);
			}
		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
		}
		return metadata;
	}

}
