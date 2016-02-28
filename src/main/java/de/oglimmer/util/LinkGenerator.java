package de.oglimmer.util;

public enum LinkGenerator {
	INSTANCE;

	public String make(String id) {
		return "http://localhost:8080/toldyouso/" + id;
	}

}
