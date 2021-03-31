package de.oglimmer.web.beans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}
		String password = value.toString();

		UIInput uiInputConfirmPassword = (UIInput) component.getAttributes().get("confirmPassword");
		String confirmPassword = uiInputConfirmPassword.getSubmittedValue().toString();

		// Let required="true" do its job.
		if (password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
			return;
		}

		if (!password.equals(confirmPassword)) {
			uiInputConfirmPassword.setValid(false);
			throw new ValidatorException(new FacesMessage("Password must match confirm password."));
		}

	}
}