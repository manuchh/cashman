package wns.cashman.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private static final Logger log = LoggerFactory.getLogger(EmailSender.class);
	
	@Autowired
	JavaMailSender javaMailSender;
	
	public void sendMail(String from, String to, String subject, String body) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(from);
		mail.setTo(to);
		mail.setSubject(subject);
		mail.setText(body);
		javaMailSender.send(mail);
		log.info("Email sent!");
	}
}
