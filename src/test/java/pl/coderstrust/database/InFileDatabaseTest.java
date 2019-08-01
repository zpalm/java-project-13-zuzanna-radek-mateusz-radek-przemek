package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private static String testPath = "src/test/resources/database/test.txt";
    private static Invoice invoiceInFile = InvoiceGenerator.getRandomInvoice();

    @BeforeAll
    static void beforeLoading() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @BeforeEach
    void setup() throws IOException {
        InFileDatabaseProperties inFileDatabasePropertiesTest = new InFileDatabaseProperties();
        inFileDatabasePropertiesTest.setPath(testPath);

        doReturn(true).when(fileHelper).exists(testPath);
        doReturn(false).when(fileHelper).isEmpty(testPath);
        doReturn(objectMapper.writeValueAsString(invoiceInFile)).when(fileHelper).readLastLine(testPath);

        inFileDatabase = new InFileDatabase(inFileDatabasePropertiesTest, objectMapper, fileHelper);

        verify(fileHelper).readLastLine(testPath);
        verify(fileHelper).exists(testPath);
        verify(fileHelper).isEmpty(testPath);
    }

    @Test
    void shouldSaveInvoiceWithNullId() throws DatabaseOperationException, IOException {
        Invoice invoiceToSave = InvoiceGenerator.getRandomInvoiceWithNullId();

        Invoice savedInvoice = inFileDatabase.save(invoiceToSave);

        verify(fileHelper).writeLine(testPath, objectMapper.writeValueAsString(savedInvoice));
        assertEquals(invoiceInFile.getId() + 1, savedInvoice.getId());
    }

    @Test
    void shouldSaveInvoiceWithSetId() throws DatabaseOperationException, IOException {
        Invoice invoiceToSave = InvoiceGenerator.getRandomInvoiceWithSpecificId(5L);

        Invoice savedInvoice = inFileDatabase.save(invoiceToSave);

        verify(fileHelper).writeLine(testPath, objectMapper.writeValueAsString(savedInvoice));
        assertEquals(invoiceToSave.getId(), savedInvoice.getId());
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException, IOException {
        Invoice invoiceToUpdate = InvoiceGenerator.getRandomInvoiceWithSpecificId(invoiceInFile.getId());
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);

        Invoice updatedInvoice = inFileDatabase.save(invoiceToUpdate);

        verify(fileHelper).replaceLine(testPath, objectMapper.writeValueAsString(updatedInvoice), 1);
        assertEquals(invoiceToUpdate.getId(), updatedInvoice.getId());
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.save(null));
    }

    @Test
    void shouldDeleteInvoiceFromFile() throws DatabaseOperationException, IOException {
        Invoice invoiceToDelete = invoiceInFile;
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);
        doNothing().when(fileHelper).removeLine(testPath, 1);

        inFileDatabase.delete(invoiceToDelete.getId());

        verify(fileHelper).removeLine(testPath, 1);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNonExistingInvoice() throws IOException {
        Invoice invoiceToDelete = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.delete(invoiceToDelete.getId()));
    }

    @Test
    void shouldGetInvoiceById() throws DatabaseOperationException, IOException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoiceWithSpecificId(5L);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile), objectMapper.writeValueAsString(invoiceToGet))).when(fileHelper).readLines(testPath);

        Optional<Invoice> optionalInvoice = inFileDatabase.getById(5L);

        verify(fileHelper).readLines(testPath);
        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoiceToGet, optionalInvoice.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoiceWithSpecificId(5L);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);

        Optional<Invoice> optionalInvoice = inFileDatabase.getById(invoiceToGet.getId());

        verify(fileHelper).readLines(testPath);
        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getById(null));
    }

    @Test
    void shouldGetInvoiceByNumber() throws DatabaseOperationException, IOException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoiceWithSpecificId(5L);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile), objectMapper.writeValueAsString(invoiceToGet))).when(fileHelper).readLines(testPath);

        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoiceToGet.getNumber());

        verify(fileHelper).readLines(testPath);
        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoiceToGet, optionalInvoice.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenGettingNonExistingInvoiceByNumber() throws IOException, DatabaseOperationException {
        Invoice invoiceToGet = InvoiceGenerator.getRandomInvoiceWithSpecificId(5L);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);

        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoiceToGet.getNumber());

        verify(fileHelper).readLines(testPath);
        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getByNumber(null));
    }

    @Test
    void shouldGetAllInvoices() throws IOException, DatabaseOperationException {
        Invoice additionalInvoice = InvoiceGenerator.getRandomInvoiceWithSpecificId(5L);
        List<Invoice> expected = Arrays.asList(invoiceInFile, additionalInvoice);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile), objectMapper.writeValueAsString(additionalInvoice))).when(fileHelper).readLines(testPath);

        Collection<Invoice> result = inFileDatabase.getAll();

        verify(fileHelper).readLines(testPath);
        assertEquals(expected, result);
    }

    @Test
    void shouldDeleteAllInvoices() throws IOException, DatabaseOperationException {
        doNothing().when(fileHelper).clear(testPath);

        inFileDatabase.deleteAll();

        verify(fileHelper).clear(testPath);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice existingInvoice = invoiceInFile;
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);

        boolean result = inFileDatabase.exists(existingInvoice.getId());

        verify(fileHelper).readLines(testPath);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice nonExistingInvoice = InvoiceGenerator.getRandomInvoiceWithSpecificId(5L);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);

        boolean result = inFileDatabase.exists(nonExistingInvoice.getId());

        verify(fileHelper).readLines(testPath);
        assertFalse(result);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.exists(null));
    }

    @Test
    void shouldCountInvoices() throws IOException, DatabaseOperationException {
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInFile))).when(fileHelper).readLines(testPath);

        long result = inFileDatabase.count();

        verify(fileHelper).readLines(testPath);
        assertEquals(1, result);
    }
}
