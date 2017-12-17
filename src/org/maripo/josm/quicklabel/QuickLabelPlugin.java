package org.maripo.josm.quicklabel;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.maripo.josm.quicklabel.QuickLabelDialog.QuickLabelDialogListener;
import org.maripo.josm.quicklabel.config.QuickLabelConfig;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.Shortcut;

public class QuickLabelPlugin extends Plugin implements QuickLabelDialogListener, LayerChangeListener {

	public QuickLabelPlugin(PluginInformation info) {
		super(info);
		QuickLabelConfig conf = QuickLabelConfig.getInstance();
		if (conf.isUserOfOlderVersion()) {
			MainMenu.add(MainApplication.getMenu().dataMenu, new Action(true));
		}
		MainMenu.add(MainApplication.getMenu().viewMenu, new Action());
		if (conf.isApplyOnStart()) {
			conf.applySaved();
			MainApplication.getLayerManager().addLayerChangeListener(this);
			
		}
	}

	class Action extends JosmAction {
		public Action() {
			// Default shortcut is Ctrl+Shift+L
			super("QuickLabel", "quicklabel.png", tr("Show specified tag values next to objects"),
					Shortcut.registerShortcut("quick_label",
							"QuickLabel: " + tr("Show specified tag values next to objects"), KeyEvent.VK_L,
							Shortcut.CTRL_SHIFT), true);
		}
		/* Constructor for older position (in "Data" section) */
		public Action(boolean b) {
			super("QuickLabel (Moved to \"View\")", "quicklabel.png", tr("Show specified tag values next to objects"),
					null, true);
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
		for (Layer layer : MainApplication.getLayerManager().getLayers()) {
			if (layer instanceof OsmDataLayer) {
				this.clearLayerCache((OsmDataLayer) layer);
			}
		}
	}

	private void clearLayerCache(OsmDataLayer layer) {
		for (OsmPrimitive primitive : layer.data.allNonDeletedPrimitives()) {
			primitive.clearCachedStyle();
		}

	}

	@Override
	public void onCancel() {
	}

	@Override
	public void layerAdded(LayerAddEvent e) {
		if (e.getAddedLayer() instanceof OsmDataLayer) {
	        new Notification("Your QuickLabel config is applied.")
	        .setIcon(JOptionPane.INFORMATION_MESSAGE)
	        .setDuration(Notification.TIME_DEFAULT)
	        .show();
	        MainApplication.getLayerManager().removeLayerChangeListener(this);
	        
		}
		
	}

	@Override
	public void layerRemoving(LayerRemoveEvent e) {
	}

	@Override
	public void layerOrderChanged(LayerOrderChangeEvent e) {
	}

}
