package io.github.takkasama.web.tinventoriesws.util;

import java.util.Arrays;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;

import java.util.List;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * @author takkasama
 */
public class EmailManager {

    public enum Host {
        GMAIL("gmail", "smtp.gmail.com", "587"),
        OUTLOOK("outlook", "smtp-mail.outlook.com", "587"),
        OFFICE365("office365", "smtp.office365.com", "587");

        private String host;
        private String port;
        
        private String hostName;

        private Host(String hostName, String host, String port) {
            this.host = host;
            this.port = port;
            this.hostName = hostName;
        }

        public String getHost() {
            return host;
        }
        public void setHost(String host){
            this.host = host;
        }

                
        public String getPort() {
            return port;
        }
        public void setPort(String port){
            this.port = port;
        }

        public String getHostName() {
            return hostName;
        }

        public static Host findHost(String hostName) {
            hostName = hostName.toLowerCase();

            switch (hostName) {
                case "gmail":
                    return GMAIL;
                case "outlook":
                    return OUTLOOK;
                case "office365":
                    return OFFICE365;
                default:
                    return null;
            }
        }
    }
    private static final System.Logger LOG = System.getLogger(EmailManager.class.getName());

    private static Properties props;
    private static Session session;
    private static String userEmail;

    public EmailManager() {
        userEmail = "";
    }

    public static void setInitSession(String mail, char[] appPassword, Host host) {
        init(mail, appPassword, host);
    }

    private static boolean init(String emailUser, char[] emailPassword, Host host) {
        try {
            if (emailPassword == null || emailPassword.length == 0) {
                throw new IllegalArgumentException("The password is necesary to open session.");
            }
            userEmail = emailUser;
            definePropertiesConnection(host);

            final String pwd = new String(emailPassword);

            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailUser, pwd);
                }
            });

            session.setDebug(false);
            Arrays.fill(emailPassword, '\0');
            return true;
        } catch (Exception e) {
            LOG.log(System.Logger.Level.ERROR, "Error : Fail to init de email", e);
            return false;
        }

    }

    private static void definePropertiesConnection(Host host) {
        props = new Properties();
        props.put("mail.smtp.host", host.getHost());
        props.put("mail.smtp.port", host.getPort());
        props.put("mail.smtp.auth", "true");

        //PROTROCOLO QUE SE USA EN REDES PRIVADAS COMO LAS DE UNIVERIDADES.
        //EN DADO CADO USAR EN EL PUERTO 465 NO EL PUETO 587
//        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.smtp.ssl.trust", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
    }

    private static Message buildMessageToPlainBody(String emailReceptor, String subject, String plainBody, Map<String, File> files) throws MessagingException, IllegalArgumentException {

        if (emailReceptor == null || emailReceptor.isBlank()) {
            throw new IllegalArgumentException("[FAIL - buildMessageToPlainBody] : The parameter emailReceptor is necessary to build and send Message");
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userEmail));
        message.setSubject(subject == null || subject.isBlank() ? "NON" : subject);
        message.setSentDate(new Date());

        MimeBodyPart plaintText = new MimeBodyPart();
        plaintText.setText(plainBody, "UTF-8");

        MimeMultipart parts = new MimeMultipart();
        parts.addBodyPart(plaintText);

        if (files != null && !files.isEmpty()) {
            List<String> keys = new ArrayList<>(files.keySet());
            for (String s : keys) {
                MimeBodyPart filePart = new MimeBodyPart();
                File file = files.get(s);
                if (file != null) {
                    filePart.setDataHandler(new DataHandler(new FileDataSource(file)));
                    filePart.setFileName(s);
                    parts.addBodyPart(filePart);
                }
            }
        }

        message.setContent(parts);

        return message;
    }

    private static Message buildMessageToHtmlBody(String emailReceptor, String subject, String htmlBody, Map<String, File> files) throws MessagingException, IllegalArgumentException {

        if (emailReceptor == null || emailReceptor.isBlank()) {
            throw new IllegalArgumentException("[FAIL - buildMessageToHtmlBody] : The parameter emailReceptor is necessary to build and send Message");
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userEmail));
        message.setSubject(subject == null || subject.isBlank() ? "NON" : subject);
        message.setSentDate(new Date());

        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReceptor));

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");

        MimeMultipart parts = new MimeMultipart(htmlPart);
        if (files != null && !files.isEmpty()) {
            List<String> keys = new ArrayList(files.keySet());
            for (String s : keys) {
                MimeBodyPart filePart = new MimeBodyPart();
                File file = files.get(s);
                if (file != null) {
                    filePart.setDataHandler(new DataHandler(new FileDataSource(file)));
                    filePart.setFileName(s);
                    parts.addBodyPart(filePart);
                }
            }
        }

        message.setContent(parts);

        return message;
    }

    public static Answer sendMesssage(String emailReceptor, String subject, String plainBody, Map<String, File> files) {
        try {
            Message message = buildMessageToPlainBody(emailReceptor, subject, plainBody, files);
            Transport.send(message);
            return new Answer(true, AnswerCode.OK, "", "");
        } catch (MessagingException | IllegalArgumentException ex) {
            LOG.log(System.Logger.Level.ERROR, (String) null, ex);
            return new Answer(Boolean.TRUE, AnswerCode.INTERNAL_SERVER_ERROR, "", ex.getMessage());
        }
    }

}
