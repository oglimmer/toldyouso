package de.oglimmer.web;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.db.couchdb.CouchDbUtil;
import de.oglimmer.db.couchdb.SmartAssEntryCouchDb;
import de.oglimmer.model.SmartAssEntry;
import de.oglimmer.util.EmailService;
import lombok.Getter;

@RequestScoped
@Named
public class SaveEntryPortal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	private String message;

	@Getter
	/* @Inject works here, but it just injects a proxy object which is not Serializeable by ektorp */
	private SmartAssEntry smartAssEntry = new SmartAssEntry();

	public String save() {
		smartAssEntry.setCreationDate(new Date());

		SmartAssEntryDao dao = new SmartAssEntryCouchDb(CouchDbUtil.getDatabase());
		dao.add(smartAssEntry);

		if (smartAssEntry.getEmail() != null && !smartAssEntry.getEmail().isEmpty()) {
			EmailService.INSTANCE.createAndSendMailFile(smartAssEntry.getEmail(), "anditoldyou.so record",
					"/creationEmail.txt", smartAssEntry.getId());
			message = "Wisdom saved. We've sent you an email. Here's the link as well: xxxx" + smartAssEntry.getId();
		} else {
			message = "Wisdom saved. Here's the link for future references: xxxx" + smartAssEntry.getId();
		}

		smartAssEntry = new SmartAssEntry();
		return "index";
	}

}