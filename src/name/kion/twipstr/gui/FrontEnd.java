/**
 * Created on Jan 6, 2011
 */
package name.kion.twipstr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

/**
 * @author kion
 */
public class FrontEnd {
	
	final ImageIcon progressIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/progress.png"));
	final ImageIcon attachIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/attach.png"));
	final ImageIcon linkIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/link.png"));
	final ImageIcon infoIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/info.png"));
	final ImageIcon prefsIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/prefs.png"));
	final ImageIcon symbolsIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/symbols.png"));
	final ImageIcon editIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/edit.png"));
	final ImageIcon addIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/add.png"));
	final ImageIcon removeIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/remove.png"));
	final ImageIcon textDecrIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/text-decr.png"));
	final ImageIcon textIncrIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/text-incr.png"));
	final ImageIcon postIcon = new ImageIcon(FrontEnd.class.getResource("/name/kion/twipstr/res/post.png"));
	
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
	
	private Map<String, String> symbolMap;
	
	private int maxLength = 140;
	
	private int dividerLocation = -1;
	private int dividerSize;
	
	private JFrame frameTwipstr;
	private JPanel panelMain;
	private JToolBar toolBar;
	private JButton btnPost;
	private JTextArea statusTextArea;
	private JPanel panelControl;
	private JToolBar toolBarManage;
	private JButton btnIncreaseFontSize;
	private JButton btnDecreaseFontSize;
	private JButton btnInfo;
	private JButton btnShortenURL;
	private JButton btnPrefs;
	private JButton btnAttach;
	private JPanel panelImages;
	private JPanel panelSymbols;
	private JTabbedPane symbolsTabPane;
	private JToggleButton btnToggleSymbols;
	private JButton btnEditSymbols;
	private JButton btnDeleteSymbols;
	private JButton btnAddSymbols;
	private JSplitPane splitPane;
	private JPanel panelContent;

