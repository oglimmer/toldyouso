package de.oglimmer.model;

import java.util.Date;

import org.ektorp.support.CouchDbDocument;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class User extends CouchDbDocument {

	private static final long serialVersionUID = 1L;

	private String email;

	private String loginPassword;

	private Date creationDate;

	private Date lastLoginDate;

	private int failedLogins;

	private Date disabledUntil;

	private String factPassword;

	private byte[] initVector;

}
