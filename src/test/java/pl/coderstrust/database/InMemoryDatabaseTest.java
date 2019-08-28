package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableMap;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

class InMemoryDatabaseTest {

    private Map<Long, Invoice> storage;
    private InMemoryDatabase database;

    @BeforeEach
    void setup() {
        storage = new HashMap<>();
        database = new InMemoryDatabase(storage);
    }

    @Test
    void constructorClassShouldThrowExceptionForNullStorage() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryDatabase(null));
    }

    @Test
    void shouldAddInvoice() {
        Invoice addedInvoice = database.save(InvoiceGenerator.getRandomInvoice());

        assertNotNull(addedInvoice.getId());
        assertEquals(1, (long) addedInvoice.getId());
        assertEquals(storage.get(addedInvoice.getId()), addedInvoice);
    }

    @Test
    void shouldAddInvoiceWithNullId() {
        Invoice addedInvoice = database.save(InvoiceGenerator.getRandomInvoiceWithNullId());

        assertNotNull(addedInvoice.getId());
        assertEquals(1, (long) addedInvoice.getId());
        assertEquals(storage.get(addedInvoice.getId()), addedInvoice);
    }

    @Test
    void shouldUpdate() {
        Invoice invoiceInDatabase = InvoiceGenerator.getRandomInvoice();
        Invoice invoiceToUpdate = InvoiceGenerator.getRandomInvoiceWithSpecificId(invoiceInDatabase.getId());
        storage.put(invoiceInDatabase.getId(), invoiceInDatabase);

        Invoice updatedInvoice = database.save(invoiceToUpdate);

        assertEquals(storage.get(invoiceInDatabase.getId()), updatedInvoice);
    }

    @Test
    void saveInvoiceMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);
        Map<Long, Invoice> expected = ImmutableMap.of(invoice2.getId(), invoice2);

        database.delete(invoice1.getId());

        assertEquals(expected, storage);
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionDuringDeletingNotExistingInvoice() {
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
    }

    @Test
    void shouldReturnInvoiceById() {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);

        Optional<Invoice> optionalInvoice = database.getById(invoice1.getId());

        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoice1, optionalInvoice.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhileGetNonExistingInvoiceById() {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);

        Optional<Invoice> optionalInvoice = database.getById(invoice2.getId());

        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldReturnInvoiceByNumber() {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);

        Optional<Invoice> optionalInvoice = database.getByNumber(invoice1.getNumber());

        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoice1, optionalInvoice.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhileGetNonExistingInvoiceByNumber() {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);

        Optional<Invoice> optionalInvoice = database.getByNumber(invoice2.getNumber());

        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void shouldReturnAllInvoices() {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);

        Collection<Invoice> invoices = database.getAll();

        assertEquals(storage.values(), invoices);
    }

    @Test
    void shouldDeleteAllInvoices() {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);

        database.deleteAll();

        assertEquals(new HashMap<>(), storage);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice.getId(), invoice);

        assertTrue(database.exists(invoice.getId()));
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice.getId(), invoice);

        assertFalse(database.exists(invoice.getId() + 1L));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void shouldReturnNumberOfInvoices() {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice3 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice4 = InvoiceGenerator.getRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);
        storage.put(invoice3.getId(), invoice3);
        storage.put(invoice4.getId(), invoice4);

        long result = database.count();

        assertEquals(4, result);
    }

    @Test
    void shouldReturnInvoicesFilteredByIssueDate() throws DatabaseOperationException {
        Invoice invoice1 = Invoice.builder().withId(1L).withIssuedDate(LocalDate.of(2019, 8, 24)).build();
        Invoice invoice2 = Invoice.builder().withId(2L).withIssuedDate(LocalDate.of(2019, 8, 25)).build();
        Invoice invoice3 = Invoice.builder().withId(3L).withIssuedDate(LocalDate.of(2019, 8, 26)).build();
        Invoice invoice4 = Invoice.builder().withId(4L).withIssuedDate(LocalDate.of(2019, 8, 27)).build();

        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);
        storage.put(invoice3.getId(), invoice3);
        storage.put(invoice4.getId(), invoice4);

        LocalDate startDate = LocalDate.of(2019, 8, 24);
        LocalDate endDate = LocalDate.of(2019, 8, 26);

        List<Invoice> expected = Arrays.asList(invoice1, invoice2, invoice3);
        Collection<Invoice> result = database.getByIssueDate(startDate, endDate);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @MethodSource("invalidIssuedDateArgumentsAndExceptionMessages")
    void getByIssuedDateShouldThrowExceptionWhenInvalidArgumentsArePassed(LocalDate startDate, LocalDate endDate, String message) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> database.getByIssueDate(startDate, endDate));
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
}
