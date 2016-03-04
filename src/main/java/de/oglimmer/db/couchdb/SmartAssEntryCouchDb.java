package de.oglimmer.db.couchdb;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.ektorp.support.CouchDbRepositorySupport;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.model.SmartAssEntry;

@ApplicationScoped
@Named
public class SmartAssEntryCouchDb extends CouchDbRepositorySupport<SmartAssEntry> implements SmartAssEntryDao {

	public SmartAssEntryCouchDb() {
		super(SmartAssEntry.class, CouchDbUtil.getDatabase());
		initStandardDesignDocument();
	}

}