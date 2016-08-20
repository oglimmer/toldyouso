package de.oglimmer.web.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

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
