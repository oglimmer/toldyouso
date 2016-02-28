package de.oglimmer.db.couchdb;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.IdleConnectionMonitor;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import de.oglimmer.util.Configuration;

public class CouchDbUtil {

	private CouchDbUtil() {
		// no code here
	}

	private static HttpClient httpClient;
	private static CouchDbInstance dbInstance;
	private static CouchDbConnector db;

	static {
		StdHttpClient.Builder builder = new StdHttpClient.Builder();
		String user = Configuration.INSTANCE.getCouchDbUser();
		if (user != null && !user.trim().isEmpty()) {
			builder.username(user);
		}
		String password = Configuration.INSTANCE.getCouchDbPassword();
		if (password != null && !password.trim().isEmpty()) {
			builder.password(password);
		}
		builder.host(Configuration.INSTANCE.getCouchDbHost());
		builder.port(Configuration.INSTANCE.getCouchDbPort());
		if (System.getProperty("http.proxyHost") != null) {
			builder.proxy(System.getProperty("http.proxyHost"));
		}
		if (System.getProperty("http.proxyPort") != null) {
			builder.proxyPort(Integer.parseInt(System.getProperty("http.proxyPort")));
		}
		httpClient = builder.build();
		dbInstance = new StdCouchDbInstance(httpClient);
		db = dbInstance.createConnector("toldyouso", false);
	}

	public static CouchDbConnector getDatabase() {
		return db;
	}

	public static void shutdown() {
		httpClient.shutdown();
		IdleConnectionMonitor.shutdown();
	}
}