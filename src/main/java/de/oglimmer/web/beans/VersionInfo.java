package de.oglimmer.web.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import de.oglimmer.utils.VersionFromManifest;
import lombok.Getter;

@ApplicationScoped
@Named
public class VersionInfo {

	@Getter
	private String longVersion;

	@PostConstruct
	public void init() {
		VersionFromManifest vfm = new VersionFromManifest();
		vfm.init(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
		longVersion = vfm.getLongVersion();
	}

}
