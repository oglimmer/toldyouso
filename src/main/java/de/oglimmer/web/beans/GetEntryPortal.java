package de.oglimmer.web.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.ektorp.DocumentNotFoundException;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.model.SmartAssEntry;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
@Named
public class GetEntryPortal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SmartAssEntryDao dao;

	@Setter
	private String id;

	@Getter
	private SmartAssEntry smartAssEntry;

	public String getId() {
		if (id != null) {
			return id;
		} else {
			HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			return (String) req.getAttribute("id");
		}
	}

	public String load() {
		if (getId() != null && !getId().isEmpty()) {
			try {
				smartAssEntry = dao.get(getId());
				return "display";
			} catch (DocumentNotFoundException e) {
			}
		}
		return "notFound";
	}

}