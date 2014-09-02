package org.geoint.bugs.mailResourceClose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 */
public abstract class AbstractSendEmailTask implements Runnable {

    private int numEmailToSend;

    public AbstractSendEmailTask(int numEmailToSend) {
        this.numEmailToSend = numEmailToSend;
    }

    @Override
    public void run() {
        Session session = createMailSession();

        File tmpAttachment = null;
        try {
            tmpAttachment = File.createTempFile("resourceLeakTest", "att");
            try (Writer w = new PrintWriter(new FileOutputStream(tmpAttachment))) {
                w.write("Test Attachment");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        while (numEmailToSend > 0) {
            try {
                MimeMessage mimeMsg = createMessage(tmpAttachment, session);

                Transport t = session.getTransport("smtp");
                t.connect();

                t.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
                t.close();
            } catch (MessagingException | IOException ex) {
                ex.printStackTrace();
            }
            numEmailToSend--;
        }
    }

    private MimeMessage createMessage(File attachment, Session session)
            throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session);
        Multipart multipart = new MimeMultipart("related");

        String to = "resourceBug@localhost.com";
        String from = "sender@localhost.com";
        message.setFrom(new InternetAddress(from));

        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(to));
        message.setSubject("Test Subject");
        message.setText("Test Body");

        for (int i = 0; i < 2; i++) {
            addAttachment(attachment, multipart);
        }

        message.setContent(multipart);
        message.saveChanges();
        return message;
    }

    private Session createMailSession() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", EmailResourceLeakTest.SMTP_HOST);
        properties.put("mail.smtp.auth", "false");
        properties.put("mail.transport.protocol", EmailResourceLeakTest.SMTP_PROTOCOL);
        properties.put("mail.smtp.socketFactory.port", EmailResourceLeakTest.SMTP_PORT);
        properties.put("mail.smtp.port", EmailResourceLeakTest.SMTP_PORT);
        properties.put("mail.smtp.user", "login-id");
        properties.put("mail.smtp.password", "password");
        return Session.getInstance(properties);
    }

    protected abstract void addAttachment(File attachment, Multipart multipart)
            throws MessagingException, IOException;
}
