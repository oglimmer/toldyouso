package de.oglimmer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum EmailService {
	INSTANCE;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public void shutdown() {
		log.debug("Stopping EmailService...");
		executor.shutdown();
	}

	public void createAndSendMailFile(String email, String subject, String fileName, Object... params) {
		try (InputStream is = getClass().getResourceAsStream(fileName)) {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, null);
			String msg = writer.toString();
			createAndSendMail(email, subject, String.format(msg, params));
		} catch (IOException e) {
			log.error("Failed to get resource from file", e);
		}
	}

	private void createAndSendMail(String email, String subject, String msg) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Email simpleEmail = new SimpleEmail();
					simpleEmail.setCharset(EmailConstants.UTF_8);
					simpleEmail.setHostName(Configuration.INSTANCE.getSmtpHost());
					if (Configuration.INSTANCE.getSmtpPort() > 0) {
						simpleEmail.setSmtpPort(Configuration.INSTANCE.getSmtpPort());
					}
					simpleEmail.setSSLOnConnect(Configuration.INSTANCE.getSmtpSSL());
					if (!Configuration.INSTANCE.getSmtpUser().isEmpty()) {
						simpleEmail.setAuthentication(Configuration.INSTANCE.getSmtpUser(),
								Configuration.INSTANCE.getSmtpPassword());
					}
					simpleEmail.setFrom(Configuration.INSTANCE.getSmtpFrom());
					simpleEmail.setSubject(subject);
					simpleEmail.setMsg(msg);
					simpleEmail.addTo(email);
					simpleEmail.send();
				} catch (EmailException e) {
					log.error("Failed to send password email", e);
				}
			}
		});
	}

}