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

@Service
public class InvoiceEmailService {

    private final JavaMailSender mailSender;
    private final MailProperties properties;
//    private final InvoicePdfService invoicePdfService;

    @Autowired
    public InvoiceEmailService(JavaMailSender mailSender, MailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Async
//    public void sendMailWithInvoice(Invoice invoice) {
    public void sendMailWithInvoice(String number, byte[] invoiceAsPdf) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(properties.getProperties().get("from"));
            helper.setTo(properties.getProperties().get("to"));
            helper.setSubject(properties.getProperties().get("title"));
            helper.setText(properties.getProperties().get("content"));
            helper.addAttachment(String.format("%s.pdf", number), new ByteArrayResource(invoiceAsPdf));
            // helper.addAttachment(String.format("%s.pdf", invoice.getNumber()),
            // new ByteArrayResource(invoicePdfService.getInvoiceAsPdf(invoice)));
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
