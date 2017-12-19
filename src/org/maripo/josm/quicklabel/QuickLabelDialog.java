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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.maripo.josm.quicklabel.config.QuickLabelConfig;
import org.maripo.josm.quicklabel.config.QuickLabelConfigItem;
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
    
    private QuickLabelDialogListener listener = null;
	public void setListener(QuickLabelDialogListener listener) {
		this.listener = listener;
	}

	class Conf {

		private JTextArea textarea;
		private String title;
		private QuickLabelConfigItem conf;

		public Conf(QuickLabelConfigItem conf, String title) {
			this.conf = conf;
			this.title = title;
		}

		/**
		 * Initialize UI widgets
		 * @return Panel containing all widgets
		 */
		public JPanel getPanel() {
			List<String> savedTags = conf.getSavedValue();
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
			if (savedTags != null && !savedTags.isEmpty()) {
				textarea.setText(String.join("\n", savedTags.toArray(new String[0])));
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
		
		/*
		 * Reset and load default keys
		 */
		protected void loadDefault() {
			List<String> defaultTags = conf.getDefaultValue();
			if (defaultTags!=null) {
				System.out.println("Default tags=" + defaultTags);
				textarea.setText(String.join("\n", defaultTags.toArray(new String[0])));
			} else {
				System.out.println("Default tags null");
			}
		}

		/**
		 * Set focus to the textarea
		 */
		public void focus() {
			textarea.requestFocus();
		}

		/**
		 * Prepare for applying input values
		 */
		public void saveConf() {
			List<String> values = new ArrayList<String>();

			String[] lines = textarea.getText().split("\n");
			for (String line : lines) {
				if (!line.isEmpty()) {
					values.add(line);
				}
			}
			conf.saveValues(values);
			
		}

	}

	
	Conf confMain = new Conf(QuickLabelConfig.getInstance().getItemMainLabel(), tr("Main"));
	Conf confSub = new Conf(QuickLabelConfig.getInstance().getItemSubLabel(), tr("Sub"));
	JCheckBox applyOnStartCheckbox;
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
		
		
		JPanel applyOnStartContainer = new JPanel(new GridBagLayout());
		applyOnStartCheckbox = new JCheckBox();
		applyOnStartContainer.add(applyOnStartCheckbox, GBC.std());
		applyOnStartContainer.add(new JLabel(tr("Apply on startup")));
		applyOnStartCheckbox.setSelected(QuickLabelConfig.getInstance().isApplyOnStart());
		panel.add(applyOnStartContainer, GBC.eol());

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
	

	private void applyAll() {
		confMain.saveConf();
		confSub.saveConf();
		QuickLabelConfig.getInstance().applySaved();
		saveApplyOnStartConf();
		dispose();
		if (listener!=null) {
			listener.onConfChange();
		}
	}


	private void saveApplyOnStartConf() {
		int val = applyOnStartCheckbox.isSelected()?
				QuickLabelConfig.APPLY_ON_START_YES:QuickLabelConfig.APPLY_ON_START_NO;
		System.out.println("QuickLabelDialog.saveApplyOnStartConf val=" + val);
		Config.getPref().putInt(QuickLabelConfig.PREF_KEY_QUICKLABEL_APPLY_ON_START, val);
	}
	
	private void reset () {
		QuickLabelConfig.getInstance().applyDefault();
		saveApplyOnStartConf();
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
