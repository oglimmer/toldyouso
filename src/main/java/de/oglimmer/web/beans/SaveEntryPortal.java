package de.oglimmer.web.beans;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.model.SmartAssEntry;
import de.oglimmer.util.Crypto;
import de.oglimmer.util.EmailService;
import de.oglimmer.util.LinkGenerator;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
@Named
public class SaveEntryPortal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SmartAssEntryDao dao;

	@Inject
	private LoginData loginData;

	@Getter
	private String message;
	@Getter
	@Setter
	private boolean crypted;

	@Getter
	/* @Inject works here, but it just injects a proxy object which is not Serializeable by ektorp */
	private SmartAssEntry smartAssEntry = new SmartAssEntry();

	public String save() {

		smartAssEntry.setCreationDate(new Date());
		if (loginData.isLoggedIn()) {
			smartAssEntry.setEmail(loginData.getEmail());
			smartAssEntry.setCreatorId(loginData.getUser().getId());
		}

		String factInEmail;
		if (crypted) {
			String cryptedFact = Crypto.INSTANCE.cryptFact(smartAssEntry.getFact(), loginData.getPassword(),
					loginData.getUser().getFactPassword(), loginData.getUser().getInitVector());
			smartAssEntry.setFact(cryptedFact);
			smartAssEntry.setEncrypted(true);
			factInEmail = "***ENCRYPTED***";
		} else {
			factInEmail = smartAssEntry.getFact();
		}

		dao.add(smartAssEntry);

		String link = "<a href='" + LinkGenerator.INSTANCE.make(smartAssEntry.getId()) + "'>"
				+ LinkGenerator.INSTANCE.make(smartAssEntry.getId()) + "</a>";

		if (smartAssEntry.getEmail() != null && !smartAssEntry.getEmail().isEmpty()) {
			EmailService.INSTANCE.createAndSendMailFile(smartAssEntry.getEmail(), "anditoldyou.so record",
					"/creationEmail.txt", factInEmail, LinkGenerator.INSTANCE.make(smartAssEntry.getId()));
			message = "Wisdom saved. We've sent you an email. Here's the link as well: " + link;
		} else {
			message = "Wisdom saved. Here's the link for future references: " + link + ". Keep it safe!";
		}

		smartAssEntry = new SmartAssEntry();
		return "index";
	}

}