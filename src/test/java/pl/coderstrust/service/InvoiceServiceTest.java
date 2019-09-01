package pl.coderstrust.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
        when(database.getAll()).thenReturn(invoices);

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
        when(database.getById(1L)).thenReturn(Optional.of(invoice));

        //when
        Optional<Invoice> result = invoiceService.getInvoiceById(1L);

        //then
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(database).getById(1L);
    }

    @Test
    void getByIdMethodShouldThrowIllegalArgumentExceptionForNullInvoiceId() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getInvoiceById(null));
        verify(database, never()).getById(1L);
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
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoice();
        Invoice addedInvoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoiceToAdd.getId())).thenReturn(false);
        when(database.save(invoiceToAdd)).thenReturn(addedInvoice);

        //when
        Invoice result = invoiceService.addInvoice(invoiceToAdd);

        //then
        assertEquals(addedInvoice, result);
        verify(database).save(invoiceToAdd);
        verify(database).exists(invoiceToAdd.getId());
    }

    @Test
    void shouldAddInvoiceWithNullId() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice addedInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        when(database.save(invoiceToAdd)).thenReturn(addedInvoice);

        //when
        Invoice result = invoiceService.addInvoice(invoiceToAdd);

        //then
        assertEquals(addedInvoice, result);
        verify(database).save(invoiceToAdd);
        verify(database, never()).exists(invoiceToAdd.getId());
    }

    @Test
    void addInvoiceMethodShouldThrowExceptionForInvoiceExistingInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
        verify(database).exists(invoice.getId());
        verify(database, never()).save(invoice);
    }

    @Test
    void addInvoiceMethodShouldThrowIllegalArgumentExceptionForNullInvoice() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.addInvoice(null));
        verify(database, never()).exists(any());
        verify(database, never()).save(any());
    }

    @Test
    void addInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringAddingInvoiceToDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);
        doThrow(new DatabaseOperationException()).when(database).save(invoice);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
        verify(database).exists(invoice.getId());
        verify(database).save(invoice);
    }

    @Test
    void shouldUpdateGivenInvoiceInDatabase() throws ServiceOperationException, DatabaseOperationException {
        //given
        Invoice invoiceToUpdate = InvoiceGenerator.getRandomInvoice();
        Invoice invoiceUpdated = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoiceToUpdate.getId())).thenReturn(true);
        when(database.save(invoiceToUpdate)).thenReturn(invoiceUpdated);

        //when
        Invoice result = invoiceService.updateInvoice(invoiceToUpdate);

        //then
        assertEquals(invoiceUpdated, result);
        verify(database).save(invoiceToUpdate);
        verify(database).exists(invoiceToUpdate.getId());
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionForNullInvoiceId() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithNullId();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
        verify(database, never()).exists(any());
        verify(database, never()).save(any());
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionWhenInvoiceNotExistInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
        verify(database).exists(invoice.getId());
        verify(database, never()).save(invoice);
    }

    @Test
    void updateInvoiceMethodShouldThrowIllegalArgumentExceptionForNullInvoice() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(null));
        verify(database, never()).exists(any());
        verify(database, never()).save(any());
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringUpdateInvoiceInDatabase() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        doThrow(new DatabaseOperationException()).when(database).save(invoice);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
        verify(database).exists(invoice.getId());
        verify(database).save(invoice);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException, ServiceOperationException {
        //given
        doNothing().when(database).delete(1L);

        //when
        invoiceService.deleteInvoiceById(1L);

        //then
        verify(database).delete(1L);
    }

    @Test
    void deleteInvoiceMethodShouldThrowIllegalArgumentExceptionForNullInvoiceId() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.deleteInvoiceById(null));
        verify(database, never()).delete(any());
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
    void shouldReturnTrueWhenInvoiceExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(database.exists(1L)).thenReturn(true);

        //when
        boolean result = invoiceService.invoiceExists(1L);

        //then
        assertTrue(result);
        verify(database).exists(1L);
    }

    @Test
    void shouldReturnFalseWhenInvoiceDoesNotExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(database.exists(1L)).thenReturn(false);

        //when
        boolean result = invoiceService.invoiceExists(1L);

        //then
        assertFalse(result);
        verify(database).exists(1L);
    }

    @Test
    void invoiceExistsMethodShouldThrowIllegalArgumentExceptionForNullInvoiceId() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.invoiceExists(null));
        verify(database, never()).exists(any());
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExists() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).exists(1L);

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoiceExists(1L));
        verify(database).exists(1L);
    }

    @Test
    void shouldReturnInvoiceByGivenNumber() throws DatabaseOperationException, ServiceOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(database.getByNumber("1/1/1")).thenReturn(Optional.of(invoice));

        //when
        Optional<Invoice> result = invoiceService.getInvoiceByNumber("1/1/1");

        //then
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(database).getByNumber("1/1/1");
    }

    @Test
    void getInvoiceByNumberMethodShouldThrowIllegalArgumentExceptionForNullInvoiceNumber() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getInvoiceByNumber(null));
        verify(database, never()).getByNumber(any());
    }

    @Test
    void getInvoiceByNumberMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoiceByNumber() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).getByNumber("1/1/1");

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.getInvoiceByNumber("1/1/1"));
        verify(database).getByNumber("1/1/1");
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(database.count()).thenReturn(10L);

        //when
        Long result = invoiceService.invoicesCount();

        //then
        assertEquals(10L, result);
        verify(database).count();
    }

    @Test
    void invoicesCountMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingNumberOfInvoices() throws DatabaseOperationException {
        //given
        doThrow(new DatabaseOperationException()).when(database).count();

        //then
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoicesCount());
        verify(database).count();
    }

    @Test
    void shouldReturnInvoicesFilteredByIssuedDate() throws ServiceOperationException, DatabaseOperationException {
        //given
        LocalDate startDate = LocalDate.of(2019, 8, 26);
        LocalDate endDate = startDate.plusDays(2L);

        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.plusDays(1L));
        Invoice invoice3 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(endDate);

        List<Invoice> filteredInvoices = List.of(invoice1, invoice2, invoice3);

        when(database.getByIssueDate(startDate, endDate)).thenReturn(filteredInvoices);

        //when
        Collection<Invoice> result = invoiceService.getByIssueDate(startDate, startDate.plusDays(2L));

        //then
        assertEquals(filteredInvoices, result);
        verify(database).getByIssueDate(startDate, endDate);
    }

    @ParameterizedTest
    @MethodSource("invalidIssuedDateArgumentsAndExceptionMessages")
    void getByIssuedDateMethodShouldThrowExceptionWhenInvalidArgumentsArePassed(LocalDate startDate, LocalDate endDate, String message) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> invoiceService.getByIssueDate(startDate, endDate));
        assertEquals(message, exception.getMessage());
    }

    private static Stream<Arguments> invalidIssuedDateArgumentsAndExceptionMessages() {
        return Stream.of(
            Arguments.of(null, null, "Start date cannot be null"),
            Arguments.of(null, LocalDate.of(2018, 8, 31), "Start date cannot be null"),
            Arguments.of(LocalDate.of(2019, 8, 22), null, "End date cannot be null"),
            Arguments.of(LocalDate.of(2019, 8, 22), LocalDate.of(2018, 8, 31), "Start date cannot be after end date"),
            Arguments.of(LocalDate.of(2019, 2, 28), LocalDate.of(2019, 1, 31), "Start date cannot be after end date"),
            Arguments.of(LocalDate.of(2019, 2, 28), LocalDate.of(2009, 3, 31), "Start date cannot be after end date")
        );
    }

    @Test
    void getByIssuedDateMethodShouldThrowExceptionWhenDatabaseOperationExceptionOccurs() throws DatabaseOperationException {
        LocalDate startDate = LocalDate.of(2019, 8, 26);
        LocalDate endDate = startDate.plusDays(2L);
        doThrow(new DatabaseOperationException()).when(database).getByIssueDate(startDate, endDate);

        ServiceOperationException exception = assertThrows(ServiceOperationException.class, () -> invoiceService.getByIssueDate(startDate, endDate));
        assertEquals("An error occurred during getting invoices filtered by issued date.", exception.getMessage());

        verify(database).getByIssueDate(startDate, endDate);
    }
}
