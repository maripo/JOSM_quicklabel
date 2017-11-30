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

	JTextArea textarea;
    private static final String PREF_KEY_QUICKLABEL_TAGS = "quicklabel.tags";
	private static final String PREF_KEY_DEFAULT = "mappaint.nameComplementOrder";
	public QuickLabelDialog () {
		super(Main.parent, "QuickLabel");
		List<String> savedDefault = Config.getPref().getList(PREF_KEY_QUICKLABEL_TAGS,
				Config.getPref().getList(PREF_KEY_DEFAULT, new ArrayList<String>()));
		this.setAlwaysOnTop(true);
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel(tr("<html>Tags to show next to objects.<br>You can specify multiple tags by writing into multiple lines<br>in the order of descending priorities.</html>")),
				GBC.eop());
		textarea = new JTextArea(6, 30);
		if (savedDefault!=null && !savedDefault.isEmpty()) {
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
				if (e.getKeyCode()==KeyEvent.VK_ENTER
						&& ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK)!=0 
						|| (e.getModifiersEx() & InputEvent.META_DOWN_MASK)!=0)) {
					apply();
				}
			}
		});
		panel.add(textarea, GBC.eop());
		JButton applyButton = new JButton(tr("Apply"));
		applyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});
		JButton cancelButton = new JButton(tr("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
				
			}
		});
		panel.add(applyButton, GBC.std());
		panel.add(cancelButton, GBC.eol());
		setContent(panel);
		textarea.requestFocus();
	}
	private void cancel () {
		dispose();
	}
	void apply () {
		List<String> values = new ArrayList<String>();
		
		String[] lines = textarea.getText().split("\n");
		for (String line: lines) {
			if (!line.isEmpty()) {
				values.add(line);
			}
		}
		if (values.isEmpty()) {
			return;
		}
		LabelCompositionStrategy strategy = TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY;
		
		List<String> prevValues = Config.getPref().getList(PREF_KEY_DEFAULT, new ArrayList<String>());
		Config.getPref().putList(PREF_KEY_DEFAULT, values);
		
		try {
			Method method = strategy.getClass().getMethod("initNameTagsFromPreferences");
			method.invoke(strategy);
			Config.getPref().putList(PREF_KEY_QUICKLABEL_TAGS, values);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} finally {
			Config.getPref().putList(PREF_KEY_DEFAULT, prevValues);
			dispose();
		}
	}
}
