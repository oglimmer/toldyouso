package de.oglimmer.web.beans;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import de.oglimmer.db.UserDao;
import de.oglimmer.model.User;
import de.oglimmer.util.Crypto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SessionScoped
@Named
@ToString
public class LoginData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserDao userDao;

	@Getter
	@Setter
	private boolean loggedIn;

	@Getter
	@Setter
	private String email;
	@Getter
	@Setter
	private String password;
	@Getter
	@Setter
	private User user;

	public String login() {
		User user = userDao.getByEmail(email);
		if (user != null) {
			if (Crypto.INSTANCE.calcPasswordHash(password, user.getLoginPassword())) {
				loggedIn = true;
				this.user = user;
			}
		}
		return "index";
	}

	public void logout() {
		loggedIn = false;
		password = null;
		email = null;
		user = null;
	}
}
