package org.maripo.josm.quicklabel;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.Shortcut;


public class QuickLabelPlugin extends Plugin  {
	
    public QuickLabelPlugin(PluginInformation info) {
        super(info);
        Action act = new Action();
        MainMenu.add(MainApplication.getMenu().dataMenu, act);
    }
    
    class Action extends JosmAction {
    	public Action () {
    		// Default shortcut is Ctrl+Shift+L
    		super("QuickLabel", "quicklabel.png", tr("Show specified tag values next to objects"),
                    Shortcut.registerShortcut("quick_label", 
                    		"QuickLabel: " + tr("Show specified tag values next to objects"), 
                            KeyEvent.VK_L,
                            Shortcut.CTRL_SHIFT), true);
    	}
		@Override
		public void actionPerformed(ActionEvent e) {
			new QuickLabelDialog().showDialog();
		}
    	
    }

}
