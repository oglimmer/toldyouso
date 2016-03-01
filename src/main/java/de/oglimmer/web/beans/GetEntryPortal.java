package de.oglimmer.web.beans;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import de.oglimmer.db.SmartAssEntryDao;
import de.oglimmer.db.couchdb.CouchDbUtil;
import de.oglimmer.db.couchdb.SmartAssEntryCouchDb;
import de.oglimmer.model.SmartAssEntry;
import lombok.Getter;

@RequestScoped
@Named
public class GetEntryPortal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	private SmartAssEntry smartAssEntry;

	@PostConstruct
	public void load() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = params.get("id");
		if (id == null || id.isEmpty()) {
			HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			id = (String) req.getAttribute("id");
		}
		if (id != null && !id.isEmpty()) {
			SmartAssEntryDao dao = new SmartAssEntryCouchDb(CouchDbUtil.getDatabase());
			try {
				smartAssEntry = dao.get(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}