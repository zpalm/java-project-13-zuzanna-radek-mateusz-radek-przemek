package pl.coderstrust.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceEmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final InvoicePdfService invoicePdfService;

    @Autowired
    public InvoiceEmailService(JavaMailSender mailSender, MailProperties mailProperties, InvoicePdfService invoicePdfService) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.invoicePdfService = invoicePdfService;
    }

    @Async
    public void sendMailWithInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(mailProperties.getProperties().get("to"));
            helper.setSubject(mailProperties.getProperties().get("title"));
            helper.setText(mailProperties.getProperties().get("content"));
            helper.addAttachment(String.format("%s.pdf", invoice.getNumber()), new ByteArrayResource(invoicePdfService.createPdf(invoice)));
            mailSender.send(message);
        } catch (MessagingException | ServiceOperationException e) {
            e.printStackTrace();
        }
    }
}
