package com.biit.birt.configuration;

import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exception.PropertyNotFoundException;

public class BirtConfigurationReader extends JarConfigurationReader {
	private static final String SYSTEM_VARIABLE = "BIRT_CONFIG";
	private static final String WEB_SERVICE_USER_ID = "web.service.rest.user";
	private static final String WEB_SERVICE_PASS_ID = "web.service.rest.password";

	private static BirtConfigurationReader instance;

	public BirtConfigurationReader() {
		super(BirtConfigurationReader.class);

		// Load folder in system variables.
		addPropertiesSource(new SystemVariablePropertiesSourceFile(SYSTEM_VARIABLE, SETTINGS_FILE));
	}

	public static BirtConfigurationReader getInstance() {
		if (instance == null) {
			synchronized (BirtConfigurationReader.class) {
				if (instance == null) {
					instance = new BirtConfigurationReader();
				}
			}
		}
		return instance;
	}

	public String getPropertyValue(String propertyTag) {
		// Add property in property list to allow the use.
		addProperty(propertyTag, null);
		// Force to load all files to find the new property
		readConfigurations();
		try {
			String propertyValue = getProperty(propertyTag);
			try {
				return propertyValue;
			} catch (Exception e) {

			}
		} catch (PropertyNotFoundException e) {
			return null;
		}
		return null;
	}
	
	public String getWebServiceUser() {
		return getPropertyValue(WEB_SERVICE_USER_ID);
	}
	
	public String getWebServicePass() {
		return getPropertyValue(WEB_SERVICE_PASS_ID);
	}
}
