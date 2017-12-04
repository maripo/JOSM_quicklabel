package org.maripo.josm.quicklabel;

import static org.openstreetmap.josm.tools.I18n.tr;
import static org.openstreetmap.josm.tools.I18n.trc;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.mappaint.styleelement.LabelCompositionStrategy;
import org.openstreetmap.josm.gui.mappaint.styleelement.TextLabel;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.LanguageInfo;

/**
 * QuickLabel configuration dialog
 * @author maripo
 *
 */
public class QuickLabelDialog extends ExtendedDialog {
	
	interface QuickLabelDialogListener {
		public void onConfChange();
		public void onCancel();
	}

	// Preferences keys for default conf
	private static final String PREF_KEY_DEFAULT_MAIN_LABEL_ORDER = "mappaint.nameOrder";
	private static final String PREF_KEY_DEFAULT_SUB_LABEL_ORDER = "mappaint.nameComplementOrder";

	// Preferences keys for custom conf
	private static final String PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER = "quicklabel.nameOrder";
	private static final String PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER = "quicklabel.nameComplementOrder";

    private static final String[] DEFAULT_NAME_TAGS = {
        "name:" + LanguageInfo.getJOSMLocaleCode(),
        "name",
        "int_name",
        "distance",
        "ref",
        "operator",
        "brand",
        "addr:housenumber"
    };

    private static final String[] DEFAULT_NAME_COMPLEMENT_TAGS = {
        "capacity"
    };
    
    private QuickLabelDialogListener listener = null;
	public void setListener(QuickLabelDialogListener listener) {
		this.listener = listener;
	}

	class Conf {

		private JTextArea textarea;
		private String prefKeyDefault;
		private String prefKeyQuicklabel;
		private String title;
		private List<String> defautTags;

		public Conf(String prefKeyDefault, String prefKeyQuicklabel, String[] defaultTags, String title) {
			this.prefKeyDefault = prefKeyDefault;
			this.prefKeyQuicklabel = prefKeyQuicklabel;
			this.defautTags = Arrays.asList(defaultTags);
			this.title = title;
		}

		/**
		 * Initialize UI widgets
		 * @return Panel containing all widgets
		 */
		public JPanel getPanel() {
			List<String> savedDefault = Config.getPref().getList(prefKeyQuicklabel,
					Config.getPref().getList(prefKeyDefault, defautTags));
			final JPanel panel = new JPanel(new GridBagLayout());
			
			panel.add(new JLabel(title), GBC.std());
			final JButton restoreButton = new JButton(tr("Restore default"));
			
			restoreButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					loadDefault();
				}
			});
			panel.add(restoreButton, GBC.eol());
			
			textarea = new JTextArea(6, 15);
			if (savedDefault != null && !savedDefault.isEmpty()) {
				textarea.setText(String.join("\n", savedDefault.toArray(new String[0])));
			}
			textarea.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					// Nothing to do
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// Nothing to do
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER && ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0
							|| (e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0)) {
						applyAll();
					} else if (e.getKeyCode()==KeyEvent.VK_TAB) {
						switchFocus();
					}
				}
			});
			// Suppress input with "TAB" key strokes
			textarea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "none");

			final JScrollPane scrolll = new JScrollPane(textarea);
			panel.add(scrolll, GBC.eol().fill());
			return panel;
		}

		JTextArea tabNextTextarea = null;
		private void switchFocus() {
			if (tabNextTextarea!=null) {
				tabNextTextarea.requestFocus();
			}
		}
		
		protected void loadDefault() {
			List<String> defaultTags = Config.getPref().getList(prefKeyDefault, defautTags);
			if (defaultTags!=null) {
				textarea.setText(String.join("\n", defaultTags.toArray(new String[0])));
			}
		}
		
		List<String> prevValues = null;
		
		// Replace with new value temporarily
		public void preApply() {
			List<String> values = new ArrayList<String>();

			String[] lines = textarea.getText().split("\n");
			for (String line : lines) {
				if (!line.isEmpty()) {
					values.add(line);
				}
			}
			if (values.isEmpty()) {
				return;
			}
			
			prevValues = Config.getPref().getList(prefKeyDefault, null);
			Config.getPref().putList(prefKeyDefault, values);
			Config.getPref().putList(prefKeyQuicklabel, values);
		}
		/* Restore default key */
		public void postApply () {
			Config.getPref().putList(prefKeyDefault, prevValues);
			
		}

		/**
		 * Set focus to the textarea
		 */
		public void focus() {
			textarea.requestFocus();
		}

	}


	Conf confMain = new Conf(PREF_KEY_DEFAULT_MAIN_LABEL_ORDER, PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER, 
			DEFAULT_NAME_TAGS, tr("Main"));
	Conf confSub = new Conf(PREF_KEY_DEFAULT_SUB_LABEL_ORDER, PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER, 
			DEFAULT_NAME_COMPLEMENT_TAGS, tr("Sub"));

	public QuickLabelDialog() {
		super(Main.parent, "QuickLabel");
		this.setAlwaysOnTop(true);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel(
						tr("<html>Tags to show next to objects.<br>You can specify multiple tags by writing into multiple lines<br>in the order of descending priorities.</html>")),
				GBC.eop());
		panel.add(new JLabel(tr("<html>Label format will be \"<b><i>Main</i>(<i>Sub</i>)</b>\" or \"<b><i>Sub</i></b>\" if main tag is empty.</html>")),GBC.eop());

		JPanel formContainer = new JPanel(new GridBagLayout());
		formContainer.add(confMain.getPanel(), GBC.std().insets(5));
		formContainer.add(confSub.getPanel(), GBC.eol().insets(5));
		confMain.tabNextTextarea = confSub.textarea;
		confSub.tabNextTextarea = confMain.textarea;
		panel.add(formContainer,GBC.eol());

		JButton applyButton = new JButton(tr("Apply"));
		applyButton.setToolTipText(tr("Apply change and show customized labels"));
		applyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				applyAll();
			}
		});
		JButton resetButton = new JButton(trc("quicklabel", "Reset"));
		resetButton.setToolTipText(tr("Reset QuickLabel config and show default labels"));
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		
		JButton cancelButton = new JButton(tr("Cancel"));
		cancelButton.setToolTipText(tr("Close this window without saving"));
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();

			}
		});

		applyButton.setIcon(ImageProvider.get("ok"));
		resetButton.setIcon(ImageProvider.get("undo"));
		cancelButton.setIcon(ImageProvider.get("cancel"));
		panel.add(applyButton, GBC.std());
		panel.add(resetButton, GBC.std());
		panel.add(cancelButton, GBC.eol());
		setContent(panel);
		confMain.focus();
	}
	
	// Reload TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY by reflection
	private void reloadStrategy () {
		LabelCompositionStrategy strategy = TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY;
		try {
			Method method = strategy.getClass().getMethod("initNameTagsFromPreferences");
			method.invoke(strategy);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	private void applyAll() {
		confMain.preApply();
		confSub.preApply();
		reloadStrategy();
		confMain.postApply();
		confSub.postApply();
		dispose();
		if (listener!=null) {
			listener.onConfChange();
		}
	}
	
	private void reset () {
		reloadStrategy();
		dispose();
		if (listener!=null) {
			listener.onConfChange();
		}
	}

	private void cancel() {
		dispose();
		if (listener!=null) {
			listener.onCancel();
		}
	}
}
