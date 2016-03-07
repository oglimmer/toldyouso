package de.oglimmer.web.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import de.oglimmer.db.UserDao;

@RequestScoped
@Named
public class EmailValidator implements Validator, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserDao dao;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}
		String email = value.toString();

		// Let required="true" do its job.
		if (email.isEmpty()) {
			return;
		}

		if (dao.getByEmail(email) != null) {
			((UIInput) component).setValid(false);
			throw new ValidatorException(new FacesMessage("Email already registered."));
		}

	}
}