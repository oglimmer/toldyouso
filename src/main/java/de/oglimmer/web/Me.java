package de.oglimmer.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class Me implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String name;

	@PostConstruct
	public void init() {
		name = "foo";
	}
}