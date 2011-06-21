/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

import name.kion.twipstr.Constants;
import name.kion.twipstr.backend.BackEnd;
import name.kion.twipstr.exception.BackEndException;
import name.kion.twipstr.util.Validator;
import twitter4j.media.MediaProvider;

/**
 * @author kion
 */
public class FrontEnd {
	
	private static final Color TEXT_BG_COLOR = Color.WHITE;
	private static final Color IMG_BG_COLOR = Color.WHITE;
	
	private UndoManager undoManager = new UndoManager();
	private UndoableEditListener undoableEditListener = new UndoableEditListener() {
		@Override
		public void undoableEditHappened(UndoableEditEvent e) {
			undoManager.addEdit(e.getEdit());
		}
	};
	
	private Preferences prefs;
	private File lastFileChooserDir;
	private Dimension imageSize;
	
	private Map<String, String> imageFiles;
	
	private List<ImagePanel> imagePanelsVisible;
	private List<ImagePanel> imagePanelsCache;
	
	private JFrame frameTwipstr;
	private JPanel panelMain;
	private JToolBar toolBar;
	private JButton buttPost;
	private JTextArea statusTextArea;
	private JPanel panelControl;
	private JToolBar toolBarShortcuts;
	private JViewport viewportToolBarShortcuts;
	private JButton buttAddSS;
	private JToolBar toolBarManage;
	private JButton buttIncreaseFontSize;
	private JButton buttDecreaseFontSize;
	private JButton buttInfo;
	private JPanel panel;
	private JButton buttShortenURLs;
	private JButton buttPrefs;
	private JButton buttAttach;
	private JPanel panelContent;
	private JPanel panelImages;

