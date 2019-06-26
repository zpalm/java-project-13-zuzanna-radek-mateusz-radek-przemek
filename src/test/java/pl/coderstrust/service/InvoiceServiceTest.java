package pl.coderstrust.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    Database database;

    @InjectMocks
    InvoiceService invoiceService;

    @Test
    void shouldReturnAllInvoices() throws ServiceOperationException, DatabaseOperationException {
        //given
        List<Invoice> invoices = List.of(InvoiceGenerator.getRandomInvoice(), InvoiceGenerator.getRandomInvoice());
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        //when
        Collection<Invoice> result = invoiceService.getAllInvoices();

        //then
        assertEquals(invoices, result);
        verify(database).getAll();
    }

    @Test
    void getAllInvoicesMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingAllInvoicesFromDatabase() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).getAll();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.getAllInvoices());
        verify(database).getAll();
    }

    @Test
    void shouldReturnInvoiceByGivenId() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceService.getInvoiceById(1L)).thenReturn(Optional.of(invoice));

        //when
        Optional<Invoice> result = invoiceService.getInvoiceById(1L);

        //then
        assertEquals(invoice, result.get());
        verify(database).getById(1L);
    }

    @Test
    void getByIdMethodShouldThrowIllegalArgumentExceptionForNullInvoiceId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getInvoiceById(null));
    }

    @Test
    void getInvoiceByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoiceByIdFromDatabase() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).getById(1L);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.getInvoiceById(1L));
        verify(database).getById(1L);
    }

    @Test
    void shouldAddInvoice() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);
        when(invoiceService.addInvoice(invoice)).thenReturn(invoice);

        //when
        Invoice result = invoiceService.addInvoice(invoice);

        //then
        assertEquals(invoice, result);
        verify(database).save(invoice);
    }

    @Test
    void shouldAddInvoiceWithNullId() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        when(invoiceService.addInvoice(invoice)).thenReturn(invoice);

        //when
        Invoice result = invoiceService.addInvoice(invoice);

        //then
        assertEquals(invoice, result);
        verify(database).save(invoice);
    }

    @Test
    void addInvoiceMethodShouldThrowExceptionForInvoiceExistingInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
        verify(database).exists(invoice.getId());
    }

    @Test
    void addInvoiceMethodShouldThrowIllegalArgumentExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.addInvoice(null));
    }

    @Test
    void addInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringAddingInvoiceToDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doThrow(new DatabaseOperationException()).when(database).save(invoice);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
        verify(database).save(invoice);
    }

    @Test
    void shouldUpdateGivenInvoiceInDatabase() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        when(invoiceService.updateInvoice(invoice)).thenReturn(invoice);

        //when
        Invoice result = invoiceService.updateInvoice(invoice);

        //then
        assertEquals(invoice, result);
        verify(database).save(invoice);
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionForNullInvoiceId() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithNullId();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionForInvoiceNotExistInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
        verify(database).exists(invoice.getId());
    }

    @Test
    void updateInvoiceMethodShouldThrowIllegalArgumentExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(null));
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringUpdateInvoiceInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        doThrow(new DatabaseOperationException()).when(database).save(invoice);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
        verify(database).save(invoice);
    }

    @Test
    void shouldDeleteGivenInvoice() throws DatabaseOperationException, ServiceOperationException {
        //given
        doNothing().when(database).delete(1L);

        //when
        invoiceService.deleteInvoiceById(1L);

        //then
        verify(database).delete(1L);
    }

    @Test
    void deleteInvoiceMethodShouldThrowIllegalArgumentExceptionForNullInvoiceId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.deleteInvoiceById(null));
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingInvoiceByIdFromDatabase() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).delete(1L);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(1L));
        verify(database).delete(1L);
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException, ServiceOperationException {
        //given
        doNothing().when(database).deleteAll();

        //when
        invoiceService.deleteAllInvoices();

        //then
        verify(database).deleteAll();
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingAllInvoicesFromDatabase() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).deleteAll();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAllInvoices());
        verify(database).deleteAll();
    }

    @Test
    void shouldReturnTrueIfGivenInvoiceExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(database.exists(1L)).thenReturn(true);

        //when
        boolean result = invoiceService.invoiceExists(1L);

        //then
        assertTrue(result);
        verify(database).exists(1L);
    }

    @Test
    void shouldReturnFalseIfGivenInvoiceNotExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(database.exists(1L)).thenReturn(false);

        //when
        boolean result = invoiceService.invoiceExists(1L);

        //then
        assertFalse(result);
        verify(database).exists(1L);
    }

    @Test
    void invoiceExistsMethodShouldThrowIllegalArgumentExceptionForNullInvoiceId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.invoiceExists(null));
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExists() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).exists(1L);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoiceExists(1L));
        verify(database).exists(1L);
    }
}
