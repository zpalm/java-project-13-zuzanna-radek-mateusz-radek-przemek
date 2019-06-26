package pl.coderstrust.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void shouldReturnAllInvoicesFromDatabase() throws ServiceOperationException, DatabaseOperationException {
        //given
        List<Invoice> invoices = List.of(InvoiceGenerator.getRandomInvoice(), InvoiceGenerator.getRandomInvoice());
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        //when
        invoiceService.getAllInvoices();

        //then
        verify(database).getAll();
    }

    @Test
    void shouldThrowServiceOperationExceptionForGetAllInvoicesOperationError() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).getAll();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.getAllInvoices());
    }

    @Test
    void shouldReturnInvoiceByGivenIdFromDatabase() throws ServiceOperationException, DatabaseOperationException {
        //given
        when(invoiceService.getInvoiceById(1L)).thenReturn(Optional.of(InvoiceGenerator.getRandomInvoice()));

        //when
        invoiceService.getInvoiceById(1L);

        //then
        verify(database).getById(1L);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullInvoiceIdAsGetInvoiceByIdArgument() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getInvoiceById(null));
    }

    @Test
    void shouldThrowServiceOperationExceptionForGetInvoiceByIdOperationError() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).getById(1L);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.getInvoiceById(1L));
    }

    @Test
    void shouldAddGivenInvoiceWithNotNullId() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);
        when(invoiceService.addInvoice(invoice)).thenReturn(invoice);

        //when
        invoiceService.addInvoice(invoice);

        //then
        verify(database).save(invoice);
    }

    @Test
    void shouldAddGivenInvoiceWithNullId() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        when(invoiceService.addInvoice(invoice)).thenReturn(invoice);

        //when
        invoiceService.addInvoice(invoice);

        //then
        verify(database).save(invoice);
    }

    @Test
    void shouldThrowServiceOperationExceptionForAddInvoiceExistingInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(1L)).thenReturn(true);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullInvoiceAsAddInvoiceArgument() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.addInvoice(null));
    }

    @Test
    void shouldThrowServiceOperationExceptionForAddInvoiceOperationError() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doThrow(new DatabaseOperationException()).when(database).save(invoice);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    void shouldUpdateGivenInvoiceInDatabase() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        when(invoiceService.updateInvoice(invoice)).thenReturn(invoice);

        //when
        invoiceService.updateInvoice(invoice);

        //then
        verify(database).save(invoice);
    }

    @Test
    void shouldThrowServiceOperationExceptionForNullIdOfGivenInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithNullId();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void shouldThrowServiceOperationExceptionForInvoiceNotExistInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullInvoiceAsUpdateInvoiceArgument() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(null));
    }

    @Test
    void shouldThrowServiceOperationExceptionForUpdateInvoiceOperationError() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        doThrow(new DatabaseOperationException()).when(database).save(invoice);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void shouldDeleteGivenInvoiceFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        doNothing().when(database).delete(1L);

        //when
        invoiceService.deleteInvoiceById(1L);

        //then
        verify(database).delete(1L);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullInvoiceIdAsDeleteInvoiceByIdArgument() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.deleteInvoiceById(null));
    }

    @Test
    void shouldThrowServiceOperationExceptionForDeleteInvoiceByIdOperationError() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).delete(1L);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(1L));
    }

    @Test
    void shouldDeleteAllInvoicesFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        doNothing().when(database).deleteAll();

        //when
        invoiceService.deleteAllInvoices();

        //then
        verify(database).deleteAll();
    }

    @Test
    void shouldThrowServiceOperationExceptionForDeleteAllOperationError() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).deleteAll();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAllInvoices());
    }

    @Test
    void shouldReturnTrueIfGivenInvoiceExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(database.exists(1L)).thenReturn(true);
        //when
        invoiceService.invoiceExists(1L);
        //then
        verify(database).exists(1L);
    }

    @Test
    void shouldReturnFalseIfGivenInvoiceNotExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(database.exists(1L)).thenReturn(false);
        //when
        invoiceService.invoiceExists(1L);
        //then
        verify(database).exists(1L);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNullInvoiceIdAsInvoiceExistsArgument() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.invoiceExists(null));
    }

    @Test
    void shouldThrowServiceOperationExceptionForInvoiceExistsOperationError() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).exists(1L);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoiceExists(1L));
    }
}
