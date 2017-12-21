package org.maripo.josm.quicklabel.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.mappaint.styleelement.LabelCompositionStrategy.DeriveLabelFromNameTagsCompositionStrategy;
import org.openstreetmap.josm.spi.preferences.Config;

/**
 * Use this custom label composition strategy instead of
 * DeriveLabelFromNameTagsCompositionStrategy
 * 
 * @author maripo
 *
 */

public class QuickLabelCompositionStrategy extends  DeriveLabelFromNameTagsCompositionStrategy{

	private List<String> nameTags = new ArrayList<>();
	private List<String> nameComplementTags = new ArrayList<>();
	
	// Preferences keys for custom conf
	public static final String PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER = "quicklabel.nameOrder";
	public static final String PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER = "quicklabel.nameComplementOrder";

	public static final String PREF_KEY_SHOW_TAG_KEY = "quicklabel.conf.showTagKey";
	public static final String PREF_KEY_SHOW_PARENTHESES = "quicklabel.conf.showParentheses";

	LabelComposer labelComposer;
	LabelComposer quickLabelComposer;
	LabelComposer defaultComposer;
	private boolean showParentheses = false;
	private boolean showTagKey = false;
	/**
     * <p>Creates the strategy and initializes its name tags from the preferences.</p>
     */
    public QuickLabelCompositionStrategy() {
    	loadFromPreferences();

    	quickLabelComposer = new QuickLabelComposer();
    	defaultComposer = new DefaultLabelComposer();
    	labelComposer = defaultComposer;
    }

	public void loadFromPreferences() {
		if (Config.getPref() == null) {
			this.nameTags = new ArrayList<>();
			this.nameComplementTags = new ArrayList<>();
		} else {
			this.nameTags = new ArrayList<>(
					Config.getPref().getList(PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER, new ArrayList<String>()));
			this.nameComplementTags = new ArrayList<>(Config.getPref().getList(PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER,
					new ArrayList<String>()));
			this.showTagKey = Config.getPref().getBoolean(PREF_KEY_SHOW_TAG_KEY, false);
			this.showParentheses = Config.getPref().getBoolean(PREF_KEY_SHOW_PARENTHESES, false);
		}
	}
	
	private String getTagString (String key, String value) {
		if (showTagKey) {
			return key + "=" + value;
		} else {
			return value;
		}
	}

	private String getPrimitiveName(OsmPrimitive n) {
		StringBuilder name = new StringBuilder();
		if (!n.hasKeys())
			return null;
		for (String rn : nameTags) {
			String val = n.get(rn);
			if (val != null) {
				name.append(getTagString(rn, val));
				break;
			}
		}
		for (String rn : nameComplementTags) {
			String comp = n.get(rn);
			if (comp != null) {
				if (name.length() == 0 && !showParentheses) {
					// No main tag
					name.append(getTagString(rn, comp));
				} else {
					name.append(" (").append(getTagString(rn, comp)).append(')');
				}
				break;
			}
		}
		return name.toString();
	}

	@Override
	public String compose(OsmPrimitive primitive) {
		return labelComposer.composeLabel(primitive);
	}
	public String composeDefault (OsmPrimitive primitive) {
		return super.compose(primitive);
	}

	@Override
	public String toString() {
		return '{' + getClass().getSimpleName() + '}';
	}
	
	interface LabelComposer {
		String composeLabel (OsmPrimitive primitive);
	}
	class DefaultLabelComposer implements LabelComposer {

		@Override
		public String composeLabel(OsmPrimitive primitive) {
			return composeDefault(primitive);
		}
		
	}
	class QuickLabelComposer implements LabelComposer {

		@Override
		public String composeLabel(OsmPrimitive primitive) {
			if (primitive == null)
				return null;
			return getPrimitiveName(primitive);
		}
		
	}

	public void useQuickLabelComposer() {
		labelComposer = quickLabelComposer;
		
	}

	public void useDefaultComposer() {
		labelComposer = defaultComposer;
		
	}
}