package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.generators.WordGenerator;
import pl.coderstrust.model.Invoice;

class InMemoryDatabaseTest {

    private InMemoryDatabase database;

    @BeforeEach
    void setup() {
        Map<Long, Invoice> storage = new HashMap<>();
        database = new InMemoryDatabase(storage);
    }

    @Test
    void shouldSaveInvoiceToEmptyInMemoryDatabase() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice existingInvoice = new Invoice(1L, firstInvoice.getNumber(), firstInvoice.getIssuedDate(),
                firstInvoice.getDueDate(), firstInvoice.getSeller(), firstInvoice.getBuyer(), firstInvoice.getEntries());

        // when
        Invoice resultInvoice = database.save(firstInvoice);

        // then
        assertEquals(existingInvoice, resultInvoice);
    }

    @Test
    void shouldSaveInvoiceToNonEmptyInMemoryDatabase() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice existingInvoice = new Invoice(2L, secondInvoice.getNumber(), secondInvoice.getIssuedDate(),
                secondInvoice.getDueDate(), secondInvoice.getSeller(), secondInvoice.getBuyer(), secondInvoice.getEntries());
        database.save(firstInvoice);

        // when
        Invoice resultInvoice = database.save(secondInvoice);

        // then
        assertEquals(existingInvoice, resultInvoice);
    }

    @Test
    void shouldSaveInvoiceToInMemoryDatabaseWithProvidedButNonExistingId() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        Invoice nonExistingInvoice = InvoiceGenerator.getRandomInvoice();
        Invoice expectingInvoice = new Invoice(3L, nonExistingInvoice.getNumber(), nonExistingInvoice.getIssuedDate(),
                nonExistingInvoice.getDueDate(), nonExistingInvoice.getSeller(), nonExistingInvoice.getBuyer(), nonExistingInvoice.getEntries());

        // when
        Invoice resultInvoice = database.save(nonExistingInvoice);

        // then
        assertEquals(expectingInvoice, resultInvoice);
    }

    @Test
    void shouldUpdateExistingInvoice() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        Invoice newSecondInvoice = new Invoice(2L, secondInvoice.getNumber(), secondInvoice.getIssuedDate(),
                secondInvoice.getDueDate(), secondInvoice.getSeller(), secondInvoice.getBuyer(), secondInvoice.getEntries());

        // when
        Invoice resultInvoice = database.save(newSecondInvoice);

        // then
        assertEquals(newSecondInvoice, resultInvoice);
        assertEquals(Optional.of(newSecondInvoice), database.getById(2L));
    }

    @Test
    void shouldThrowExceptionWhileSaveNullAsPassedInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void shouldDeleteExistingInvoice() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        database.delete(2L);

        // then
        assertEquals(2, database.count());
    }

    @Test
    void shouldThrowExceptionForNullAsIdForDeleteInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void shouldThrowExceptionWhileDeleteNonExistingInvoice() {
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
    }

    @Test
    void shouldGetExistingInvoiceById() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice existingInvoice = new Invoice(2L, secondInvoice.getNumber(), secondInvoice.getIssuedDate(),
                secondInvoice.getDueDate(), secondInvoice.getSeller(), secondInvoice.getBuyer(), secondInvoice.getEntries());
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional<Invoice> optionalInvoice = Optional.of(existingInvoice);

        // when
        Optional optionalResultInvoice = database.getById(2L);

        // then
        assertEquals(optionalInvoice, optionalResultInvoice);
    }

    @Test
    void shouldReturnEmptyObjectWhileGetNonExistingInvoiceById() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional expectedEmptyObject = Optional.empty();

        // when
        Optional optionalResultInvoice = database.getById(5L);

        // then
        assertEquals(expectedEmptyObject, optionalResultInvoice);
    }

    @Test
    void shouldThrowExceptionForNullAsIdWhileGetInvoiceById() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldGetExistingInvoiceByNumber() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice existingInvoice = new Invoice(2L, secondInvoice.getNumber(), secondInvoice.getIssuedDate(),
                secondInvoice.getDueDate(), secondInvoice.getSeller(), secondInvoice.getBuyer(), secondInvoice.getEntries());
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional<Invoice> optionalInvoice = Optional.of(existingInvoice);

        // when
        Optional optionalResultInvoice = database.getByNumber(secondInvoice.getNumber());

        // then
        assertEquals(optionalInvoice, optionalResultInvoice);
    }

    @Test
    void shouldReturnEmptyObjectWhileGetNonExistingInvoiceByNumber() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional expectedEmptyObject = Optional.empty();

        // when
        Optional optionalResultInvoice = database.getByNumber(WordGenerator.getRandomWord());

        // then
        assertEquals(expectedEmptyObject, optionalResultInvoice);
    }

    @Test
    void shouldThrowExceptionForNullAsNumberWhileGetInvoiceByNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void shouldReturnCorrectCollection() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Collection<Invoice> expectedCollection = new HashSet<>();
        Invoice expectedFirstInvoice = new Invoice(1L, firstInvoice.getNumber(), firstInvoice.getIssuedDate(),
                firstInvoice.getDueDate(), firstInvoice.getSeller(), firstInvoice.getBuyer(), firstInvoice.getEntries());
        Invoice expectedSecondInvoice = new Invoice(2L, secondInvoice.getNumber(), secondInvoice.getIssuedDate(),
                secondInvoice.getDueDate(), secondInvoice.getSeller(), secondInvoice.getBuyer(), secondInvoice.getEntries());
        Invoice expectedThirdInvoice = new Invoice(3L, thirdInvoice.getNumber(), thirdInvoice.getIssuedDate(),
                thirdInvoice.getDueDate(), thirdInvoice.getSeller(), thirdInvoice.getBuyer(), thirdInvoice.getEntries());
        expectedCollection.add(expectedFirstInvoice);
        expectedCollection.add(expectedSecondInvoice);
        expectedCollection.add(expectedThirdInvoice);

        // when
        Collection<Invoice> resultCollection = new HashSet<>(database.getAll());

        // then
        assertEquals(resultCollection, expectedCollection);
    }

    @Test
    void shouldDeleteAllInvoicesInDatabase() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        database.deleteAll();

        // then
        assertEquals(0, database.count());
        assertEquals(0, database.getAll().size());
    }

    @Test
    void shouldReturnTrueForExistingInvoice() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        boolean result = database.exists(2L);

        // then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        boolean result = database.exists(5L);

        // then
        assertFalse(result);
    }

    @Test
    void shouldThrowExceptionForNullAsIdWhileCheckInvoiceExistsInDatabase() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void shouldReturnCorrectDatabaseSize() {
        // given
        Invoice firstInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice secondInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        Invoice thirdInvoice = InvoiceGenerator.getRandomInvoiceWithNullId();
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        long result = database.count();

        // then
        assertEquals(3L, result);
    }
}
