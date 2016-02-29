package de.oglimmer.util;

public enum LinkGenerator {
	INSTANCE;

	public String make(String id) {
		return Configuration.INSTANCE.getDomain() + "/" + id;
	}

}
