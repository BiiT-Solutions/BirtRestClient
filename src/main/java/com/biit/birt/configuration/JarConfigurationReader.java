package com.biit.birt.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;

public class JarConfigurationReader extends ConfigurationReader {
	protected static final String SETTINGS_FILE = "settings.conf";
	private Class<?> birtConfigurationClass;

	/**
	 * Load settings from defaults folders, conf with plugin jar or system variables.
	 * 
	 * @param birtConfigurationClass
	 */
	public JarConfigurationReader(Class<?> birtConfigurationClass) {
		this.birtConfigurationClass = birtConfigurationClass;

		BiitCommonLogger.debug(this.getClass(), "Loading default settings file...");
		addPropertiesSource(new PropertiesSourceFile(SETTINGS_FILE));

		String settingsFile = getJarName();
		BiitCommonLogger.debug(this.getClass(), "Loading settings file " + settingsFile);
		// Load settings as resource.
		if (settingsFile != null) {
			// using same name as jar file.
			if (resourceExist(settingsFile + ".conf")) {
				addPropertiesSource(new PropertiesSourceFile(settingsFile + ".conf"));
				BiitCommonLogger.debug(this.getClass(), "Plugin using settings in resource folder '" + settingsFile
						+ ".conf" + "'.");
			}
		}
		// Load settings as file.
		settingsFile = getJarFolder() + "/" + getJarName() + ".conf";
		BiitCommonLogger.debug(this.getClass(), "Searching for configuration file in '" + settingsFile + "'.");
		if (fileExists(settingsFile)) {
			addPropertiesSource(new PropertiesSourceFile(getJarFolder(), getJarName() + ".conf"));
			BiitCommonLogger.debug(this.getClass(), "Found configuration file '" + settingsFile + "'!");
		}

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

	private boolean resourceExist(String resourceName) {
		URL url;
		if (birtConfigurationClass != null) {
			url = birtConfigurationClass.getClassLoader().getResource(resourceName);
		} else {
			url = JarConfigurationReader.class.getClassLoader().getResource(resourceName);
		}
		return url != null;
	}

}