	/**
	 * Launch the application.
	 */
	public static void init() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final FrontEnd window = new FrontEnd();
					window.frameTwipstr.setVisible(true);
					window.frameTwipstr.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							window.onExit();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private FrontEnd() {
		initGUI();
		restoreState();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initGUI() {
		frameTwipstr = new JFrame();
		frameTwipstr.setIconImage(Toolkit.getDefaultToolkit().getImage(FrontEnd.class.getResource("/name/kion/twipstr/res/app-icon.png")));
		frameTwipstr.setTitle(Constants.APP_INFO_NAME);
		frameTwipstr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panelMain = new JPanel();
		frameTwipstr.getContentPane().add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BorderLayout(0, 0));
		
		panelContent = new JPanel();
		panelMain.add(panelContent, BorderLayout.CENTER);
		panelContent.setLayout(new BorderLayout(0, 0));
		
		statusTextArea = new JTextArea(new TwitterStatusDocument());
		panelContent.add(statusTextArea);
		statusTextArea.setBackground(TEXT_BG_COLOR);
		statusTextArea.setBorder(new LineBorder(TEXT_BG_COLOR, 15));
		statusTextArea.setFont(Constants.FONT);
		statusTextArea.setLineWrap(true);
		statusTextArea.setWrapStyleWord(true);
		
		panelImages = new JPanel();
		panelImages.setVisible(false);
		panelContent.add(panelImages, BorderLayout.EAST);
		statusTextArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateState();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateState();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateState();
			}
		});
		statusTextArea.getDocument().addUndoableEditListener(undoableEditListener);
		statusTextArea.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (statusTextArea.isEditable()) {
					if (e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_Z) {
						if (undoManager.canUndo()) {
							undoManager.undo();
						}
					} else if (e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_Y) {
						if (undoManager.canRedo()) {
							undoManager.redo();
						}
					}
				}
			};
		});
		statusTextArea.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				if (imagePanelsVisible != null && !imagePanelsVisible.isEmpty()) {
					for (JPanel imagePanel : imagePanelsVisible) {
						imagePanel.setPreferredSize(getImageSize());
					}
				}
			}
		});
		
		panelControl = new JPanel();
		panelMain.add(panelControl, BorderLayout.SOUTH);
		panelControl.setLayout(new BorderLayout(0, 0));
		
		toolBarManage = new JToolBar();
		toolBarManage.setFloatable(false);
		panelControl.add(toolBarManage, BorderLayout.WEST);
		
		buttAddSS = new JButton("");
		buttAddSS.setIcon(new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/ss-add.png")));
		buttAddSS.setToolTipText("Add Shortcut");
		buttAddSS.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String shortcut = JOptionPane.showInputDialog(frameTwipstr, "Shortcut:", statusTextArea.getSelectedText());
				if (!Validator.isNullOrBlank(shortcut)) {
					addShortcutButton(shortcut);
					toolBarShortcuts.repaint();
				}
				statusTextArea.requestFocusInWindow();
			}
		});
		
		buttInfo = new JButton("");
		buttInfo.setToolTipText("About & Help");
		final ImageIcon infoIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/info.png"));
		buttInfo.setIcon(infoIcon);
		buttInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LinkLabel info = new LinkLabel(Constants.APP_INFO_NAME_AND_VERSION, Constants.APP_INFO_URL);
				LinkLabel author = new LinkLabel(Constants.APP_INFO_AUTHOR, Constants.APP_INFO_AUTHOR_URL);
				JLabel help = new JLabel(Constants.APP_INFO_HELP);
				JOptionPane.showMessageDialog(
						frameTwipstr, 
						new Component[]{ info, author, help }, 
						"Twipstr :: About & Help", 
						JOptionPane.PLAIN_MESSAGE, infoIcon);
				statusTextArea.requestFocusInWindow();
			}
		});
		toolBarManage.add(buttInfo);
		
		buttPrefs = new JButton("");
		final ImageIcon prefsIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/prefs.png"));
		buttPrefs.setIcon(prefsIcon);
		buttPrefs.setToolTipText("Preferences");
		buttPrefs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cbCloseAfterSuccessfulStatusUpdate = new JCheckBox("Close Twipstr window after successful status update");
				cbCloseAfterSuccessfulStatusUpdate.setSelected(prefs.getBoolean(Constants.PROPERTY_USERPREF_CLOSE_WINDOW_AFTER_SUCCESSFUL_STATUS_UPDATE, false));
				JLabel labelImageUploadService = new JLabel("Preferred image upload service:");
				JComboBox cbImageUploadService = new JComboBox();
				cbImageUploadService.addItem(MediaProvider.YFROG.getName());
				cbImageUploadService.addItem(MediaProvider.TWITPIC.getName());
				cbImageUploadService.addItem(MediaProvider.TWITGOO.getName());
				cbImageUploadService.addItem(MediaProvider.TWIPPLE.getName());
				cbImageUploadService.addItem(MediaProvider.PLIXI.getName());
				cbImageUploadService.addItem(MediaProvider.IMG_LY.getName());
				String currentMediaProvider = prefs.get(Constants.PROPERTY_USERPREF_MEDIA_PROVIDER, Constants.DEFAULT_MEDIA_PROVIDER);
				cbImageUploadService.setSelectedItem(currentMediaProvider);
				JLabel labelBitly = new JLabel(
						"<html>" +
						"<br/>If you want to track statistics for j.mp/bit.ly URL-shortening," +
						"<br/>provide your j.mp/bit.ly username & API-key below." +
						"<br/>Note: you must be a registered j.mp/bit.ly user; you can get your API-key here:");
				JLabel labelBitlyUsername = new JLabel("Username:");
				JTextField tfBitlyUsername = new JTextField(prefs.get(Constants.PROPERTY_USERPREF_BITLY_USERNAME, ""));
				JLabel labelBitlyApiKey = new JLabel("API-key:");
				JTextField tfBitlyApiKey = new JTextField(prefs.get(Constants.PROPERTY_USERPREF_BITLY_API_KEY, ""));
				LinkLabel labelBitlyApiKeyLink = new LinkLabel("http://j.mp/a/your_api_key", "http://j.mp/a/your_api_key");
				JOptionPane.showMessageDialog(
						frameTwipstr, 
						new Component[] { 
								cbCloseAfterSuccessfulStatusUpdate, 
								labelImageUploadService, cbImageUploadService,
								labelBitly, labelBitlyApiKeyLink,
								labelBitlyUsername, tfBitlyUsername, 
								labelBitlyApiKey, tfBitlyApiKey
								}, 
						"Twipstr :: Preferences", 
						JOptionPane.PLAIN_MESSAGE, 
						prefsIcon);
				prefs.putBoolean(Constants.PROPERTY_USERPREF_CLOSE_WINDOW_AFTER_SUCCESSFUL_STATUS_UPDATE, cbCloseAfterSuccessfulStatusUpdate.isSelected());
				String selectedMediaProvider = cbImageUploadService.getSelectedItem().toString();
				if (!selectedMediaProvider.equals(currentMediaProvider)) {
					BackEnd.resetImageUploadService();
				}
				prefs.put(Constants.PROPERTY_USERPREF_MEDIA_PROVIDER, selectedMediaProvider);
				if (!Validator.isNullOrBlank(tfBitlyUsername.getText()) && !Validator.isNullOrBlank(tfBitlyApiKey.getText())) {
					prefs.put(Constants.PROPERTY_USERPREF_BITLY_USERNAME, tfBitlyUsername.getText());
					prefs.put(Constants.PROPERTY_USERPREF_BITLY_API_KEY, tfBitlyApiKey.getText());
				} else {
					prefs.remove(Constants.PROPERTY_USERPREF_BITLY_USERNAME);
					prefs.remove(Constants.PROPERTY_USERPREF_BITLY_API_KEY);
				}
				BackEnd.storePreferences(prefs);
				statusTextArea.requestFocusInWindow();
			}
		});
		toolBarManage.add(buttPrefs);
		toolBarManage.addSeparator();

		buttDecreaseFontSize = new JButton("");
		buttDecreaseFontSize.setIcon(new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/text-decr.png")));
		buttDecreaseFontSize.setToolTipText("Decrease Font Size");
		buttDecreaseFontSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = statusTextArea.getFont();
				int size = font.getSize();
				if (size > 2) size--;
				font = new Font(font.getName(), font.getStyle(), size);
				statusTextArea.setFont(font);
				statusTextArea.requestFocusInWindow();
			}
		});
		toolBarManage.add(buttDecreaseFontSize);
		
		buttIncreaseFontSize = new JButton("");
		buttIncreaseFontSize.setIcon(new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/text-incr.png")));
		buttIncreaseFontSize.setToolTipText("Increase Font Size");
		buttIncreaseFontSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = statusTextArea.getFont();
				int size = font.getSize();
				size++;
				font = new Font(font.getName(), font.getStyle(), size);
				statusTextArea.setFont(font);
				statusTextArea.requestFocusInWindow();
			}
		});
		toolBarManage.add(buttIncreaseFontSize);

		toolBarManage.add(buttAddSS);
		toolBarManage.addSeparator();
		
		toolBarShortcuts = new JToolBar();
		toolBarShortcuts.setFloatable(false);
		viewportToolBarShortcuts = new JViewport();
		viewportToolBarShortcuts.setView(toolBarShortcuts);
		viewportToolBarShortcuts.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() != 0) {
					Point pt = viewportToolBarShortcuts.getViewPosition();
					if (e.getWheelRotation() > 0) {
						pt.x += 15;
					} else {
						pt.x -= 15;
					}
					pt.x = Math.max(0, pt.x);
					pt.x = Math.min(viewportToolBarShortcuts.getView().getWidth() - viewportToolBarShortcuts.getWidth(), pt.x);
					viewportToolBarShortcuts.setViewPosition(pt);
				}
			}
		});
		panelControl.add(viewportToolBarShortcuts, BorderLayout.CENTER);
		
		panel = new JPanel();
		panelControl.add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		toolBar = new JToolBar();
		panel.add(toolBar, BorderLayout.CENTER);
		toolBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		toolBar.setFloatable(false);
		toolBar.addSeparator();
		
		final ImageIcon progressIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/progress.png"));
		
		final ImageIcon attachIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/attach.png"));
		buttAttach = new JButton("");
		buttAttach.setIcon(attachIcon);
		buttAttach.setToolTipText("Attach...");
		buttAttach.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final ImageFileChooser jFileChooser = new ImageFileChooser(false); // only image files are supported so far...
				if (lastFileChooserDir != null) {
					jFileChooser.setCurrentDirectory(lastFileChooserDir);
				}
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int rVal = jFileChooser.showOpenDialog(frameTwipstr);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                	lastFileChooserDir = jFileChooser.getCurrentDirectory();
					buttAttach.setIcon(progressIcon);
					buttAttach.setText("Uploading...");
					buttAttach.setEnabled(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							try {
								File imageFile = jFileChooser.getSelectedFile();
								if (imageFile != null) {
			                		String imageURL = BackEnd.uploadImage(imageFile);
			                		if (!Validator.isNullOrBlank(imageURL)) {
										insertURLWithSmartSpacing(imageURL);
			                			attachImage(imageFile, imageURL);
			                		}
								}
							} catch (BackEndException bee) {
								NotificationService.errorMessage(bee, frameTwipstr);
							} finally {
								buttAttach.setIcon(attachIcon);
								buttAttach.setText("");
								buttAttach.setEnabled(true);
								statusTextArea.requestFocusInWindow();
							}
						}
					});
                } else {
					statusTextArea.requestFocusInWindow();
                }
			}
		});
		toolBar.add(buttAttach);

		final ImageIcon shortenURLIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/shorten-url.png"));
		buttShortenURLs = new JButton("");
		buttShortenURLs.setToolTipText("Shorten URL");
		buttShortenURLs.setIcon(shortenURLIcon);
		buttShortenURLs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String url = JOptionPane.showInputDialog(frameTwipstr, "Original URL:");
				if (!Validator.isNullOrBlank(url)) {
					buttShortenURLs.setIcon(progressIcon);
					buttShortenURLs.setText("Shortening...");
					buttShortenURLs.setEnabled(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							try {
		            			insertURLWithSmartSpacing(BackEnd.shortenURL(url));
							} catch (BackEndException bee) {
								NotificationService.errorMessage(bee, frameTwipstr);
							} finally {
								buttShortenURLs.setIcon(shortenURLIcon);
								buttShortenURLs.setText("");
								buttShortenURLs.setEnabled(true);
								statusTextArea.requestFocusInWindow();
							}
						}
					});
				} else {
					statusTextArea.requestFocusInWindow();
				}
			}
		});
		
		toolBar.add(buttShortenURLs);
		toolBar.addSeparator();

		buttPost = new JButton("");
		buttPost.setHorizontalTextPosition(SwingConstants.LEFT);
		buttPost.setText("140");
		buttPost.setToolTipText("Number Of Characters Left");
		buttPost.setFont(Constants.FONT);
		buttPost.setForeground(Constants.COLOR_OK);
		buttPost.setIcon(new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/update.png")));
		buttPost.setToolTipText("UPDATE!");
		buttPost.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				postStatus();
			}
		});
		toolBar.add(buttPost);
	}
	
	private void insertURLWithSmartSpacing(String url) {
		int caretPos = statusTextArea.getCaretPosition();
		try {
			if (caretPos > 0 && !" ".equals(statusTextArea.getText(caretPos - 1, 1))) {
				url = " ".concat(url);
			}
			if (caretPos < statusTextArea.getText().length() && !" ".equals(statusTextArea.getText(caretPos, 1))) {
				url += " ";
			}
		} catch (BadLocationException e) {
			// ignore
		}
		statusTextArea.insert(url, caretPos);
	}
	
	private Dimension getImageSize() {
		int s = statusTextArea.getBounds().height;
		if (imageSize == null) {
			imageSize = new Dimension(s, s);
		} else {
			imageSize.setSize(s, s);
		}
		return imageSize;
	}
	
	private void attachImage(File imageFile, String imageURL) {
		try {
			
			ImagePanel imagePanel = ImagePanelFactory.buildImagePanel(imageFile, IMG_BG_COLOR);

			imagePanel.setPreferredSize(getImageSize());
			imagePanel.setName(imageURL);

			addVisibleImagePanel(imagePanel);
			
			if (imageFiles == null) {
				imageFiles = new HashMap<String, String>();
			}
			imageFiles.put(imageURL, imageFile.getAbsolutePath());
			
			reLayoutImages();
			
		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
		}
	}
	
	private void updateImages() {
		boolean reLayoutImages = false;
		if (imagePanelsCache != null && !imagePanelsCache.isEmpty()) {
			List<ImagePanel> pl = null;
			for (ImagePanel imagePanel : imagePanelsCache) {
				if (statusTextArea.getText().contains(imagePanel.getName())) {
					if (pl == null) {
						pl = new ArrayList<ImagePanel>();
					}
					pl.add(imagePanel);
				}
			}
			if (pl != null && !pl.isEmpty()) {
				restoreCacheImagePanels(pl);
				reLayoutImages = true;
			}
		}
		if (imagePanelsVisible != null && !imagePanelsVisible.isEmpty()) {
			List<ImagePanel> pl = null;
			for (ImagePanel imagePanel : imagePanelsVisible) {
				if (!statusTextArea.getText().contains(imagePanel.getName())) {
					if (pl == null) {
						pl = new ArrayList<ImagePanel>();
					}
					pl.add(imagePanel);
				}
			}
			if (pl != null && !pl.isEmpty()) {
				removeVisibleImagePanels(pl);
				reLayoutImages = true;
			}
		}
		if (reLayoutImages) {
			reLayoutImages();
		}
	}
	
	private void reLayoutImages() {
		panelImages.removeAll();
		if (imagePanelsVisible == null || imagePanelsVisible.isEmpty()) {
			panelImages.setVisible(false);
		} else {
			panelImages.setLayout(new GridLayout(1, imagePanelsVisible.size()));
			for (final ImagePanel ip : imagePanelsVisible) {
				final JPanel imageFrame = new JPanel(new BorderLayout());
				imageFrame.setName(ip.getName());
				JPanel removeImagePanel = new JPanel(new BorderLayout());
				removeImagePanel.setBackground(IMG_BG_COLOR);
				JLabel removeImageLink = new JLabel();
				removeImageLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				removeImageLink.setIcon(new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/remove.png")));
				removeImageLink.setPreferredSize(new Dimension(24, 24));
				removeImageLink.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						removeVisibleImagePanel(ip);
						panelImages.remove(imageFrame);
						int idx = statusTextArea.getText().indexOf(ip.getName());
						if (idx != -1) {
							statusTextArea.setText(statusTextArea.getText().replace(ip.getName(), ""));
							statusTextArea.setCaretPosition(idx);
						}
						if (imagePanelsVisible.isEmpty()) {
							panelImages.setVisible(false);
						}
						statusTextArea.requestFocusInWindow();
					}
				});
				removeImagePanel.add(removeImageLink, BorderLayout.NORTH);
				imageFrame.add(removeImagePanel, BorderLayout.EAST);
				imageFrame.add(ip, BorderLayout.CENTER);
				panelImages.add(imageFrame);
			}
			panelImages.setVisible(true);
		}
	}
	
	private void addVisibleImagePanel(ImagePanel imagePanel) {
		if (imagePanelsVisible == null) {
			imagePanelsVisible = new LinkedList<ImagePanel>();
		}
		imagePanelsVisible.add(imagePanel);
	}
	
	private void addVisibleImagePanels(List<ImagePanel> imagePanels) {
		if (imagePanelsVisible == null) {
			imagePanelsVisible = new LinkedList<ImagePanel>();
		}
		imagePanelsVisible.addAll(imagePanels);
	}

	private void removeVisibleImagePanel(ImagePanel imagePanel) {
		if (imagePanelsVisible != null) {
			imagePanelsVisible.remove(imagePanel);
			cacheImagePanel(imagePanel);
		}
	}
	
	private void removeVisibleImagePanels(List<ImagePanel> imagePanels) {
		if (imagePanelsVisible != null) {
			imagePanelsVisible.removeAll(imagePanels);
			cacheImagePanels(imagePanels);
		}
	}

	private void cacheImagePanel(ImagePanel imagePanel) {
		if (imagePanelsCache == null) {
			imagePanelsCache = new LinkedList<ImagePanel>();
		}
		imagePanelsCache.add(imagePanel);
	}

	private void cacheImagePanels(List<ImagePanel> imagePanels) {
		if (imagePanelsCache == null) {
			imagePanelsCache = new LinkedList<ImagePanel>();
		}
		imagePanelsCache.addAll(imagePanels);
	}

	private void restoreCacheImagePanels(List<ImagePanel> imagePanels) {
		if (imagePanelsCache != null) {
			imagePanelsCache.removeAll(imagePanels);
		}
		addVisibleImagePanels(imagePanels);
	}
	
	private void restoreState() {
		prefs = BackEnd.loadPreferences();
		
		int wpxValue = prefs.getInt(Constants.PROPERTY_WINDOW_COORDINATE_X, frameTwipstr.getToolkit().getScreenSize().width / 4);
		int wpyValue = prefs.getInt(Constants.PROPERTY_WINDOW_COORDINATE_Y, frameTwipstr.getToolkit().getScreenSize().height / 4);
		int wwValue = prefs.getInt(Constants.PROPERTY_WINDOW_WIDTH, Constants.DEFAULT_WINDOW_WIDTH);
		int whValue = prefs.getInt(Constants.PROPERTY_WINDOW_HEIGHT, Constants.DEFAULT_WINDOW_HEIGHT);
		frameTwipstr.setLocation(wpxValue, wpyValue);
		frameTwipstr.setSize(wwValue, whValue);
		
		int fontSize = prefs.getInt(Constants.PROPERTY_FONT_SIZE, -1);
		if (fontSize != -1) {
			Font font = new Font(Constants.FONT.getName(), Constants.FONT.getStyle(), fontSize);
			statusTextArea.setFont(font);
		}
		
		String text = prefs.get(Constants.PROPERTY_TEXT, null);
		if (text != null) {
			statusTextArea.getDocument().removeUndoableEditListener(undoableEditListener);
			statusTextArea.setText(text);
			statusTextArea.getDocument().addUndoableEditListener(undoableEditListener);
		}
		
		String imageURLs = prefs.get(Constants.PROPERTY_IMAGE_URLS, null);
		if (!Validator.isNullOrBlank(imageURLs)) {
			String[] imageURLsArr = imageURLs.split(Constants.VALUES_SEPARATOR);
			for (String imageURL : imageURLsArr) {
				String imagePath = prefs.get(Constants.PROPERTY_PREFIX_IMAGE.concat(imageURL), null);
				if (imagePath != null) {
					File imageFile = new File(imagePath);
					if (imageFile.exists()) {
						attachImage(imageFile, imageURL);
					}
				}
			}
		}
		
		String lastImgDirPath = prefs.get(Constants.PROPERTY_LAST_IMG_DIR, null);
		if (!Validator.isNullOrBlank(lastImgDirPath)) {
			lastFileChooserDir = new File(lastImgDirPath);
			if (!lastFileChooserDir.isDirectory()) {
				lastFileChooserDir = null;
			}
		}
		
		String[] shortcuts = prefs.get(Constants.PROPERTY_SHORTCUTS, Constants.DEFAULT_SHORTCUTS).split(Constants.VALUES_SEPARATOR);
		for (String shortcut : shortcuts) {
			addShortcutButton("" + shortcut);
		}
		
	}

	private void persistState() {
		try {
			if (prefs != null) {
				prefs.putInt(Constants.PROPERTY_WINDOW_COORDINATE_X, frameTwipstr.getLocation().x);
				prefs.putInt(Constants.PROPERTY_WINDOW_COORDINATE_Y, frameTwipstr.getLocation().y);
				prefs.putInt(Constants.PROPERTY_WINDOW_WIDTH, frameTwipstr.getSize().width);
				prefs.putInt(Constants.PROPERTY_WINDOW_HEIGHT, frameTwipstr.getSize().height);
				
				if (!Validator.isNullOrBlank(statusTextArea.getText())) {
					prefs.put(Constants.PROPERTY_TEXT, statusTextArea.getText());
				} else {
					prefs.remove(Constants.PROPERTY_TEXT);
				}
				
				if (statusTextArea.getFont().getSize() != Constants.FONT.getSize()) {
					prefs.putInt(Constants.PROPERTY_FONT_SIZE, statusTextArea.getFont().getSize());
				} else {
					prefs.remove(Constants.PROPERTY_FONT_SIZE);
				}
				
				String imageURLs = null;
				if (imagePanelsVisible == null || imagePanelsVisible.isEmpty()) {
						prefs.remove(Constants.PROPERTY_IMAGE_URLS);
				} else {
					for (JPanel ip : imagePanelsVisible) {
						String imageURL = ip.getName();
						prefs.put(Constants.PROPERTY_PREFIX_IMAGE.concat(imageURL), imageFiles.get(imageURL));
						if (imageURLs == null) {
							imageURLs = imageURL;
						} else {
							imageURLs += Constants.VALUES_SEPARATOR.concat(imageURL);
						}
					}
					prefs.put(Constants.PROPERTY_IMAGE_URLS, imageURLs);
				}
				for (String node : prefs.keys()) {
					if (node.startsWith(Constants.PROPERTY_PREFIX_IMAGE)) {
						if (imageURLs == null || !imageURLs.contains(node.substring(Constants.PROPERTY_PREFIX_IMAGE.length()))) {
							prefs.remove(node);
						}
					}
				}
				
				String shortcuts = "";
				for (Component cmp : toolBarShortcuts.getComponents()) {
					if (cmp.getName() != null) {
						shortcuts += cmp.getName() + Constants.VALUES_SEPARATOR;
					}
				}
				prefs.put(Constants.PROPERTY_SHORTCUTS, shortcuts);
				
				if (lastFileChooserDir != null && lastFileChooserDir.isDirectory()) {
					prefs.put(Constants.PROPERTY_LAST_IMG_DIR, lastFileChooserDir.getAbsolutePath());
				}
				
				BackEnd.storePreferences(prefs);
			}
		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
		}
	}

	private void addShortcutButton(String shortcut) {
		String tooltip = null;
		String caption = shortcut;
		if (shortcut.length() > Constants.MAX_SHORTCUT_CAPTION_LENGTH) {
			tooltip = shortcut;
			caption = shortcut.substring(0, Constants.MAX_SHORTCUT_CAPTION_LENGTH).concat("…");
		}
		final JButton buttShortcut = new JButton(caption);
		buttShortcut.setName(shortcut);
		if (tooltip != null) {
			buttShortcut.setToolTipText(tooltip);
		}
		buttShortcut.setFont(Constants.FONT);
		buttShortcut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0 && (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
					toolBarShortcuts.setVisible(false);
                	toolBarShortcuts.remove(buttShortcut);
					toolBarShortcuts.setVisible(true);
				} else if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
					toolBarShortcuts.setVisible(false);
                	int idx = toolBarShortcuts.getComponentIndex(buttShortcut);
                	toolBarShortcuts.remove(buttShortcut);
                	toolBarShortcuts.add(buttShortcut, idx - 1);
					toolBarShortcuts.setVisible(true);
				} else if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
					toolBarShortcuts.setVisible(false);
                	int idx = toolBarShortcuts.getComponentIndex(buttShortcut);
                	toolBarShortcuts.remove(buttShortcut);
                	if (idx == toolBarShortcuts.getComponentCount()) idx = -1;
                	toolBarShortcuts.add(buttShortcut, idx + 1);
					toolBarShortcuts.setVisible(true);
				} else {
					String shortcut = buttShortcut.getToolTipText();
					if (shortcut == null) {
						shortcut = buttShortcut.getText();
					}
					insertShortcut(shortcut);
				}
			}
		});
		toolBarShortcuts.add(buttShortcut);
		// scroll to added shortcut
		Point pt = viewportToolBarShortcuts.getViewPosition();
		pt.x = viewportToolBarShortcuts.getView().getWidth();
		viewportToolBarShortcuts.setViewPosition(pt);
	}
	
	private void insertShortcut(String shortcut) {
		statusTextArea.insert(shortcut, statusTextArea.getCaretPosition());
		statusTextArea.requestFocusInWindow();
	}
	
	private void updateCounter() {
		int charsLeft = 140 - statusTextArea.getText().length();
		Color color;
		if (charsLeft < 0) {
			color = Constants.COLOR_OVER_LIMIT;
		} else if (charsLeft <= Constants.LENGTH_LIMIT) {
			color = Constants.COLOR_CLOSE_TO_LIMIT;
		} else if (charsLeft <= Constants.LENGTH_WARNING) {
			color = Constants.COLOR_WARNING;
		} else {
			color = Constants.COLOR_OK;
		}
		buttPost.setText("" + charsLeft);
		buttPost.setForeground(color);
		if (charsLeft >= 0) {
			buttPost.setEnabled(true);
		} else {
			buttPost.setEnabled(false);
		}
	}
	
	private void updateState() {
		updateCounter();
		updateImages();
	}
	
	private void postStatus() {
		try {
			if (!Validator.isNullOrBlank(statusTextArea.getText()) && statusTextArea.getText().length() <= 140) {
				if (BackEnd.updateStatus(statusTextArea.getText())) {
					statusTextArea.setText("");
					undoManager.discardAllEdits();
					if (imagePanelsVisible != null) {
						imagePanelsVisible.clear();
					}
					if (imagePanelsCache != null) {
						imagePanelsCache.clear();
					}
					reLayoutImages();
					if (prefs.getBoolean(Constants.PROPERTY_USERPREF_CLOSE_WINDOW_AFTER_SUCCESSFUL_STATUS_UPDATE, false)) {
						onExit();
						System.exit(0);
					}
				}
			}
		} catch (Throwable cause) {
			NotificationService.errorMessage(cause, frameTwipstr);
		} finally {
			statusTextArea.requestFocusInWindow();
		}
	}
	
	private void onExit() {
		persistState();
	}

}
