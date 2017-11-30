package org.maripo.josm.quicklabel;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.mappaint.styleelement.LabelCompositionStrategy;
import org.openstreetmap.josm.gui.mappaint.styleelement.TextLabel;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.GBC;

public class QuickLabelDialog extends ExtendedDialog {

	// Preferences keys for default conf
	private static final String PREF_KEY_DEFAULT_MAIN_LABEL_ORDER = "mappaint.nameOrder";
	private static final String PREF_KEY_DEFAULT_SUB_LABEL_ORDER = "mappaint.nameComplementOrder";

	// Preferences keys for custom conf
	private static final String PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER = "quicklabel.nameOrder";
	private static final String PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER = "quicklabel.nameComplementOrder";

	class Conf {

		private JTextArea textarea;
		private String prefKeyDefault;
		private String prefKeyQuicklabel;
		private String title;

		public Conf(String prefKeyDefault, String prefKeyQuicklabel, String title) {
			this.prefKeyDefault = prefKeyDefault;
			this.prefKeyQuicklabel = prefKeyQuicklabel;
			this.title = title;
		}

		public JPanel getPanel() {
			List<String> savedDefault = Config.getPref().getList(prefKeyQuicklabel,
					Config.getPref().getList(prefKeyDefault, new ArrayList<String>()));
			JPanel panel = new JPanel(new GridBagLayout());
			panel.add(new JLabel(title), GBC.eol());
			textarea = new JTextArea(6, 30);
			if (savedDefault != null && !savedDefault.isEmpty()) {
				textarea.setText(String.join("\n", savedDefault.toArray(new String[0])));
			}
			textarea.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER && ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0
							|| (e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0)) {
						applyAll();
					}
				}
			});
			panel.add(textarea, GBC.eol());
			return panel;
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

		public void focus() {
			textarea.requestFocus();
		}

	}

	Conf confSub = new Conf(PREF_KEY_DEFAULT_SUB_LABEL_ORDER, PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER, 
			tr("Sub"));

	Conf confMain = new Conf(PREF_KEY_DEFAULT_MAIN_LABEL_ORDER, PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER, 
			tr("Main"));

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
		panel.add(formContainer,GBC.eol());

		JButton applyButton = new JButton(tr("Apply"));
		applyButton.setToolTipText(tr("Apply change and show customized labels"));
		applyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				applyAll();
			}
		});

		JButton resetButton = new JButton(tr("Reset"));
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
	}
	
	private void reset () {
		reloadStrategy();
		dispose();
	}

	private void cancel() {
		dispose();
	}
}
