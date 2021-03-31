package de.oglimmer.web.beans;

import java.io.Serializable;
import java.util.Date;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.RandomUtils;

import de.oglimmer.db.UserDao;
import de.oglimmer.model.User;
import de.oglimmer.util.Crypto;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
@Named
public class Register implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserDao dao;
	@Inject
	private LoginData loginData;

	@Getter
	@Setter
	private String message;
	@Getter
	private User user = new User();

	public String save() {
		user.setCreationDate(new Date());
		user.setInitVector(RandomUtils.nextBytes(16));
		user.setFactPassword(Crypto.INSTANCE.createFactPassword(user.getLoginPassword(), user.getInitVector()));
		loginData.setPassword(user.getLoginPassword());
		user.setLoginPassword(Crypto.INSTANCE.calcPasswordHash(user.getLoginPassword()));
		dao.add(user);

		loginData.setLoggedIn(true);
		loginData.setUser(user);
		loginData.setEmail(user.getEmail());

		return "index";
	}

}