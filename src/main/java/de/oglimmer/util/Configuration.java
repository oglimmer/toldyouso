package de.oglimmer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.SneakyThrows;

public enum Configuration {
	INSTANCE;

	private Properties prop = new Properties();

	@SneakyThrows(value = IOException.class)
	Configuration() {
		if (System.getProperty("toldyouso.properties") != null) {
			try (FileInputStream fis = new FileInputStream(System.getProperty("toldyouso.properties"))) {
				prop.load(fis);
				System.out.println("Successfully loaded toldyouso.properties from " + System.getProperty("toldyouso.properties"));
			}
		}
	}

	public String getDomain() {
		return prop.getProperty("toldyouso.domain", "https://toldyouso.oglimmer.de");
	}

	public String getCouchDbHost() {
		return prop.getProperty("couchdb.host", "localhost");
	}

	public int getCouchDbPort() {
		return Integer.parseInt(prop.getProperty("couchdb.port", "5984"));
	}

	public String getCouchDbUser() {
		return prop.getProperty("couchdb.user", "admin");
	}

	public String getCouchDbPassword() {
		return prop.getProperty("couchdb.password", "password");
	}

	public String getSmtpHost() {
		return prop.getProperty("smtp.host", "localhost");
	}

	public int getSmtpPort() {
		return Integer.parseInt(prop.getProperty("smtp.port", "25"));
	}

	public boolean getSmtpSSL() {
		return Boolean.parseBoolean(prop.getProperty("smtp.ssl", "false"));
	}

	public String getSmtpUser() {
		return prop.getProperty("smtp.user", "");
	}

	public String getSmtpPassword() {
		return prop.getProperty("smtp.password", "");
	}

	public String getSmtpFrom() {
		return prop.getProperty("smtp.from", "\"toldyouso robot\" <robot@toldyouso.oglimmer.de>");
	}

}