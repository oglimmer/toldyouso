package de.oglimmer.web.beans;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import lombok.Getter;

@ApplicationScoped
@Named
public class VersionInfo {

	@Getter
	private String longVersion;

	@PostConstruct
	public void init() {
		String commit;
		String version;
		String creationDate;
		try (InputStream is = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream("/META-INF/MANIFEST.MF")) {
			Manifest mf = new Manifest(is);
			Attributes attr = mf.getMainAttributes();
			commit = attr.getValue("SVN-Revision-No");
			version = attr.getValue("TYS-Version");
			long time = Long.parseLong(attr.getValue("Creation-Date"));
			creationDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(time));
		} catch (Exception e) {
			commit = "?";
			creationDate = "?";
			version = "?";
		}
		longVersion = "V" + version + " [Commit#" + commit + "] build " + creationDate;
	}

}
