package de.oglimmer.web.beans;

import de.oglimmer.db.UserDao;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@RequestScoped
@Named
public class EmailValidator implements Validator {

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