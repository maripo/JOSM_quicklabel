package org.maripo.josm.quicklabel.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern patternReplacement = Pattern.compile("\\{(.+?)\\}");
	class TagFormat {

		private String str;
		private boolean useReplacement = false;

		public TagFormat(String line) {
			this.str = line;
			if (patternReplacement.matcher(str).find()) {
				this.useReplacement  = true;
			}
		}
		boolean matched = false;
		public String generateString(OsmPrimitive primitive) {
			if (useReplacement) {
				matched = true;
				StringBuffer resultString = new StringBuffer();
				Matcher regexMatcher = patternReplacement.matcher(str);
				while (regexMatcher.find()) {
					regexMatcher.appendReplacement(resultString, getReplacement(regexMatcher, primitive));
				}
				regexMatcher.appendTail(resultString);
				if (matched) {
					return resultString.toString();
				} return null;
			} else if (primitive.hasKey(str)) {
				if (showTagKey) {
					return str + "=" + primitive.get(str);
				} else {
					return primitive.get(str);
				}
			}
			return null;
		}
		private String getReplacement(Matcher matcher, OsmPrimitive primitive) {
			String key = matcher.group(1);
			if (primitive.hasKey(key)) {
				return primitive.get(key);
			} else {
				matched = false;
				return "";
			}
		}
		
	}
	private List<TagFormat> mainFormatList = new ArrayList<TagFormat>();
	private List<TagFormat> subFormatList = new ArrayList<TagFormat>();
	
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
/*
 * 
	private List<TagFormat> mainFormatList = new ArrayList<TagFormat>();
	private List<TagFormat> subFormatList = new ArrayList<TagFormat>();
 */
	public void loadFromPreferences() {
		if (Config.getPref() == null) {
			this.mainFormatList = new ArrayList<TagFormat>();
			this.subFormatList = new ArrayList<TagFormat>();
		} else {
			List<String> mainTagLines = new ArrayList<>(
					Config.getPref().getList(PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER, new ArrayList<String>()));
			List<String> subTagLines = new ArrayList<>(Config.getPref().getList(PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER,
					new ArrayList<String>()));
			mainFormatList.clear();
			subFormatList.clear();
			for (String line: mainTagLines) {
				mainFormatList.add(new TagFormat(line));
			}
			for (String line: subTagLines) {
				subFormatList.add(new TagFormat(line));
				
			}
			this.showTagKey = Config.getPref().getBoolean(PREF_KEY_SHOW_TAG_KEY, false);
			this.showParentheses = Config.getPref().getBoolean(PREF_KEY_SHOW_PARENTHESES, false);
		}
	}
	

	private String getPrimitiveName(OsmPrimitive primitive) {
		StringBuilder name = new StringBuilder();
		if (!primitive.hasKeys())
			return null;
		for (TagFormat format: mainFormatList) {
			String val = format.generateString(primitive);
			if (val != null) {
				name.append(val);
				break;
			}
		}
		for (TagFormat format: subFormatList) {
			String comp = format.generateString(primitive);
			if (comp != null) {
				if (name.length() == 0 && !showParentheses) {
					// No main tag
					name.append(comp);
				} else {
					name.append(" (").append(comp).append(')');
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