package cr.ac.una.web.template.util;

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
import java.util.Date;

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

        public String getPort() {
            return port;
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

    private static EmailManager instance;
    private static Properties props;
    private static Session session;
    private static String emailUser;
    private static boolean initialized = false;

    private static String host;
    private static String port = "587";

    private EmailManager() {
    }

    public static EmailManager getInstance() {
        if (instance == null) {
            synchronized (EmailManager.class) {
                if (instance == null) {
                    instance = new EmailManager();
                }
            }
        }
        return instance;
    }

    public static void setInitSession(String mail, char[] appPassword, Host host) {
        init(mail, appPassword, host);
    }

    private static void setPort(String port) {
        EmailManager.port = port;
    }

    private static void setEmailUser(String emailUser) {
        EmailManager.emailUser = emailUser;
    }

    private static void setHost(Host host) {
        EmailManager.host = host.getHost();
    }

    private static void init(String emailUser, char[] emailPassword, Host host) {
        try {
            if (emailPassword == null || emailPassword.length == 0) {
                throw new IllegalArgumentException("The password is necesary to open session.");
            }
            setHost(host);
            setEmailUser(emailUser);
            definePropertiesConnection();

            final String pwd = new String(emailPassword);

            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailUser, pwd);
                }
            });

            session.setDebug(false);
            Arrays.fill(emailPassword, '\0');
            initialized = true;
        } catch (Exception e) {
            LOG.log(System.Logger.Level.ERROR, "Error : Fail to init de email", e);
        }
    }

    private static void definePropertiesConnection() {
        props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
    }

    private Message buildMessage(String emailReceptor, String subject, String htmlBody, String plainBody, List<File> files, List<String> fileNames)
            throws MessagingException {
        checkInitialized();

        MimeMultipart multipart = new MimeMultipart();
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailUser));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailReceptor));
        message.setSubject(subject);
        message.setSentDate(new Date());

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(plainBody, "UTF-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");

        if (files != null && fileNames != null) {
            MimeBodyPart filesPart = null;
            for (int i = 0; i < files.size(); i++) {
                filesPart = new MimeBodyPart();
                filesPart.setDataHandler(new DataHandler(new FileDataSource(files.get(i))));
                filesPart.setFileName(fileNames.get(i));
                multipart.addBodyPart(filesPart);
            }

        }
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);
        message.setContent(multipart);

        return message;
    }

    public boolean sendMessage(String emailReceptor, String subject, String htmlBody, String plainBody) {
        try {
            Transport.send(buildMessage(emailReceptor, subject, htmlBody, plainBody, null, null));
            return true;
        } catch (AuthenticationFailedException e) {
            System.err.println("Autenticación fallida: " + e.getMessage());
        } catch (SendFailedException e) {
            System.err.println("Dirección inválida o rechazada: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("Error de red o protocolo: " + e.getMessage());
        }
        return false;
    }

    public boolean sendMessageWithFiles(String emailReceptor, String subject,
            String htmlBody, String plainBody, List<File> files, List<String> fileNames) {
        try {
            Transport.send(buildMessage(emailReceptor, subject, htmlBody, plainBody, files, fileNames));
            return true;
        } catch (AuthenticationFailedException e) {
            System.err.println("Autenticación fallida: " + e.getMessage());
        } catch (SendFailedException e) {
            System.err.println("Dirección inválida o rechazada: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("Error de red o protocolo: " + e.getMessage());
        }
        return false;
    }

    private static void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("EmailManager no inicializado. Llamar a init(user, password) primero.");
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
