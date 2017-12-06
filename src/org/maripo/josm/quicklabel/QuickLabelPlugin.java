package org.maripo.josm.quicklabel;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maripo.josm.quicklabel.QuickLabelDialog.QuickLabelDialogListener;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.Shortcut;


public class QuickLabelPlugin extends Plugin implements QuickLabelDialogListener  {
	
    public QuickLabelPlugin(PluginInformation info) {
        super(info);
        Action act = new Action();
        MainMenu.add(MainApplication.getMenu().viewMenu, act);
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
			QuickLabelDialog dialog = new QuickLabelDialog();
			dialog.setListener(QuickLabelPlugin.this);
			dialog.showDialog();
		}
    	
    }

	@Override
	public void onConfChange() {
		for (Layer layer: MainApplication.getLayerManager().getLayers()) {
			if (layer instanceof OsmDataLayer) {
				this.clearLayerCache((OsmDataLayer)layer);
			}
		}
	}

	private void clearLayerCache(OsmDataLayer layer) {
		for (OsmPrimitive primitive: layer.data.allNonDeletedPrimitives()) {
			primitive.clearCachedStyle();
		}
		
	}

	@Override
	public void onCancel() {
	}

}
