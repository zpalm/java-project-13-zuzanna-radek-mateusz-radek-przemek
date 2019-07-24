package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@SpringBootTest
class InFileDatabaseIT {

    @Autowired
    InFileDatabase inFileDatabase;

    @Autowired
    ObjectMapper mapper;

    private static File expectedFile;
    private static File inputFile;

    @BeforeEach
    void beforeEach() {
        expectedFile = new File("src/test/resources/database/expected_in_file_database.txt");
        inputFile = new File("src/test/resources/database/in_file_database.txt");
        if (expectedFile.exists()) {
            expectedFile.delete();
        }
        if (inputFile.exists()) {
            inputFile.delete();
        }
    }

    @AfterAll
    static void cleanUp() {
        expectedFile.delete();
        inputFile.delete();
    }

    @Test
    void shouldSaveInvoiceWithNullId() throws DatabaseOperationException, IOException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithNullId();
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(expectedFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(expectedFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2).replaceAll("\"invoiceId\":null", "\"invoiceId\":2")), true);
        inFileDatabase.save(invoice2);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldSaveInvoiceWithSetId() throws DatabaseOperationException, IOException {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        String invoiceString = mapper.writeValueAsString(invoice);
        FileUtils.writeLines(expectedFile, "UTF-8", Collections.singleton(invoiceString), true);
        inFileDatabase.save(invoice);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException, IOException {
        Invoice invoice = InvoiceGenerator.getRandomInvoice();
        Invoice updatedInvoice = InvoiceGenerator.getRandomInvoiceWithSpecificId(invoice.getId());
        FileUtils.writeLines(expectedFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(updatedInvoice)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice)), true);
        inFileDatabase.save(updatedInvoice);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.save(null));
    }

    @Test
    void saveMethodShouldThrowExceptionWhen() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getByNumber(null));
    }

    @Test
    void shouldDeleteInvoiceFromFile() throws DatabaseOperationException, IOException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        FileUtils.writeLines(expectedFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        inFileDatabase.delete(1L);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNonExistingInvoice() throws IOException {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice)), true);
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.delete(2L));
    }

    @Test
    void shouldGetInvoiceById() throws DatabaseOperationException, IOException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        Optional<Invoice> optionalInvoice = inFileDatabase.getById(1L);
        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoice1, optionalInvoice.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        Optional<Invoice> optionalInvoice = inFileDatabase.getById(2L);
        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getById(null));
    }

    @Test
    void shouldGetInvoiceByNumber() throws DatabaseOperationException, IOException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoice1.getNumber());
        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoice1, optionalInvoice.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenGettingNonExistingInvoiceByNumber() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber("test");
        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getByNumber(null));
    }

    @Test
    void shouldGetAllInvoices() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        List<Invoice> expected = Arrays.asList(invoice1, invoice2);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        Collection<Invoice> result = inFileDatabase.getAll();
        assertEquals(expected, result);
    }

    @Test
    void shouldDeleteAllInvoices() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        inFileDatabase.deleteAll();
        assertEquals(0, inputFile.length());
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        assertTrue(inFileDatabase.exists(1L));
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        assertFalse(inFileDatabase.exists(3L));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.exists(null));
    }

    @Test
    void shouldCountInvoices() throws IOException, DatabaseOperationException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice1)), true);
        FileUtils.writeLines(inputFile, "UTF-8", Collections.singleton(mapper.writeValueAsString(invoice2)), true);
        assertEquals(2, inFileDatabase.count());
    }

}