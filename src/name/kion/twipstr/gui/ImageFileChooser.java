/**
 * Created on Jan 27, 2006
 */
package name.kion.twipstr.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

import name.kion.twipstr.util.ImageUtils;


/**
 * @author kion
 */
public class ImageFileChooser extends JFileChooser implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    
    private static final int PREVIEW_WIDTH = 200;

    private static final int PREVIEW_HEIGHT = 200;

    private ImagePreviewPanel imagePreviewPanel = new ImagePreviewPanel();

    public ImageFileChooser(boolean multiselectionEnabled) {
        super();
        imagePreviewPanel.setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        imagePreviewPanel.setBorder(new EtchedBorder());
        setFileSelectionMode(FILES_ONLY);
        setFileFilter(imageFileFilter);
        setAccessory(imagePreviewPanel);
        if (multiselectionEnabled) {
            setMultiSelectionEnabled(multiselectionEnabled);
            addPropertyChangeListener(JFileChooser.SELECTED_FILES_CHANGED_PROPERTY, this);
        } else {
            addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, this);
        }
    }

    @Override
    public void setMultiSelectionEnabled(boolean b) {
        if (b) {
            removePropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, this);
            addPropertyChangeListener(JFileChooser.SELECTED_FILES_CHANGED_PROPERTY, this);
        } else {
            removePropertyChangeListener(JFileChooser.SELECTED_FILES_CHANGED_PROPERTY, this);
            addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, this);
        }
        super.setMultiSelectionEnabled(b);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        showPreview();
    }

    private void showPreview(){
        File file = null;
        if (isMultiSelectionEnabled()) {
            File[] selectedFiles = getSelectedFiles();
            if (selectedFiles.length > 0) {
                int idx = selectedFiles.length - 1;
                file = selectedFiles[idx];
            }
        } else {
            file = getSelectedFile();
        }
        if (file != null && !file.isDirectory()) {
            imagePreviewPanel.setImage(new ImageIcon(file.getAbsolutePath()).getImage());
        } else {
            imagePreviewPanel.setImage(null);
        }
    }

    private FileFilter imageFileFilter = new FileFilter() {
        public boolean accept(File f) {
            return (f.isDirectory()
                    || (f.isFile() && f.getName().toLowerCase().endsWith(".jpg"))
                    || (f.isFile() && f.getName().toLowerCase().endsWith(".jpeg"))
                    || (f.isFile() && f.getName().toLowerCase().endsWith(".gif"))
                    || (f.isFile() && f.getName().toLowerCase().endsWith(".png")));
        }
        public String getDescription() {
            return "JPG/GIF/PNG image file";
        }
    };

    private class ImagePreviewPanel extends JPanel {

        private static final long serialVersionUID = 438944019406000615L;

        private Image image;

        /**
         * Sets new image to view and repaints the component
         */
        public void setImage(Image image) {
            if (image != null) {
                try {
					this.image = ImageUtils.getScaledImage(image, PREVIEW_WIDTH, PREVIEW_HEIGHT);
				} catch (Exception e) {
	                this.image = null;
				}
            } else {
                this.image = null;
            }
            revalidate();
            repaint();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(image, 0, 0, this);
        }

    }

}