	/**
	 * Launch the application.
	 */
	public static void init() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final FrontEnd window = new FrontEnd();
					window.frameTwipstr.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							window.onExit();
						}
					});
					window.frameTwipstr.setVisible(true);
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
		
		panelControl = new JPanel();
		panelMain.add(panelControl, BorderLayout.SOUTH);
		panelControl.setLayout(new BorderLayout(0, 0));
		
		toolBarManage = new JToolBar();
		toolBarManage.setFloatable(false);
		panelControl.add(toolBarManage, BorderLayout.CENTER);
		
		btnInfo = new JButton(infoIcon);
		btnInfo.setFocusable(false);
		btnInfo.setToolTipText("About & Help");
		btnInfo.addActionListener(new ActionListener() {
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
		toolBarManage.add(btnInfo);
		
		btnPrefs = new JButton(prefsIcon);
		btnPrefs.setFocusable(false);
		btnPrefs.setToolTipText("Preferences");
		btnPrefs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel labelLAF = new JLabel("Look & Feel:");
				final JComboBox<String> cbLAF = new JComboBox<String>();
				for (String laf : Constants.SUPPORTED_LAFS.keySet()) {
					cbLAF.addItem(laf);
				}
				String currentLAF = prefs.get(Constants.PROPERTY_LAF, Constants.DEFAULT_LAF);
				cbLAF.setSelectedItem(currentLAF);
				cbLAF.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							UIManager.setLookAndFeel(Constants.SUPPORTED_LAFS.get(cbLAF.getSelectedItem().toString()));
							SwingUtilities.updateComponentTreeUI(frameTwipstr);
							SwingUtilities.updateComponentTreeUI(getFirstParentComponent(cbLAF, JDialog.class));
						} catch (Throwable cause) {
							// ignore
							cause.printStackTrace(System.err);
						}
					}
				});
				JCheckBox cbCloseAfterSuccessfulStatusUpdate = new JCheckBox("Close Twipstr window after successful status update");
				cbCloseAfterSuccessfulStatusUpdate.setSelected(prefs.getBoolean(Constants.PROPERTY_USERPREF_CLOSE_WINDOW_AFTER_SUCCESSFUL_STATUS_UPDATE, false));
				JLabel labelImageUploadService = new JLabel("Preferred image upload service:");
				JComboBox<String> cbImageUploadService = new JComboBox<String>();
				for (String mp : Constants.SUPPORTED_MEDIA_PROVIDERS) {
					cbImageUploadService.addItem(mp);
				}
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
				JButton resetSymbols = new JButton("Reset symbols");
				resetSymbols.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						int opt = JOptionPane.showConfirmDialog(frameTwipstr, 
								"All current symbols sets will be deleted and replaced with default ones", 
								"Reset symbols", JOptionPane.OK_CANCEL_OPTION);
						if (opt == JOptionPane.OK_OPTION) {
							renderSymbols(Arrays.asList(Constants.DEFAULT_SYMBOLS.split(Constants.SYMBOL_GROUP_SEPARATOR_PATTERN)));
							resetSymbolPrefs();
						}
					}
				});
				JOptionPane.showMessageDialog(
						frameTwipstr, 
						new Component[] { 
								labelLAF, cbLAF,
								cbCloseAfterSuccessfulStatusUpdate, 
								labelImageUploadService, cbImageUploadService,
								labelBitly, labelBitlyApiKeyLink,
								labelBitlyUsername, tfBitlyUsername, 
								labelBitlyApiKey, tfBitlyApiKey,
								resetSymbols
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
				String selectedLAF = cbLAF.getSelectedItem().toString();
				prefs.put(Constants.PROPERTY_LAF, selectedLAF);
				BackEnd.storePreferences(prefs);
				statusTextArea.requestFocusInWindow();
			}
		});
		toolBarManage.add(btnPrefs);
		toolBarManage.addSeparator();

		btnToggleSymbols = new JToggleButton();
		btnToggleSymbols.setFocusable(false);
		btnToggleSymbols.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (btnToggleSymbols.isSelected()) {
					if (dividerLocation > dividerSize) {
						splitPane.setDividerLocation(dividerLocation);
					} else {
						splitPane.setDividerLocation(0.25);
					}
				}
				splitPane.setDividerSize(btnToggleSymbols.isSelected() ? dividerSize : 0);
				panelSymbols.setVisible(btnToggleSymbols.isSelected());
				btnEditSymbols.setVisible(btnToggleSymbols.isSelected());
				btnDeleteSymbols.setVisible(btnToggleSymbols.isSelected());
				btnAddSymbols.setVisible(btnToggleSymbols.isSelected());
				if (btnToggleSymbols.isSelected() && symbolsTabPane == null) {
					List<String> symbols = null;
					int i = 0;
					while (i != -1) {
						String symbolSet = prefs.get(Constants.PROPERTY_PREFIX_SYMBOLS + i++, null);
						if (symbolSet != null) {
							if (symbols == null) {
								symbols = new ArrayList<String>();
							}
							symbols.add(symbolSet);
						} else {
							i = -1;
						}
					}
					if (symbols == null) {
						symbols = Arrays.asList(Constants.DEFAULT_SYMBOLS.split(Constants.SYMBOL_GROUP_SEPARATOR_PATTERN));
					}
					renderSymbols(symbols);
				}
				statusTextArea.requestFocusInWindow();
			}
		});
		btnToggleSymbols.setToolTipText("Symbols");
		btnToggleSymbols.setIcon(symbolsIcon);
		toolBarManage.add(btnToggleSymbols);

		btnEditSymbols = new JButton(editIcon);
		btnEditSymbols.setVisible(false);
		btnEditSymbols.setFocusable(false);
		btnEditSymbols.setToolTipText("Edit selected set of symbols");
		btnEditSymbols.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component cmp = symbolsTabPane.getSelectedComponent();
				String key = cmp == null ? null : cmp.getName();
				if (key != null) {
					String symbols = showSymbolsEditor(key);
					if (symbols != null) {
						if (Validator.isNullOrBlank(symbols)) {
							symbolsTabPane.remove(cmp);
							symbolMap.remove(key);
							updateSymbolPrefs();
						} else if (!symbols.equals(symbolMap.get(key))) {
							symbolMap.put(key, symbols);
							((JViewport) cmp).setView(getSymbolsPanel(symbols));
							updateSymbolPrefs();
						}
					}
				}
			}
		});
		toolBarManage.add(btnEditSymbols);

		btnAddSymbols = new JButton(addIcon);
		btnAddSymbols.setVisible(false);
		btnAddSymbols.setFocusable(false);
		btnAddSymbols.setToolTipText("Add new set of symbols");
		btnAddSymbols.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String symbols = showSymbolsEditor(null);
				if (symbols != null && !Validator.isNullOrBlank(symbols)) {
					initSymbolSet(symbols);
					updateSymbolPrefs();
				}
			}
		});
		toolBarManage.add(btnAddSymbols);
		
		btnDeleteSymbols = new JButton(removeIcon);
		btnDeleteSymbols.setVisible(false);
		btnDeleteSymbols.setFocusable(false);
		btnDeleteSymbols.setToolTipText("Delete selected set of symbols");
		btnDeleteSymbols.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opt = JOptionPane.showConfirmDialog(frameTwipstr, 
						"Selected set of symbols will be deleted", 
						"Delete symbols", JOptionPane.OK_CANCEL_OPTION);
				if (opt == JOptionPane.OK_OPTION) {
					Component cmp = symbolsTabPane.getSelectedComponent();
					String key = cmp == null ? null : cmp.getName();
					if (key != null) {
						symbolsTabPane.remove(cmp);
						symbolMap.remove(key);
						updateSymbolPrefs();
					}
				}
			}
		});
		toolBarManage.add(btnDeleteSymbols);

		toolBarManage.addSeparator();
		
		btnDecreaseFontSize = new JButton(textDecrIcon);
		btnDecreaseFontSize.setFocusable(false);
		btnDecreaseFontSize.setToolTipText("Decrease Font Size");
		btnDecreaseFontSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = statusTextArea.getFont();
				int size = font.getSize();
				if (size > 2) size--;
				font = new Font(font.getName(), font.getStyle(), size);
				statusTextArea.setFont(font);
				updateSymbolsFontSize(font);
				statusTextArea.requestFocusInWindow();
			}
		});
		toolBarManage.add(btnDecreaseFontSize);
		
		btnIncreaseFontSize = new JButton(textIncrIcon);
		btnIncreaseFontSize.setFocusable(false);
		btnIncreaseFontSize.setToolTipText("Increase Font Size");
		btnIncreaseFontSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = statusTextArea.getFont();
				int size = font.getSize();
				size++;
				font = new Font(font.getName(), font.getStyle(), size);
				statusTextArea.setFont(font);
				updateSymbolsFontSize(font);
				statusTextArea.requestFocusInWindow();
			}
		});
		toolBarManage.add(btnIncreaseFontSize);
		toolBarManage.addSeparator();
		
		toolBar = new JToolBar();
		panelControl.add(toolBar, BorderLayout.EAST);
		toolBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		toolBar.setFloatable(false);
		toolBar.addSeparator();
		btnAttach = new JButton(attachIcon);
		btnAttach.setFocusable(false);
		btnAttach.setToolTipText("Attach...");
		btnAttach.addActionListener(new ActionListener() {
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
					btnAttach.setIcon(progressIcon);
					btnAttach.setText("Uploading...");
					btnAttach.setEnabled(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							try {
								File imageFile = jFileChooser.getSelectedFile();
								if (imageFile != null) {
									String imageURL;
									if (BackEnd.usingSeparateImageUploading()) {
				                		imageURL = BackEnd.uploadImage(imageFile);
				                		if (!Validator.isNullOrBlank(imageURL)) {
											insertURLWithSmartSpacing(imageURL);
				                		}
									} else {
										maxLength -= BackEnd.attachMedia(imageFile);
										updateCounter();
										imageURL = imageFile.getAbsolutePath();
									}
		                			attachImage(imageFile, imageURL);
								}
							} catch (BackEndException bee) {
								NotificationService.errorMessage(bee, frameTwipstr);
							} finally {
								btnAttach.setIcon(attachIcon);
								btnAttach.setText("");
								btnAttach.setEnabled(true);
								statusTextArea.requestFocusInWindow();
							}
						}
					});
                } else {
					statusTextArea.requestFocusInWindow();
                }
			}
		});
		toolBar.add(btnAttach);
		btnShortenURL = new JButton(linkIcon);
		btnShortenURL.setFocusable(false);
		btnShortenURL.setToolTipText("Insert/Shorten URL");
		btnShortenURL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String url = JOptionPane.showInputDialog(frameTwipstr, "Original URL:");
				if (!Validator.isNullOrBlank(url)) {
					btnShortenURL.setIcon(progressIcon);
					btnShortenURL.setText("Shortening...");
					btnShortenURL.setEnabled(false);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							try {
		            			insertURLWithSmartSpacing(BackEnd.shortenURL(url));
							} catch (BackEndException bee) {
								NotificationService.errorMessage(bee, frameTwipstr);
							} finally {
								btnShortenURL.setIcon(linkIcon);
								btnShortenURL.setText("");
								btnShortenURL.setEnabled(true);
								statusTextArea.requestFocusInWindow();
							}
						}
					});
				} else {
					statusTextArea.requestFocusInWindow();
				}
			}
		});
		
		toolBar.add(btnShortenURL);
		toolBar.addSeparator();
		
		btnPost = new JButton(postIcon);
		btnPost.setFocusable(false);
		btnPost.setHorizontalTextPosition(SwingConstants.LEFT);
		btnPost.setText("" + maxLength);
		btnPost.setToolTipText("Number Of Characters Left");
		btnPost.setFont(Constants.FONT);
		btnPost.setForeground(Constants.COLOR_OK);
		btnPost.setToolTipText("POST!");
		btnPost.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				postStatus();
			}
		});
		toolBar.add(btnPost);
		
		panelSymbols = new JPanel();
		panelSymbols.setVisible(false);
		panelSymbols.setLayout(new BorderLayout(0, 0));
		
		panelContent = new JPanel();
		panelContent.setLayout(new BorderLayout(0, 0));
		
		panelImages = new JPanel();
		panelContent.add(panelImages, BorderLayout.EAST);
		
		statusTextArea = new JTextArea(new TwitterStatusDocument());
		panelContent.add(statusTextArea, BorderLayout.CENTER);
		statusTextArea.setBackground(Constants.TEXT_BG_COLOR);
		statusTextArea.setBorder(new LineBorder(Constants.TEXT_BG_COLOR, 15));
		statusTextArea.setFont(Constants.FONT);
		statusTextArea.setLineWrap(true);
		statusTextArea.setWrapStyleWord(true);
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
		addTextAreaKeyListener(statusTextArea);
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
		panelImages.setVisible(false);
		
		splitPane = new JSplitPane();
		splitPane.setRightComponent(panelContent);
		splitPane.setLeftComponent(panelSymbols);
		dividerSize = splitPane.getDividerSize();
		splitPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (splitPane.getDividerLocation() > 0) dividerLocation = splitPane.getDividerLocation();
			}
		});
		panelMain.add(splitPane, BorderLayout.CENTER);
	}
	
	private Component getFirstParentComponent(Component child, Class<? extends Component> parentClass) {
		Container c = child.getParent();
		while (c != null) {
			if (c.getClass().equals(parentClass)) break;
			c = c.getParent();
		}
		return c;
	}
	
	private void addTextAreaKeyListener(JTextArea textArea) {
		textArea.addKeyListener(new KeyAdapter() {
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
			ImagePanel imagePanel = ImagePanelFactory.buildImagePanel(imageFile, Constants.IMG_BG_COLOR);

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
				if (imagePanel.getName().startsWith("http") && statusTextArea.getText().contains(imagePanel.getName())) {
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
				if (imagePanel.getName().startsWith("http") && !statusTextArea.getText().contains(imagePanel.getName())) {
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
				removeImagePanel.setBackground(Constants.IMG_BG_COLOR);
				JLabel removeImageLink = new JLabel();
				removeImageLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				removeImageLink.setIcon(removeIcon);
				removeImageLink.setPreferredSize(new Dimension(24, 24));
				removeImageLink.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						removeVisibleImagePanel(ip);
						panelImages.remove(imageFrame);
						String imageURL = ip.getName();
						int idx = statusTextArea.getText().indexOf(imageURL);
						if (idx != -1) {
							statusTextArea.replaceRange("", idx, idx + imageURL.length());
							statusTextArea.setCaretPosition(idx);
						} else {
							maxLength += BackEnd.cancelMedia(ip.getName());
							updateCounter();
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
			imagePanelsVisible = new ArrayList<ImagePanel>();
		}
		imagePanelsVisible.add(imagePanel);
	}
	
	private void addVisibleImagePanels(List<ImagePanel> imagePanels) {
		if (imagePanelsVisible == null) {
			imagePanelsVisible = new ArrayList<ImagePanel>();
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
			imagePanelsCache = new ArrayList<ImagePanel>();
		}
		imagePanelsCache.add(imagePanel);
	}

	private void cacheImagePanels(List<ImagePanel> imagePanels) {
		if (imagePanelsCache == null) {
			imagePanelsCache = new ArrayList<ImagePanel>();
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
		try {
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
				String[] imageURLsArr = imageURLs.split(Constants.URL_SEPARATOR);
				for (String imageURL : imageURLsArr) {
					String imagePath = prefs.get(Constants.PROPERTY_PREFIX_IMAGE.concat(imageURL), null);
					if (imagePath != null) {
						File imageFile = new File(imagePath);
						if (imageFile.exists()) {
							if (!imageURL.startsWith("http")) {
								maxLength -= BackEnd.attachMedia(imageFile);
								updateCounter();
							}
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
			
			dividerLocation = prefs.getInt(Constants.PROPERTY_DIVIDER_LOCATION, -1);
			btnToggleSymbols.setSelected(prefs.getBoolean(Constants.PROPERTY_SYMBOLS_ENABLED, false));
			
			statusTextArea.requestFocusInWindow();

		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
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
						prefs.put(Constants.PROPERTY_PREFIX_IMAGE.concat(imageURL != null ? imageURL : ""), imageFiles.get(imageURL));
						if (imageURLs == null) {
							imageURLs = imageURL;
						} else {
							imageURLs += Constants.URL_SEPARATOR.concat(imageURL);
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
				
				if (lastFileChooserDir != null && lastFileChooserDir.isDirectory()) {
					prefs.put(Constants.PROPERTY_LAST_IMG_DIR, lastFileChooserDir.getAbsolutePath());
				}
				
				prefs.putBoolean(Constants.PROPERTY_SYMBOLS_ENABLED, btnToggleSymbols.isSelected());
				prefs.putInt(Constants.PROPERTY_DIVIDER_LOCATION, dividerLocation);
				
				BackEnd.storePreferences(prefs);
			}
		} catch (Throwable cause) {
			cause.printStackTrace(System.err);
		}
	}
	
	private void updateSymbolsFontSize(Font font) {
		for (int i = 0; i < symbolsTabPane.getTabCount(); i++) {
			for (Component cmp : ((JPanel) ((JViewport) symbolsTabPane.getComponentAt(i)).getView()).getComponents()) {
				((JLabel) cmp).setFont(font);
			}
		}
	}

	private Component getSymbolCtrl(String symbol) {
		final JLabel lblSymbol = new JLabel(symbol);
		lblSymbol.setFont(statusTextArea.getFont());
		lblSymbol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblSymbol.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				insertSymbol(lblSymbol.getText());
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				lblSymbol.setForeground(Constants.COLOR_SYMBOL_HIGHLIGHT);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				lblSymbol.setForeground(Color.BLACK);
			}
		});
		return lblSymbol;
	}
	
	private void insertSymbol(String symbol) {
		statusTextArea.insert(symbol, statusTextArea.getCaretPosition());
		statusTextArea.requestFocusInWindow();
	}
	
	private void renderSymbols(List<String> symbols) {
		if (symbolsTabPane == null) {
			symbolsTabPane = new JTabbedPane(JTabbedPane.TOP);
			symbolsTabPane.setFocusable(false);
			panelSymbols.add(symbolsTabPane, BorderLayout.CENTER);
			TabMoveListener tabMoveListener = new TabMoveListener(new Runnable() {
				@Override
				public void run() {
					updateSymbolPrefs();
				}
			});
			symbolsTabPane.addMouseListener(tabMoveListener);
			symbolsTabPane.addMouseMotionListener(tabMoveListener);
		} else {
			symbolsTabPane.removeAll();
		}
		symbolMap = new LinkedHashMap<String, String>(symbols.size(), 1f);
		for (String s : symbols) {
			initSymbolSet(s);
		}
	}
	
	private void initSymbolSet(String symbols) {
		String key = symbolsTabPane.getTabCount() + "";
		JPanel panel = getSymbolsPanel(symbols);
		JViewport viewport = getSymbolsViewport(panel, key);
		symbolsTabPane.addTab(((JLabel) panel.getComponent(0)).getText(), viewport);
		symbolMap.put(key, symbols);
	}
	
	private JPanel getSymbolsPanel(String symbols) {
		JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT));
		
		String[] symbolArr = symbols.split(Constants.SYMBOL_SEPARATOR_PATTERN);
		for (String s : symbolArr) {
			panel.add(getSymbolCtrl(s));
		}
		
		return panel;
	}
	
	private JViewport getSymbolsViewport(JPanel view, String key) {
		final JViewport viewport = new JViewport();
		viewport.setName(key);
		viewport.setView(view);
		viewport.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() != 0) {
					Point pt = viewport.getViewPosition();
					if (e.getWheelRotation() > 0) {
						pt.y += 10;
					} else {
						pt.y -= 10;
					}
					pt.y = Math.max(0, pt.y);
					pt.y = Math.min(viewport.getView().getHeight() - viewport.getHeight(), pt.y);
					viewport.setViewPosition(pt);
				}
			}
		});
		return viewport;
	}
	
	private String showSymbolsEditor(String key) {
		JTextArea editSymbolsTextArea = new JTextArea();
		if (key != null) {
			editSymbolsTextArea.setText(symbolMap.get(key));
		}
		addTextAreaKeyListener(editSymbolsTextArea);
		editSymbolsTextArea.setLineWrap(true);
		editSymbolsTextArea.setFont(Constants.FONT);
		editSymbolsTextArea.setBorder(new LineBorder(Constants.TEXT_BG_COLOR, 15));
		editSymbolsTextArea.setBackground(Color.WHITE);
		JScrollPane editSymbolsScrollPane = new JScrollPane(editSymbolsTextArea);
		editSymbolsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		editSymbolsScrollPane.setPreferredSize(new Dimension(symbolsTabPane.getWidth(), symbolsTabPane.getHeight()));
		editSymbolsTextArea.getDocument().addUndoableEditListener(undoableEditListener);
		int opt = JOptionPane.showConfirmDialog(frameTwipstr, editSymbolsScrollPane, "Edit Symbols", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (opt == JOptionPane.OK_OPTION) {
			return editSymbolsTextArea.getText();
		}
		return null;
	}

	private void updateSymbolPrefs() {
		handleSymbolPrefs(false);
	}
	
	private void resetSymbolPrefs() {
		handleSymbolPrefs(true);
	}
	
	private void handleSymbolPrefs(boolean reset) {
		// remove existing symbol-props first
		int i = 0;
		while (i != -1) {
			String propName = Constants.PROPERTY_PREFIX_SYMBOLS + i;
			String symbolsGroup = prefs.get(propName, null);
			if (symbolsGroup != null) {
				prefs.remove(propName);
				i++;
			} else {
				i = -1;
			}
		}
		if (!reset) { // now, if this is not a reset -  
			// save new symbol-props
			for (i = 0; i < symbolsTabPane.getTabCount(); i++) {
				String key = symbolsTabPane.getComponentAt(i).getName();
				prefs.put(Constants.PROPERTY_PREFIX_SYMBOLS + i, symbolMap.get(key));
			}
		}
	}
	
	private void updateCounter() {
		int charsLeft = maxLength - statusTextArea.getText().length();
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
		btnPost.setText("" + charsLeft);
		btnPost.setForeground(color);
		if (charsLeft >= 0) {
			btnPost.setEnabled(true);
		} else {
			btnPost.setEnabled(false);
		}
	}
	
	private void updateState() {
		updateCounter();
		updateImages();
	}
	
	private void postStatus() {
		try {
			if (!Validator.isNullOrBlank(statusTextArea.getText()) && statusTextArea.getText().length() <= maxLength) {
				if (BackEnd.updateStatus(statusTextArea.getText())) {
					maxLength = 140;
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
