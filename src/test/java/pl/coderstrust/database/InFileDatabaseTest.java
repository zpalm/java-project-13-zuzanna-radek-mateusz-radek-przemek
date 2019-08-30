package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.configuration.ApplicationConfiguration;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class InFileDatabaseTest {

    @Mock
    private FileHelper fileHelper;

    private InFileDatabase inFileDatabase;

    private static ObjectMapper objectMapper;

    private static final String DATABASE_FILE = "invoice_database.txt";

    @BeforeEach
    void setup() throws IOException {
        objectMapper = new ApplicationConfiguration().getObjectMapper();
        InFileDatabaseProperties inFileDatabasePropertiesTest = new InFileDatabaseProperties();
        inFileDatabasePropertiesTest.setFilePath(DATABASE_FILE);

        doReturn(false).when(fileHelper).exists(DATABASE_FILE);

        inFileDatabase = new InFileDatabase(inFileDatabasePropertiesTest, objectMapper, fileHelper);
    }

    @Test
    void shouldAddInvoice() throws DatabaseOperationException, IOException {
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoice();
        Invoice expectedInvoice = Invoice.builder()
            .withId(1L)
            .withNumber(invoiceToAdd.getNumber())
            .withIssuedDate(invoiceToAdd.getIssuedDate())
            .withDueDate(invoiceToAdd.getDueDate())
            .withSeller(invoiceToAdd.getSeller())
            .withBuyer(invoiceToAdd.getBuyer())
            .withEntries(invoiceToAdd.getEntries())
            .build();
        List<String> invoicesInDatabase = new ArrayList<>();
        invoicesInDatabase.add(objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoice()));
        invoicesInDatabase.add(objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoice()));
        doNothing().when(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(expectedInvoice));
        doReturn(invoicesInDatabase).when(fileHelper).readLines(DATABASE_FILE);

        Invoice addedInvoice = inFileDatabase.save(invoiceToAdd);

        assertEquals(expectedInvoice, addedInvoice);
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(addedInvoice));
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException, IOException {
        Invoice invoiceInDatabase = InvoiceGenerator.getRandomInvoice();
        Invoice invoiceToUpdate = Invoice.builder()
            .withId(invoiceInDatabase.getId())
            .withNumber("123/456")
            .withIssuedDate(invoiceInDatabase.getIssuedDate())
            .withDueDate(invoiceInDatabase.getDueDate())
            .withSeller(invoiceInDatabase.getSeller())
            .withBuyer(invoiceInDatabase.getBuyer())
            .withEntries(invoiceInDatabase.getEntries())
            .build();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInDatabase))).when(fileHelper).readLines(DATABASE_FILE);
        doNothing().when(fileHelper).replaceLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToUpdate), 1);

        Invoice updatedInvoice = inFileDatabase.save(invoiceToUpdate);

        assertEquals(invoiceToUpdate, updatedInvoice);
        verify(fileHelper).replaceLine(DATABASE_FILE, objectMapper.writeValueAsString(updatedInvoice), 1);
        verify(fileHelper, times(2)).readLines(DATABASE_FILE);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.save(null));
    }

    @Test
    void saveMethodShouldThrowExceptionWhenFileHelpersWriteLineMethodThrowsException() throws IOException {
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoice();
        Invoice expectedInvoice = Invoice.builder()
            .withId(1L)
            .withNumber(invoiceToAdd.getNumber())
            .withIssuedDate(invoiceToAdd.getIssuedDate())
            .withDueDate(invoiceToAdd.getDueDate())
            .withSeller(invoiceToAdd.getSeller())
            .withBuyer(invoiceToAdd.getBuyer())
            .withEntries(invoiceToAdd.getEntries())
            .build();
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(expectedInvoice));

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.save(invoiceToAdd));
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(expectedInvoice));
    }

    @Test
    void saveMethodShouldThrowExceptionWhenFileHelpersReplaceLineMethodThrowsException() throws IOException {
        Invoice invoiceInDatabase = InvoiceGenerator.getRandomInvoice();
        Invoice invoiceToUpdate = Invoice.builder()
            .withId(invoiceInDatabase.getId())
            .withNumber("123/456")
            .withIssuedDate(invoiceInDatabase.getIssuedDate())
            .withDueDate(invoiceInDatabase.getDueDate())
            .withSeller(invoiceInDatabase.getSeller())
            .withBuyer(invoiceInDatabase.getBuyer())
            .withEntries(invoiceInDatabase.getEntries())
            .build();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInDatabase))).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).replaceLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToUpdate), 1);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.save(invoiceToUpdate));
        verify(fileHelper, times(2)).readLines(DATABASE_FILE);
        verify(fileHelper).replaceLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToUpdate), 1);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException, IOException {
        Invoice invoiceToDelete = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToDelete))).when(fileHelper).readLines(DATABASE_FILE);
        doNothing().when(fileHelper).removeLine(DATABASE_FILE, 1);

        inFileDatabase.delete(invoiceToDelete.getId());

        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).removeLine(DATABASE_FILE, 1);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNonExistingInvoice() throws IOException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.delete(invoice.getId() + 1L));
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper, never()).removeLine(anyString(), anyInt());
    }

    @Test
    void deleteMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        Invoice invoiceToDelete = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToDelete))).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).removeLine(DATABASE_FILE, 1);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.delete(invoiceToDelete.getId()));
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).removeLine(DATABASE_FILE, 1);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException, IOException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoice()), objectMapper.writeValueAsString(invoiceToGet))).when(fileHelper).readLines(DATABASE_FILE);

        Optional<Invoice> optionalInvoice = inFileDatabase.getById(invoiceToGet.getId());

        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoiceToGet, optionalInvoice.get());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnEmptyOptionalWhenGettingNonExistingInvoiceById() throws IOException, DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        Optional<Invoice> optionalInvoice = inFileDatabase.getById(invoice.getId() + 1L);

        assertTrue(optionalInvoice.isEmpty());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getById(null));
    }

    @Test
    void getByIdMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoice();
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.getById(invoiceToGet.getId()));
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException, IOException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoice()), objectMapper.writeValueAsString(invoiceToGet))).when(fileHelper).readLines(DATABASE_FILE);

        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoiceToGet.getNumber());

        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoiceToGet, optionalInvoice.get());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnEmptyOptionalWhenGettingNonExistingInvoiceByNumber() throws IOException, DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoice.getNumber() + "sd");

        assertTrue(optionalInvoice.isEmpty());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getByNumber(null));
    }

    @Test
    void getByNumberMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoice();
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.getByNumber(invoiceToGet.getNumber()));
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnAllInvoices() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        List<Invoice> expected = Arrays.asList(invoice1, invoice2);
        doReturn(List.of(objectMapper.writeValueAsString(invoice1), objectMapper.writeValueAsString(invoice2))).when(fileHelper).readLines(DATABASE_FILE);

        Collection<Invoice> result = inFileDatabase.getAll();

        assertEquals(expected, result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getAllMethodShouldReturnEmptyListWhenDatabaseIsEmpty() throws IOException, DatabaseOperationException {
        doReturn(Collections.emptyList()).when(fileHelper).readLines(DATABASE_FILE);

        Collection<Invoice> result = inFileDatabase.getAll();

        assertEquals(Collections.emptyList(), result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getAllMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.getAll());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldDeleteAllInvoices() throws IOException, DatabaseOperationException {
        doNothing().when(fileHelper).clear(DATABASE_FILE);

        inFileDatabase.deleteAll();

        verify(fileHelper).clear(DATABASE_FILE);
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        doThrow(IOException.class).when(fileHelper).clear(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.deleteAll());
        verify(fileHelper).clear(DATABASE_FILE);
    }

    @Test
    void shouldReturnTrueWhenInvoiceExists() throws IOException, DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        boolean result = inFileDatabase.exists(invoice.getId());

        assertTrue(result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnFalseWhenInvoiceDoesNotExist() throws IOException, DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        boolean result = inFileDatabase.exists(invoice.getId() + 1L);

        assertFalse(result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.exists(null));
    }

    @Test
    void existsMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.exists(invoice.getId()));
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws IOException, DatabaseOperationException {
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);

        long result = inFileDatabase.count();

        assertEquals(2, result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void countMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.count());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnInvoicesByIssuedDate() throws IOException, DatabaseOperationException {
        LocalDate startDate = LocalDate.of(2019, 8, 26);
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.plusDays(1L));
        Invoice invoice3 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.plusDays(2L));
        Invoice invoice4 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.plusDays(3L));
        Invoice invoice5 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.minusDays(1L));


        List<Invoice> expected = Arrays.asList(invoice1, invoice2, invoice3);
        doReturn(List.of(objectMapper.writeValueAsString(invoice1), objectMapper.writeValueAsString(invoice2), objectMapper.writeValueAsString(invoice3), objectMapper.writeValueAsString(invoice4), objectMapper.writeValueAsString(invoice5))).when(fileHelper).readLines(DATABASE_FILE);

        Collection<Invoice> result = inFileDatabase.getByIssueDate(startDate, startDate.plusDays(2L));

        assertEquals(expected, result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @ParameterizedTest
    @MethodSource("invalidIssuedDateArgumentsAndExceptionMessages")
    void getByIssueDateMethodShouldThrowExceptionWhenArgumentsAreInvalid(LocalDate startDate, LocalDate endDate, String message) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getByIssueDate(startDate, endDate));
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
    void getByIssueDateMethodShouldThrowExceptionWhenFileHelperThrowsIoException() throws IOException {
        LocalDate startDate = LocalDate.now();
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> inFileDatabase.getByIssueDate(startDate, startDate.plusDays(2L)));
        assertEquals("An error occurred during getting invoices filtered by issued date", exception.getMessage());

        verify(fileHelper).readLines(DATABASE_FILE);
    }
}
