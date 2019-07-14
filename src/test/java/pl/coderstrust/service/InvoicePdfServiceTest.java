package pl.coderstrust.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoicePdfServiceTest {

    InvoicePdfService invoicePdfService;

    @BeforeEach
    void setUp() {
        invoicePdfService = new InvoicePdfService();
    }

    @Test
    void shouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> invoicePdfService.createPdf(null));
    }

    @Test
    void shouldReturnPdfForPassedInvoice() throws ServiceOperationException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();

        byte[] result = invoicePdfService.createPdf(invoice);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
