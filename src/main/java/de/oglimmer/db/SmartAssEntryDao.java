package de.oglimmer.db;

import de.oglimmer.model.SmartAssEntry;

public interface SmartAssEntryDao {

	void add(SmartAssEntry smartAssEntry);

	SmartAssEntry get(String id);

	void update(SmartAssEntry smartAssEntry);

}
