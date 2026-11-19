package lt.gov.vpb.pdb.springbatch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
@Slf4j
public class EmailSenderService {

    @Value("${opendata.mail.from}")
    private String from;

    @Value("${opendata.mail.to}")
    private String to;

    @Value("${smtp.host}")
    private String host;

    @Value("${smtp.port}")
    private String port;

    @Value("${smtp.user}")
    private String user;

    @Value("${smtp.password}")
    private String password;

    @Value("${opendata.mail.send-enabled}")
    private Boolean mailEnabled;

    public void sendMail(String subject, String body) {

        if (!mailEnabled) {
            return;
        }

        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.user", user);
        properties.setProperty("mail.password", password);
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.auth", "true");

        Authenticator authenticator = new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        };


        log.debug("Initialize SMTP session with properties host={}, port={}, user={}, password-provided={}", host, port, user, StringUtils.isNotEmpty(password));

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, authenticator);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            String[] tos = to.split(",");
            for (String _to : tos) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(_to));
            }

            message.setSubject(subject, "utf-8");
            // Send the actual HTML message, as big as you like
            message.setContent(body, "text/plain; charset=UTF-8");

            // Send message
            Transport.send(message);

            log.info("Sent e-mail successfully");

        } catch (MessagingException mex) {
            log.error("Error sending mail", mex);
        }
    }

}
