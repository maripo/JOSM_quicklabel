package org.maripo.josm.quicklabel.config;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.spi.preferences.Config;

public class QuickLabelConfigItem {

	private String prefKeyDefault;
	private String prefKey;
	private List<String> defaultNameTags;

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

	public void saveValues (List<String> values) {
		Config.getPref().putList(prefKey, values);
		
	}

}
