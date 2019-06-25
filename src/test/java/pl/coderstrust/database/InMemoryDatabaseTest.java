package pl.coderstrust.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

class InMemoryDatabaseTest {

    private InMemoryDatabase database;
    private Company seller;
    private Company buyer;
    private List<InvoiceEntry> entries;

    @BeforeEach
    void setup() {
        database = new InMemoryDatabase();
        seller = new Company(0L, "Seller", "Seller address", "000-000-00-00",
                "00-00-00", "0048600600600", "office@seller.com");
        buyer = new Company(0L, "Buyer", "Buyer address", "111-111-11-11",
                "11-11-11", "0048100100100", "office@buyer.com");
        entries = new ArrayList<>();
    }

    @Test
    void shouldSaveInvoiceToEmptyInMemoryDatabase() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice existingInvoice = new Invoice(1L, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);

        // when
        Invoice resultInvoice = database.save(firstInvoice);

        // then
        assertThat(resultInvoice).isEqualTo(existingInvoice);
    }

    @Test
    void shouldReturnNullWhileSavingInvoiceWithInvalidIdNumber() throws DatabaseOperationException {
        // given
        Invoice invoice = new Invoice(111L, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);

        //when
        Invoice resultInvoice = database.save(invoice);

        // then
        assertThat(resultInvoice).isEqualTo(null);
    }

    @Test
    void shouldSaveInvoiceToNonEmptyInMemoryDatabase() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice existingInvoice = new Invoice(2L, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);

        // when
        Invoice resultInvoice = database.save(secondInvoice);

        // then
        assertThat(resultInvoice).isEqualTo(existingInvoice);
    }

    @Test
    void shouldThrowExceptionWhileSaveNullAsPassedInvoice() {
        assertThatThrownBy(() -> database.save(null)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDeleteExistingInvoice() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        database.delete(2L);

        // then
        assertThat(database.count()).isEqualTo(2);
    }

    @Test
    void shouldDoNothingWhileDeleteNonExistingInvoice() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        database.delete(10L);

        // then
        assertThat(database.count()).isEqualTo(3);
    }

    @Test
    void shouldThrowExceptionForNullAsIdForDeleteInvoice() {
        assertThatThrownBy(() -> database.delete(null)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldGetExistingInvoiceById() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice existingInvoice = new Invoice(2L, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional<Invoice> optionalInvoice = Optional.of(existingInvoice);

        // when
        Optional optionalResultInvoice = database.getById(2L);

        // then
        assertThat(optionalResultInvoice).isEqualTo(optionalInvoice);
    }

    @Test
    void shouldReturnEmptyObjectWhileGetNonExistingInvoiceById() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional expectedEmptyObject = Optional.empty();

        // when
        Optional optionalResultInvoice = database.getById(5L);

        // then
        assertThat(optionalResultInvoice).isEqualTo(expectedEmptyObject);
    }

    @Test
    void shouldThrowExceptionForNullAsIdWhileGetInvoiceById() {
        assertThatThrownBy(() -> database.getById(null)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldGetExistingInvoiceByNumber() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice existingInvoice = new Invoice(2L, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional<Invoice> optionalInvoice = Optional.of(existingInvoice);

        // when
        Optional optionalResultInvoice = database.getByNumber("2/2019");

        // then
        assertThat(optionalResultInvoice).isEqualTo(optionalInvoice);
    }

    @Test
    void shouldReturnEmptyObjectWhileGetNonExistingInvoiceByNumber() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);
        Optional expectedEmptyObject = Optional.empty();

        // when
        Optional optionalResultInvoice = database.getByNumber("5/2019");

        // then
        assertThat(optionalResultInvoice).isEqualTo(expectedEmptyObject);
    }

    @Test
    void shouldThrowExceptionForNullAsNumberWhileGetInvoiceByNumber() {
        assertThatThrownBy(() -> database.getByNumber(null)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnCorrectCollection() throws DatabaseOperationException {
        // given
        Collection<Invoice> expectedCollection = new HashSet<>();
        Invoice expectedFirstInvoice = new Invoice(1L, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice expectedSecondInvoice = new Invoice(2L, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice expectedThirdInvoice = new Invoice(3L, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        expectedCollection.add(expectedFirstInvoice);
        expectedCollection.add(expectedSecondInvoice);
        expectedCollection.add(expectedThirdInvoice);
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        Collection<Invoice> resultCollection = database.getAll();

        // then
        assertThat(resultCollection).isEqualTo(expectedCollection);
    }

    @Test
    void shouldDeleteAllInvoicesInDatabase() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        database.deleteAll();

        // then
        assertThat(database.count()).isEqualTo(0);
        assertThat(database.getAll().size()).isEqualTo(0);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        boolean result = database.exists(2L);

        // then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
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
        assertThatThrownBy(() -> database.exists(null)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnCorrectDatabaseSize() throws DatabaseOperationException {
        // given
        Invoice firstInvoice = new Invoice(null, "1/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice secondInvoice = new Invoice(null, "2/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        Invoice thirdInvoice = new Invoice(null, "3/2019", LocalDate.of(2019, 6, 24),
                LocalDate.of(2019, 7, 1), seller, buyer, entries);
        database.save(firstInvoice);
        database.save(secondInvoice);
        database.save(thirdInvoice);

        // when
        long result = database.count();

        // then
        assertThat(result).isEqualTo(3L);
    }
}
