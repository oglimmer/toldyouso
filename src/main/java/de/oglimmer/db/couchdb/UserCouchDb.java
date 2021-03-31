package de.oglimmer.db.couchdb;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import org.ektorp.support.CouchDbRepositorySupport;

import de.oglimmer.db.UserDao;
import de.oglimmer.model.User;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Named
@Slf4j
public class UserCouchDb extends CouchDbRepositorySupport<User> implements UserDao {

	public UserCouchDb() {
		super(User.class, CouchDbUtil.getDatabase());
		initStandardDesignDocument();
	}

	@Override
	public User getByEmail(String email) {
		if (email == null) {
			return null;
		}
		List<User> users = queryView("by_email", email.toLowerCase());
		if (users.size() > 1) {
			log.warn("Email = {} resulted in {} entries.", email, users.size());
		}
		if (users.size() == 1) {
			return users.get(0);
		} else {
			return null;
		}
	}

}