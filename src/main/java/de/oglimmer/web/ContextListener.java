package de.oglimmer.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import de.oglimmer.db.couchdb.CouchDbUtil;
import de.oglimmer.util.EmailService;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		EmailService.INSTANCE.shutdown();
		CouchDbUtil.shutdown();
	}

}
