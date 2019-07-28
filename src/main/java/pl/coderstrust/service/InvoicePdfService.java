package pl.coderstrust.service;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@Service
public class InvoicePdfService {

    private static final int NUM_COLUMNS = 6;
    private static final int WIDTH_PERCENTAGE = 100;
    private static final int HEADER_FONT_SIZE = 14;
    private static final float TAB_INTERVAL = 300f;

    private Logger log = LoggerFactory.getLogger(InvoicePdfService.class);

    public byte[] createPdf(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            log.error("Attempt to create PDF for null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        Document document = new Document();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, byteStream);
            document.open();
            document.add(new Paragraph(generateInvoiceHeader(invoice)));
            document.add(new Paragraph(generateCompanyData(invoice)));
            document.add(new PdfPTable(generateEntriesTableHeader()));
            document.add(new PdfPTable(generateEntriesTableBody(invoice)));
            document.add(new PdfPTable(generateEntriesTableFooter(invoice)));
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph(generateSignatureLine()));
            document.close();
            return byteStream.toByteArray();
        } catch (DocumentException e) {
            String message = "An error occurred during generating pdf.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    private Paragraph generateInvoiceHeader(Invoice invoice) {
        Paragraph invoiceHeader = new Paragraph();
        Paragraph invoiceNumber = new Paragraph(String.format("Invoice number: %s", invoice.getNumber()), new Font(Font.FontFamily.TIMES_ROMAN, HEADER_FONT_SIZE));
        Paragraph paymentDueDate = new Paragraph(String.format("Due date: %tD", invoice.getDueDate()));
        Paragraph accountNumber = new Paragraph(String.format("Account number: %s", invoice.getSeller().getAccountNumber()));

        invoiceHeader.add(invoiceNumber);
        invoiceHeader.add(Chunk.NEWLINE);
        invoiceHeader.add(paymentDueDate);
        invoiceHeader.add(accountNumber);
        invoiceHeader.add(Chunk.NEWLINE);
        return invoiceHeader;
    }

    private Paragraph generateCompanyData(Invoice invoice) {
        Paragraph companyData = new Paragraph();
        Paragraph companyDataHeader = generateCompanyDataLine("Seller:", "Buyer:");
        Paragraph companyNames = generateCompanyDataLine(invoice.getSeller().getName(), invoice.getBuyer().getName());
        Paragraph companyAddresses = generateCompanyDataLine(invoice.getSeller().getAddress(), invoice.getBuyer().getAddress());
        Paragraph companyTaxIds = generateCompanyDataLine(invoice.getSeller().getTaxId(), invoice.getBuyer().getTaxId());

        companyData.add(companyDataHeader);
        companyData.add(companyNames);
        companyData.add(companyAddresses);
        companyData.add(companyTaxIds);
        companyData.add(Chunk.NEWLINE);
        return companyData;
    }

    private Paragraph generateCompanyDataLine(String sellerData, String buyerData) {
        Paragraph companyDataLine = new Paragraph(String.format("%s", sellerData));
        companyDataLine.setTabSettings(new TabSettings(TAB_INTERVAL));
        companyDataLine.add(Chunk.TABBING);
        companyDataLine.add(String.format("%s", buyerData));
        return companyDataLine;
    }

    private PdfPTable generateEntriesTableHeader() {
        PdfPTable entriesTableHeader = new PdfPTable(NUM_COLUMNS);
        entriesTableHeader.setWidthPercentage(WIDTH_PERCENTAGE);
        entriesTableHeader.addCell("Description");
        entriesTableHeader.addCell("Quantity");
        entriesTableHeader.addCell("Price");
        entriesTableHeader.addCell("Net Value");
        entriesTableHeader.addCell("Vat Rate");
        entriesTableHeader.addCell("Gross Value");
        return entriesTableHeader;
    }

    private PdfPTable generateEntriesTableBody(Invoice invoice) {
        PdfPTable entriesTable = new PdfPTable(NUM_COLUMNS);
        entriesTable.setWidthPercentage(WIDTH_PERCENTAGE);
        for (InvoiceEntry entry : invoice.getEntries()) {
            PdfPCell description = new PdfPCell(new Paragraph(entry.getDescription()));
            PdfPCell quantity = new PdfPCell(new Paragraph(String.valueOf(entry.getQuantity())));
            PdfPCell price = new PdfPCell(new Paragraph(String.valueOf(entry.getPrice())));
            PdfPCell netValue = new PdfPCell(new Paragraph(String.valueOf(entry.getNetValue())));
            PdfPCell vatRate = new PdfPCell(new Paragraph(String.format("%,.2f %%", entry.getVatRate().getValue() * 100)));
            PdfPCell grossValue = new PdfPCell(new Paragraph(String.valueOf(entry.getGrossValue())));

            entriesTable.addCell(description);
            entriesTable.addCell(quantity);
            entriesTable.addCell(price);
            entriesTable.addCell(netValue);
            entriesTable.addCell(vatRate);
            entriesTable.addCell(grossValue);
        }
        return entriesTable;
    }

    private PdfPTable generateEntriesTableFooter(Invoice invoice) {
        PdfPTable entriesTableFooter = new PdfPTable(NUM_COLUMNS);
        entriesTableFooter.setWidthPercentage(WIDTH_PERCENTAGE);
        BigDecimal totalNetValue = BigDecimal.ZERO;
        BigDecimal totalGrossValue = BigDecimal.ZERO;

        for (InvoiceEntry entry : invoice.getEntries()) {
            totalNetValue = totalNetValue.add(entry.getNetValue());
            totalGrossValue = totalGrossValue.add(entry.getGrossValue());
        }
        entriesTableFooter.addCell(new PdfPCell());
        entriesTableFooter.addCell(new PdfPCell());
        entriesTableFooter.addCell(new PdfPCell(new Paragraph("Total:")));
        entriesTableFooter.addCell(new PdfPCell(new Paragraph(String.valueOf(totalNetValue))));
        entriesTableFooter.addCell(new PdfPCell());
        entriesTableFooter.addCell(new PdfPCell(new Paragraph(String.valueOf(totalGrossValue))));

        return entriesTableFooter;
    }

    private Paragraph generateSignatureLine() {
        Paragraph signatureLine = new Paragraph("Seller's signature:");
        signatureLine.setTabSettings(new TabSettings(TAB_INTERVAL));
        signatureLine.add(Chunk.TABBING);
        signatureLine.add("Buyer's signature:");
        return signatureLine;
    }
}
