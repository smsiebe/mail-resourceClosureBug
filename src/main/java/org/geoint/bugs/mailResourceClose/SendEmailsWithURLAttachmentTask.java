package org.geoint.bugs.mailResourceClose;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import static javax.mail.Part.ATTACHMENT;
import javax.mail.internet.MimeBodyPart;

/**
 * Send email using URLDataSource for attachment
 *
 */
public class SendEmailsWithURLAttachmentTask extends AbstractSendEmailTask {

    public SendEmailsWithURLAttachmentTask(int numEmailToSend) {
        super(numEmailToSend);
    }

    @Override
    protected void addAttachment(File attachment, Multipart multipart)
            throws MessagingException, IOException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        URL urlo = attachment.toURI().toURL();
        DataHandler dh = new DataHandler(new URLDataSource(urlo));
        attachmentPart.setDataHandler(dh);
        attachmentPart.setDisposition(ATTACHMENT);
        attachmentPart.setFileName(attachment.getName());
        multipart.addBodyPart(attachmentPart);
    }

}
