package de.oglimmer.db;

import de.oglimmer.model.User;

public interface UserDao {

	User getByEmail(String email);

	void add(User user);

}
