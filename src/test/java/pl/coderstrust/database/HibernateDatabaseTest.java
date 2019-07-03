package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
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
    void shouldAddInvoice() {
        //given
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice addedInvoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceRepository.save(invoiceToAdd)).thenReturn(addedInvoice);

        //when
        Invoice result = database.save(invoiceToAdd);

        //then
        assertEquals(addedInvoice, result);
        verify(invoiceRepository).save(invoiceToAdd);
    }

    @Test
    void shouldUpdate() {
        //given
        Invoice invoiceToUpdate = InvoiceGenerator.getRandomInvoice();
        Invoice invoiceUpdated = InvoiceGenerator.getRandomInvoice();
        when(invoiceRepository.save(invoiceToUpdate)).thenReturn(invoiceUpdated);

        //when
        Invoice result = database.save(invoiceToUpdate);

        //then
        assertEquals(invoiceUpdated, result);
        verify(invoiceRepository).save(invoiceToUpdate);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
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
        //when
        when(invoiceRepository.existsById(100L)).thenReturn(false);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(100L));
        verify(invoiceRepository).existsById(100L);
    }

    @Test
    void shouldReturnInvoiceById() {
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
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldReturnInvoiceByNumber() {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        //when
        Optional<Invoice> result = database.getByNumber("1/1/1");

        //then
        verify(invoiceRepository).findAll();
    }

    @Test
    void shouldReturnEmptyOptionalWhileGetNonExistingInvoiceByNumber() {
        //when
        Optional<Invoice> invoice = database.getByNumber("not_existing_number");

        //then
        assertTrue(invoice.isEmpty());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void shouldReturnAllInvoices() {
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
    void shouldReturnTrueForExistingInvoice() {
        //when
        when(invoiceRepository.existsById(1L)).thenReturn(true);

        //then
        assertTrue(database.exists(1L));
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() {
        assertFalse(database.exists(100L));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void shouldReturnNumberOfInvoices() {
        //given
        when(invoiceRepository.count()).thenReturn(10L);

        //when
        Long result = database.count();

        //then
        assertEquals(10L, result);
        verify(invoiceRepository).count();
    }

    @Test
    void shouldDeleteAllInvoices() {
        //given
        doNothing().when(invoiceRepository).deleteAll();

        //when
        database.deleteAll();

        //then
        verify(invoiceRepository).deleteAll();
    }
}
