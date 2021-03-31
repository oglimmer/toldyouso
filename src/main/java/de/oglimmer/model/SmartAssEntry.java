package de.oglimmer.model;

import java.util.Date;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import org.ektorp.support.CouchDbDocument;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Named
@RequestScoped
public class SmartAssEntry extends CouchDbDocument {

	private static final long serialVersionUID = 1L;

	private String fact;

	private String email;

	private Date creationDate;

	private boolean encrypted;

	private String creatorId;

}
