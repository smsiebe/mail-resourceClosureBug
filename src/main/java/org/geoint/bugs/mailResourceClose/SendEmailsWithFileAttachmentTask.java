package org.geoint.bugs.mailResourceClose;

import java.io.File;
import java.io.IOException;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import static javax.mail.Part.ATTACHMENT;
import javax.mail.internet.MimeBodyPart;

/**
 * Sends emails with FileDataSource
 */
public class SendEmailsWithFileAttachmentTask extends AbstractSendEmailTask {

    public SendEmailsWithFileAttachmentTask(int numEmailToSend) {
        super(numEmailToSend);
    }

    @Override
    protected void addAttachment(File attachment, Multipart multipart)
            throws MessagingException, IOException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        //explicit use of FileDataSource, rather than implicit through MimeBodyPart#attachFile
        FileDataSource fds = new FileDataSource(attachment);
        attachmentPart.setDataHandler(new DataHandler(fds));
        attachmentPart.setDisposition(ATTACHMENT);
        attachmentPart.setFileName(attachment.getName());
        multipart.addBodyPart(attachmentPart);
    }

}
