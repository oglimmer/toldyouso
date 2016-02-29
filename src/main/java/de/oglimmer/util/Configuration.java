package de.oglimmer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.SneakyThrows;

public enum Configuration {
	INSTANCE;

	private Properties prop = new Properties();

	@SneakyThrows(value = IOException.class)
	private Configuration() {
		if (System.getProperty("cyc.properties") != null) {
			try (FileInputStream fis = new FileInputStream(System.getProperty("cyc.properties"))) {
				prop.load(fis);
				System.out.println("Successfully loaded cyc.properties from " + System.getProperty("cyc.properties"));
			}
		}
	}

	public String getDomain() {
		return prop.getProperty("toldyouso.domain", "http://anditoldyou.so");
	}

	public String getCouchDbHost() {
		return prop.getProperty("couchdb.host", "localhost");
	}

	public int getCouchDbPort() {
		return Integer.parseInt(prop.getProperty("couchdb.port", "5984"));
	}

	public String getCouchDbUser() {
		return prop.getProperty("couchdb.user", "");
	}

	public String getCouchDbPassword() {
		return prop.getProperty("couchdb.password", "");
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
		return prop.getProperty("smtp.from", "\"AndIToldYou.So robot\" <robot@anditoldyou.so>");
	}

}