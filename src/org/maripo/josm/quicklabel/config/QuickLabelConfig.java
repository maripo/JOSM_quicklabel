package org.maripo.josm.quicklabel.config;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.maripo.josm.quicklabel.strategy.QuickLabelCompositionStrategy;
import org.openstreetmap.josm.gui.mappaint.styleelement.LabelCompositionStrategy;
import org.openstreetmap.josm.gui.mappaint.styleelement.TextLabel;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.LanguageInfo;

public class QuickLabelConfig {
	private static QuickLabelConfig instance = null;
	

	// Preferences keys for default conf
	private static final String PREF_KEY_DEFAULT_MAIN_LABEL_ORDER = "mappaint.nameOrder";
	private static final String PREF_KEY_DEFAULT_SUB_LABEL_ORDER = "mappaint.nameComplementOrder";

	// Preferences keys for custom conf
	public static final String PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER = "quicklabel.nameOrder";
	public static final String PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER = "quicklabel.nameComplementOrder";

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
			PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER, DEFAULT_NAME_TAGS);
	private QuickLabelConfigItem itemSubLabel = new QuickLabelConfigItem(PREF_KEY_DEFAULT_SUB_LABEL_ORDER, 
			PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER, DEFAULT_NAME_COMPLEMENT_TAGS);
	

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
	public static QuickLabelConfig getInstance(){
		if (instance==null) {
			instance = new QuickLabelConfig();
		}
		return instance;
	}
	// Reload TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY by reflection
	private void reloadStrategy () {
		LabelCompositionStrategy strategy = TextLabel.AUTO_LABEL_COMPOSITION_STRATEGY;
		String methodName = (strategy instanceof QuickLabelCompositionStrategy)?"initNameTagsFromPreferences":"initNameTagsFromPreferences2";
		try {
			Method method = strategy.getClass().getMethod(methodName);
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
	
	/**
	 * Apply values set by QuickLabelConfig#setValuesToApply()
	 */
	public void apply() {
		// Stash default values
		itemMainLabel.preApply();
		itemSubLabel.preApply();
		// Apply
		reloadStrategy();
		// Restore pref values
		itemMainLabel.postApply();
		itemSubLabel.postApply();
	}
	
	/**
	 * Reset. Apply default label config.
	 */
	public void applyDefault() {
		reloadStrategy();
	}

	/**
	 * Use "quicklabel.applyOnStart" key to determine whether the user uses 
	 * new interface for the first time.
	 * @return
	 */
	public boolean isUserOfOlderVersion() {
		return (
				!Config.getPref().getList(PREF_KEY_QUICKLABEL_MAIN_LABEL_ORDER).isEmpty()
				||
				!Config.getPref().getList(PREF_KEY_QUICKLABEL_SUB_LABEL_ORDER).isEmpty())
				&& Config.getPref().getInt(PREF_KEY_QUICKLABEL_APPLY_ON_START, -1)==-1;
	}
	public boolean isApplyOnStart() {
		return Config.getPref().getInt(PREF_KEY_QUICKLABEL_APPLY_ON_START, 0)==APPLY_ON_START_YES;
	}
	public void applySaved() {
		System.out.println("QuickLabelConfig.applySaved");
		itemMainLabel.setValuesToApply(itemMainLabel.getSavedValue());
		itemSubLabel.setValuesToApply(itemSubLabel.getSavedValue());
		apply();
		
	}
}
