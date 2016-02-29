package de.oglimmer.web.beans;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.db.couchdb.CouchDbUtil;
import de.oglimmer.db.couchdb.SmartAssEntryCouchDb;
import de.oglimmer.model.SmartAssEntry;
import de.oglimmer.util.EmailService;
import de.oglimmer.util.LinkGenerator;
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

		String link = "<a href='" + LinkGenerator.INSTANCE.make(smartAssEntry.getId()) + "'>"
				+ LinkGenerator.INSTANCE.make(smartAssEntry.getId()) + "</a>";

		if (smartAssEntry.getEmail() != null && !smartAssEntry.getEmail().isEmpty()) {
			EmailService.INSTANCE.createAndSendMailFile(smartAssEntry.getEmail(), "anditoldyou.so record",
					"/creationEmail.txt", smartAssEntry.getFact(), LinkGenerator.INSTANCE.make(smartAssEntry.getId()));
			message = "Wisdom saved. We've sent you an email. Here's the link as well: " + link;
		} else {
			message = "Wisdom saved. Here's the link for future references: " + link + ". Keep it safe!";
		}

		smartAssEntry = new SmartAssEntry();
		return "index";
	}

}