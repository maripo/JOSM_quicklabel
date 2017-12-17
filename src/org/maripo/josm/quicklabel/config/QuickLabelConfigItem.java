package org.maripo.josm.quicklabel.config;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.spi.preferences.Config;

public class QuickLabelConfigItem {

	private String prefKeyDefault;
	private String prefKey;
	private List<String> defaultNameTags;
	private List<String> valuesToApply;
	private List<String> prevValues;

	public QuickLabelConfigItem(String prefKeyDefault, String prefKey, String[] defaultNameTags) {
		this.prefKeyDefault = prefKeyDefault;
		this.prefKey = prefKey;
		this.defaultNameTags = Arrays.asList(defaultNameTags);
	}

	public List<String> getSavedValue() {
		return Config.getPref().getList(prefKey,
				Config.getPref().getList(prefKeyDefault, defaultNameTags));
	}

	public List<String> getDefaultValue() {
		return Config.getPref().getList(prefKeyDefault, defaultNameTags);
	}

	public void setValuesToApply(List<String> values) {
		this.valuesToApply = values;
		
	}
	// Stash default value and temporarily set textbox value
	void preApply () {
		this.prevValues = Config.getPref().getList(prefKeyDefault, null);
		Config.getPref().putList(prefKeyDefault, valuesToApply);
		Config.getPref().putList(prefKey, valuesToApply);
		
	}
	// Restore values
	void postApply () {
		Config.getPref().putList(prefKeyDefault, prevValues);
	}


}
