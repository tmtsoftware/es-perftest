package org.tmt.csw.eventservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads the configuration file in memory
 * 
 * @author amit_harsola
 *
 */
public class ConfigPropertyLoader {
	private static Properties prop = new Properties();
	
	static {
		InputStream inStream = BrokerUtility.class.getClassLoader().getResourceAsStream("config/configuration.properties");
		try {
			prop.load(inStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Properties getProperties() {
		return prop;
	}
}
