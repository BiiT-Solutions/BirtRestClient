package com.biit.birt.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;

public class BirtConfigurationReader extends ConfigurationReader {
	protected static final String SETTINGS_FILE = "settings.conf";
	private static final String SYSTEM_VARIABLE = "BIRT_CONFIG";
	private static final String WEB_SERVICE_USER_ID = "web.service.rest.user";
	private static final String WEB_SERVICE_PASS_ID = "web.service.rest.password";

	private static BirtConfigurationReader instance;

	public BirtConfigurationReader() {
		BiitCommonLogger.debug(this.getClass(), "Loading default settings file...");
		// Load settings as file.
		String settingsFile = getJarFolder() + "/" + SETTINGS_FILE;
		BiitCommonLogger.debug(this.getClass(), "Searching for configuration file in '" + settingsFile + "'.");
		if (fileExists(settingsFile)) {
			addPropertiesSource(new PropertiesSourceFile(getJarFolder(), SETTINGS_FILE));
			BiitCommonLogger.debug(this.getClass(), "Found configuration file '" + settingsFile + "'!");
		}
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

	private URL getJarUrl() {
		URL url = this.getClass().getResource('/' + this.getClass().getName().replace('.', '/') + ".class");
		if (url == null) {
			return null;
		}
		// Remove class inside JAR file (i.e. jar:file:///outer.jar!/file.class)
		String packetPath = url.getPath();
		if (packetPath.contains("!")) {
			packetPath = packetPath.substring(0, packetPath.indexOf("!"));
		}

		packetPath = packetPath.replace("jar:", "");
		try {
			url = new URL(packetPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			BiitCommonLogger.errorMessageNotification(this.getClass(), e);
		}

		return url;
	}

	protected String getJarFolder() {
		URL settingsUrl = getJarUrl();
		return settingsUrl.getPath().substring(0, settingsUrl.getPath().lastIndexOf('/'));
	}

	protected String getJarName() {
		URL settingsUrl = getJarUrl();
		if (settingsUrl == null || !settingsUrl.getPath().contains(".jar")) {
			return null;
		}
		return settingsUrl.getPath().substring(settingsUrl.getPath().lastIndexOf('/') + 1,
				settingsUrl.getPath().length() - ".jar".length());
	}

	private boolean fileExists(String filePathString) {
		File f = new File(filePathString);
		return (f.exists() && !f.isDirectory());
	}
}
