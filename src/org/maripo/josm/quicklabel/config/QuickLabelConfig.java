package org.maripo.josm.quicklabel.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.maripo.josm.quicklabel.strategy.QuickLabelCompositionStrategy;
import org.openstreetmap.josm.gui.mappaint.styleelement.LabelCompositionStrategy;
import org.openstreetmap.josm.gui.mappaint.styleelement.TextLabel;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.LanguageInfo;

public class QuickLabelConfig {
	private QuickLabelCompositionStrategy strategy;
	
	private static QuickLabelConfig instance = null;

	// Preferences keys for default conf
	private static final String PREF_KEY_DEFAULT_MAIN_LABEL_ORDER = "mappaint.nameOrder";
	private static final String PREF_KEY_DEFAULT_SUB_LABEL_ORDER = "mappaint.nameComplementOrder";

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
    
	private QuickLabelConfigItem itemMainLabel = new QuickLabelConfigItem(PREF_KEY_DEFAULT_MAIN_LABEL_ORDER, 
			QuickLabelCompositionStrategy.PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER, DEFAULT_NAME_TAGS);
	private QuickLabelConfigItem itemSubLabel = new QuickLabelConfigItem(PREF_KEY_DEFAULT_SUB_LABEL_ORDER, 
			QuickLabelCompositionStrategy.PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER, DEFAULT_NAME_COMPLEMENT_TAGS);
	

	// For future use. Currently it's used to determine if the user need "Data->View" alert
	public static final String PREF_KEY_QUICKLABEL_APPLY_ON_START = "quicklabel.applyOnStart";
	public static final int APPLY_ON_START_NO = 0;
	public static final int APPLY_ON_START_YES = 1;
	
	public QuickLabelConfigItem getItemMainLabel() {
		return itemMainLabel;
	}
	public QuickLabelConfigItem getItemSubLabel() {
		return itemSubLabel;
	}
	
	private QuickLabelConfig () {
	}
	public void initCustomStrategy() {
		strategy = new QuickLabelCompositionStrategy();
		setStrategy(strategy);
	}
	/**
	 * Singleton
	 * @return
	 */
	public static QuickLabelConfig getInstance(){
		if (instance==null) {
			instance = new QuickLabelConfig();
		}
		return instance;
	}
	
	/**
	 * Apply values set by QuickLabelConfig#setValuesToApply()
	 */
	public void applyQuickLabelConf() {
		strategy.useQuickLabelComposer();
	}
	/**
	 * Reset. Apply default strategy.
	 */
	public void applyDefault() {
		strategy.useDefaultComposer();
	}
	/**
	 * Replace TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY with given object
	 * (by reflection)
	 * @param strategy
	 */
	private void setStrategy(LabelCompositionStrategy strategy) {
		System.out.println("QuickLabelConfig.setStrategy " + strategy);
		TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY.toString();
		try {

			Field field = TextLabel.class.getDeclaredField("AUTO_LABEL_COMPOSITION_STRATEGY");
			field.setAccessible(true);
			// Remove "final" modifier from AUTO_LABEL_COMPOSITION_STRATEGY
			Field modifierField = Field.class.getDeclaredField("modifiers");
			modifierField.setAccessible(true);
			modifierField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			// Replace!
			field.set(null, strategy);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println("TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY=" + TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY);
	}

	/**
	 * Use "quicklabel.applyOnStart" key to determine whether the user uses 
	 * new interface for the first time.
	 * @return
	 */
	public boolean isUserOfOlderVersion() {
		return (
				!Config.getPref().getList(QuickLabelCompositionStrategy.PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER).isEmpty()
				||
				!Config.getPref().getList(QuickLabelCompositionStrategy.PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER).isEmpty())
				&& Config.getPref().getInt(PREF_KEY_QUICKLABEL_APPLY_ON_START, -1)==-1;
	}
	public boolean isApplyOnStart() {
		return Config.getPref().getInt(PREF_KEY_QUICKLABEL_APPLY_ON_START, 0)==APPLY_ON_START_YES;
	}
	public void applySaved() {
		System.out.println("QuickLabelConfig.applySaved");
		itemMainLabel.saveValues(itemMainLabel.getSavedValue());
		itemSubLabel.saveValues(itemSubLabel.getSavedValue());
		strategy.useQuickLabelComposer();
		strategy.loadFromPreferences();
		
	}
}
