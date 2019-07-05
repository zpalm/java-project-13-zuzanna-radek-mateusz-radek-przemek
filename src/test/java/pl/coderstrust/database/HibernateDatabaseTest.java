package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Example;
import pl.coderstrust.database.hibernate.InvoiceRepository;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class HibernateDatabaseTest {

    @Mock
    InvoiceRepository invoiceRepository;

    @InjectMocks
    HibernateDatabase database;

    @Test
    void shouldSaveInvoice() throws DatabaseOperationException {
        //given
        Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();
        Invoice saveInvoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceRepository.save(invoiceToSave)).thenReturn(saveInvoice);

        //when
        Invoice result = database.save(invoiceToSave);

        //then
        assertEquals(saveInvoice, result);
        verify(invoiceRepository).save(invoiceToSave);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringSavingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doThrow(new NonTransientDataAccessException(""){}).when(invoiceRepository).save(invoice);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        //given
        when(invoiceRepository.existsById(1L)).thenReturn(true);
        doNothing().when(invoiceRepository).deleteById(1L);

        //when
        database.delete(1L);

        //then
        verify(invoiceRepository).existsById(1L);
        verify(invoiceRepository).deleteById(1L);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingInvoice() {
        //given
        when(invoiceRepository.existsById(100L)).thenReturn(false);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(100L));
        verify(invoiceRepository).existsById(100L);
        verify(invoiceRepository, never()).deleteById(1L);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringDeletingInvoice() {
        //given
        when(invoiceRepository.existsById(1L)).thenReturn(true);
        doThrow(new NonTransientDataAccessException(""){}).when(invoiceRepository).deleteById(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(1L));
        verify(invoiceRepository).existsById(1L);
        verify(invoiceRepository).deleteById(1L);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionOccurDuringDeletingInvoice() {
        //given
        when(invoiceRepository.existsById(1L)).thenReturn(true);
        doThrow(new NoSuchElementException()).when(invoiceRepository).deleteById(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(1L));
        verify(invoiceRepository).existsById(1L);
        verify(invoiceRepository).deleteById(1L);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        //when
        Optional<Invoice> result = database.getById(1L);

        //then
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(invoiceRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceById() throws DatabaseOperationException {
        //when
        Optional<Invoice> invoice = database.getById(100L);

        //then
        assertTrue(invoice.isEmpty());
        verify(invoiceRepository).findById(100L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void getByIdMethodShouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionOccurDuringGettingInvoiceById() {
        //given
        doThrow(new NoSuchElementException()).when(invoiceRepository).findById(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(1L));
        verify(invoiceRepository).findById(1L);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceRepository.findOne(any(Example.class))).thenReturn(Optional.of(invoice));

        //when
        Optional<Invoice> result = database.getByNumber(invoice.getNumber());

        //then
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceByNumber() throws DatabaseOperationException {
        //when
        Optional<Invoice> invoice = database.getByNumber("not_existing_number");

        //then
        assertTrue(invoice.isEmpty());
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void getByNumberMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringGettingInvoiceByNumber() {
        //given
        doThrow(new NonTransientDataAccessException(""){}).when(invoiceRepository).findOne(any(Example.class));

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getByNumber("1/1/1"));
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void shouldReturnAllInvoices() throws DatabaseOperationException {
        //given
        List<Invoice> invoices = List.of(InvoiceGenerator.getRandomInvoice(), InvoiceGenerator.getRandomInvoice());
        when(invoiceRepository.findAll()).thenReturn(invoices);

        //when
        Collection<Invoice> result = database.getAll();

        //then
        assertEquals(invoices, result);
        verify(invoiceRepository).findAll();
    }

    @Test
    void getAllMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringGettingAllInvoices() {
        //given
        doThrow(new NonTransientDataAccessException(""){}).when(invoiceRepository).findAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getAll());
        verify(invoiceRepository).findAll();
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        //when
        when(invoiceRepository.existsById(1L)).thenReturn(true);

        //then
        assertTrue(database.exists(1L));
        verify(invoiceRepository).existsById(1L);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws DatabaseOperationException {
        //given
        when(invoiceRepository.existsById(100L)).thenReturn(false);

        //then
        assertFalse(database.exists(100L));
        verify(invoiceRepository).existsById(100L);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void existsMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringCheckingInvoiceExists() {
        //given
        doThrow(new NonTransientDataAccessException(""){}).when(invoiceRepository).existsById(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.exists(1L));
        verify(invoiceRepository).existsById(1L);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //given
        when(invoiceRepository.count()).thenReturn(10L);

        //when
        Long result = database.count();

        //then
        assertEquals(10L, result);
        verify(invoiceRepository).count();
    }

    @Test
    void countMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringGettingNumberOfInvoices() {
        //given
        doThrow(new NonTransientDataAccessException(""){}).when(invoiceRepository).count();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.count());
        verify(invoiceRepository).count();
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        //given
        doNothing().when(invoiceRepository).deleteAll();

        //when
        database.deleteAll();

        //then
        verify(invoiceRepository).deleteAll();
    }

    @Test
    void deleteAllMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringDeletingAllInvoices() {
        //given
        doThrow(new NonTransientDataAccessException(""){}).when(invoiceRepository).deleteAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteAll());
        verify(invoiceRepository).deleteAll();
    }
}
