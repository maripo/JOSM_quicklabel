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
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.Shortcut;

public class QuickLabelPlugin extends Plugin implements QuickLabelDialogListener {

	public QuickLabelPlugin(PluginInformation info) {
		super(info);
		if (isUserOfOlderVersion()) {
			MainMenu.add(MainApplication.getMenu().dataMenu, new Action(true));
		}
		MainMenu.add(MainApplication.getMenu().viewMenu, new Action());
	}

	private boolean isUserOfOlderVersion() {
		return (
				!Config.getPref().getList(QuickLabelDialog.PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER).isEmpty()
				||
				!Config.getPref().getList(QuickLabelDialog.PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER).isEmpty())
				&& Config.getPref().getInt(QuickLabelDialog.PREF_KEY_QUICKLABEL_APPLY_ON_START, -1)==-1;
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

}
