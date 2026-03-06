package de.oglimmer.web.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.Getter;

@ApplicationScoped
@Named
public class VersionInfo {

	@Getter
	private String longVersion;

	@PostConstruct
	public void init() {
		try (InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/META-INF/MANIFEST.MF")) {
			Manifest manifest = new Manifest(is);
			Attributes attrs = manifest.getMainAttributes();
			String commit = attrs.getValue("git-commit");
			String gitUrl = attrs.getValue("git-url");
			String version = attrs.getValue("project-version");
			long creationDateMillis = Long.parseLong(attrs.getValue("creation-date"));
			String creationDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(creationDateMillis));
			longVersion = "V" + version + " [<a href='" + gitUrl + "/commits/" + commit + "'>Commit#" + commit + "</a>] build " + creationDate;
		} catch (Exception e) {
			String creationDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date());
			longVersion = "V? [<a href='#'>Commit#?</a>] build " + creationDate;
		}
	}

}
