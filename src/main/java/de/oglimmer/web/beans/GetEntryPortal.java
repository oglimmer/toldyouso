package de.oglimmer.web.beans;

import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.ektorp.DocumentNotFoundException;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.model.SmartAssEntry;
import de.oglimmer.util.Crypto;
import lombok.Setter;

@RequestScoped
@Named
public class GetEntryPortal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private LoginData loginData;

	@Inject
	private SmartAssEntryDao dao;

	@Setter
	private String id;

	private SmartAssEntry smartAssEntry;

	public boolean isShowbutton() {
		if (loginData.getUser() == null) {
			return false;
		}
		if (smartAssEntry == null) {
			return true;
		}
		return loginData.getUser().getId().equals(smartAssEntry.getCreatorId());
	}

	public SmartAssEntry getSmartAssEntry() {
		if (smartAssEntry == null && getId() != null) {
			load();
		}
		return smartAssEntry;
	}

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

				if (smartAssEntry.isEncrypted()) {
					if (loginData.isLoggedIn() && loginData.getEmail().equals(smartAssEntry.getEmail())) {
						smartAssEntry
								.setFact(Crypto.INSTANCE.decryptFact(smartAssEntry.getFact(), loginData.getPassword(),
										loginData.getUser().getFactPassword(), loginData.getUser().getInitVector()));
					}
				}

				return "display";
			} catch (DocumentNotFoundException e) {
			}
		}
		return "notFound";
	}

	public String decryptFact() {
		load();
		if (smartAssEntry.getCreatorId().equals(loginData.getUser().getId())) {
			smartAssEntry.setEncrypted(false);
			dao.update(smartAssEntry);
		}
		return "display?faces-redirect=true&id=" + id;
	}

	public String cryptFact() {
		load();
		if (smartAssEntry.getCreatorId().equals(loginData.getUser().getId())) {
			String uncryptedFact = smartAssEntry.getFact();
			String cryptedFact = Crypto.INSTANCE.cryptFact(uncryptedFact, loginData.getPassword(),
					loginData.getUser().getFactPassword(), loginData.getUser().getInitVector());
			smartAssEntry.setFact(cryptedFact);
			smartAssEntry.setEncrypted(true);
			dao.update(smartAssEntry);
			smartAssEntry.setFact(uncryptedFact);
		}
		return "display?faces-redirect=true&id=" + id;
	}

}