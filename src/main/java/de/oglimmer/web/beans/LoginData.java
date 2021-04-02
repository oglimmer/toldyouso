package de.oglimmer.web.beans;

import de.oglimmer.db.UserDao;
import de.oglimmer.model.User;
import de.oglimmer.util.BeanFetcher;
import de.oglimmer.util.Crypto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@SessionScoped
@Named
@ToString
public class LoginData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private boolean loggedIn;

    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String password;

    @Setter
    private User user;

    public LoginData() {
        System.out.println("Created LoginData");
    }

    public User getUser() {
        System.out.println("getUser: " + user);
        return user;
    }

	public String login() {
        UserDao userDao = BeanFetcher.getBean("userCouchDb");
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
