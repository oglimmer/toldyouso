package de.oglimmer.db.couchdb;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.model.SmartAssEntry;

public class SmartAssEntryCouchDb extends CouchDbRepositorySupport<SmartAssEntry> implements SmartAssEntryDao {

	public SmartAssEntryCouchDb(CouchDbConnector db) {
		super(SmartAssEntry.class, db);
		initStandardDesignDocument();
	}

}