package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import pl.coderstrust.database.sql.model.SqlModelMapper;
import pl.coderstrust.database.sql.model.SqlModelMapperImpl;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-sql-test.properties")
class SqlDatabaseIT {

    private static JdbcTemplate jdbcTemplate = new JdbcTemplate();
    private SqlModelMapper sqlModelMapper = new SqlModelMapperImpl();
    private SqlDatabase database = new SqlDatabase(jdbcTemplate, sqlModelMapper);

//    @Bean
//    @ConfigurationProperties("application-sql-test.properties")
//    public DataSource dataSource() {
//        return DataSourceBuilder.create().build();
//    }

    @BeforeAll
    static void createSqlDatabase() {
        jdbcTemplate.execute(dropDatabaseIfExists());
        jdbcTemplate.execute(createDatabase());
        jdbcTemplate.execute(createTableCompany());
        jdbcTemplate.execute(createTableInvoice());
        jdbcTemplate.execute(alterTableInvoice());
        jdbcTemplate.execute(createTableInvoiceEntry());
        jdbcTemplate.execute(createTableInvoiceEntries());
    }

    private static String dropDatabaseIfExists() {
        return "DROP DATABASE IF EXISTS invoices";
    }

    private static String createDatabase() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("CREATE DATABASE invoices1 ")
            .append("WITH ")
            .append("OWNER = postgres ")
            .append("ENCODING = 'UTF8' ")
            .append("LC_COLLATE = 'Polish_Poland.1250' ")
            .append("LC_CTYPE = 'Polish_Poland.1250' ")
            .append("TABLESPACE = pg_default ")
            .append("CONNECTION LIMIT = -1");
        return stringBuilder.toString();
    }

    private static String createTableCompany() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("CREATE TABLE company ")
            .append("(")
            .append("id BIGSERIAL, ")
            .append("name VARCHAR(255), ")
            .append("address VARCHAR(255), ")
            .append("tax_id VARCHAR(255), ")
            .append("account_number VARCHAR(255), ")
            .append("phone_number VARCHAR(255), ")
            .append("email VARCHAR(255), ")
            .append("CONSTRAINT company_pkey PRIMARY KEY (id)")
            .append(")");
        return stringBuilder.toString();
    }

    private static String createTableInvoice () {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("CREATE TABLE invoice ")
            .append("(")
            .append("id BIGSERIAL, ")
            .append("number VARCHAR(255), ")
            .append("issued_date DATE, ")
            .append("due_date DATE, ")
            .append("seller_id bigint, ")
            .append("buyer_id bigint, ")
            .append("CONSTRAINT invoice_pkey PRIMARY KEY (id), ")
            .append("FOREIGN KEY (seller_id) REFERENCES company(id), ")
            .append("FOREIGN KEY (buyer_id) REFERENCES company(id)")
            .append(")");
        return stringBuilder.toString();
    }

    private static String alterTableInvoice() {
        return "ALTER TABLE invoice OWNER TO postgres";
    }

    private static String createTableInvoiceEntry() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("CREATE TABLE invoice_entry ")
            .append("(")
            .append("id BIGSERIAL, ")
            .append("description VARCHAR(255), ")
            .append("quantity bigint, ")
            .append("price numeric(19, 2), ")
            .append("net_value numeric(19, 2), ")
            .append("gross_value numeric(19, 2), ")
            .append("vat_rate int, ")
            .append("CONSTRAINT invoice_entry_pkey PRIMARY KEY (id)")
            .append(")");
        return stringBuilder.toString();
    }

    private static String createTableInvoiceEntries() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("CREATE TABLE invoice_entries ")
            .append("(")
            .append("invoice_id bigint, ")
            .append("entries_id bigint, ")
            .append("FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE CASCADE ON UPDATE CASCADE, ")
            .append("FOREIGN KEY (entries_id) REFERENCES invoice_entry(id) ON DELETE CASCADE ON UPDATE CASCADE")
            .append(")");
        return stringBuilder.toString();
    }

    @BeforeEach
    void deleteAllDataFromDatabase() {
        jdbcTemplate.execute("DELETE FROM invoice_entries");
        jdbcTemplate.execute("DELETE FROM invoice_entry");
        jdbcTemplate.execute("DELETE FROM invoice");
        jdbcTemplate.execute("DELETE FROM company");
    }

    @Test
    void shouldSaveInvoice() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();

        //when
        pl.coderstrust.model.Invoice savedInvoice = database.save(invoiceToSave);

        //then
        assertEquals(invoiceToSave.getNumber(), savedInvoice.getNumber());
        assertEquals(invoiceToSave.getIssuedDate(), savedInvoice.getIssuedDate());
        assertEquals(invoiceToSave.getDueDate(), savedInvoice.getDueDate());
        assertEquals(invoiceToSave.getSeller(), savedInvoice.getSeller());
        assertEquals(invoiceToSave.getBuyer(), savedInvoice.getBuyer());
        assertEquals(invoiceToSave.getEntries(), savedInvoice.getEntries());
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice = database.save(invoiceToSave);
        Long savedInvoiceId = savedInvoice.getId();
        pl.coderstrust.model.Invoice temporaryInvoice = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice invoiceToUpdate = Invoice.builder()
            .withId(savedInvoiceId)
            .withNumber(temporaryInvoice.getNumber())
            .withIssuedDate(temporaryInvoice.getIssuedDate())
            .withDueDate(temporaryInvoice.getDueDate())
            .withSeller(temporaryInvoice.getSeller())
            .withBuyer(temporaryInvoice.getBuyer())
            .withEntries(temporaryInvoice.getEntries())
            .build();

        //when
        pl.coderstrust.model.Invoice updatedInvoice = database.save(invoiceToUpdate);

        //then
        assertEquals(invoiceToUpdate, updatedInvoice);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccursDuringSavingInvoice() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoice = InvoiceGenerator.getRandomInvoice();
        doThrow(new NonTransientDataAccessException("") {}).when(database).save(invoice);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
        verify(database).save(invoice);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice = database.save(invoiceToSave);
        Long savedInvoiceId = savedInvoice.getId();

        //when
        database.delete(savedInvoiceId);
        boolean result = database.exists(savedInvoiceId);

        //then
        assertFalse(result);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingInvoice() {
        assertThrows(DatabaseOperationException.class, () -> database.delete(100L));
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccursDuringDeletingInvoice() throws DatabaseOperationException {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(database).delete(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(1L));
        verify(database).delete(1L);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionOccurDuringDeletingInvoice() throws DatabaseOperationException {
        //given
        doThrow(new NoSuchElementException()).when(database).delete(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(1L));
        verify(database).delete(1L);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice = database.save(invoiceToSave);
        Long savedInvoiceId = savedInvoice.getId();

        //when
        Optional<pl.coderstrust.model.Invoice> result = database.getById(savedInvoiceId);

        //then
        assertTrue(result.isPresent());
        assertEquals(savedInvoice, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceById() throws DatabaseOperationException {
        //when
        Optional<pl.coderstrust.model.Invoice> result = database.getById(100L);

        //then
        assertTrue(result.isEmpty());
        verify(database).getById(100L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void getByIdMethodShouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionOccursDuringGettingInvoiceById() throws DatabaseOperationException {
        //given
        doThrow(new NoSuchElementException()).when(database).getById(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(1L));
        verify(database).getById(1L);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice = database.save(invoiceToSave);
        String savedInvoiceNumber = savedInvoice.getNumber();

        //when
        Optional<pl.coderstrust.model.Invoice> result = database.getByNumber(savedInvoiceNumber);

        //then
        assertTrue(result.isPresent());
        assertEquals(savedInvoice, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceByNumber() throws DatabaseOperationException {
        //when
        Optional<pl.coderstrust.model.Invoice> result = database.getByNumber("1");

        //then
        assertTrue(result.isEmpty());
        verify(database).getByNumber("1");
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void getByNumberMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccursDuringGettingInvoiceByNumber() throws DatabaseOperationException {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(database).getByNumber("1");

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getByNumber("1"));
        verify(database).getByNumber("1");
    }

    @Test
    void shouldReturnAllInvoices() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoice1 = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice1 = database.save(invoice1);
        pl.coderstrust.model.Invoice invoice2 = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice2 = database.save(invoice2);
        List<pl.coderstrust.model.Invoice> invoices = List.of(savedInvoice1, savedInvoice2);

        //when
        Collection<pl.coderstrust.model.Invoice> result = database.getAll();

        //then
        assertEquals(invoices, result);
    }

    @Test
    void getAllMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccursDuringGettingAllInvoices() throws DatabaseOperationException {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(database).getAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getAll());
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        //given
        pl.coderstrust.model.Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice = database.save(invoiceToSave);
        Long savedInvoiceId = savedInvoice.getId();

        //when
        boolean result = database.exists(savedInvoiceId);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws DatabaseOperationException {
        //when
        boolean result = database.exists(1L);

        //then
        assertFalse(result);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void existsMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccursDuringCheckingInvoiceExists() throws DatabaseOperationException {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(database).exists(1L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.exists(1L));
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //given
        database.save(InvoiceGenerator.getRandomInvoice());
        database.save(InvoiceGenerator.getRandomInvoice());

        //when
        Long result = database.count();

        //then
        assertEquals(2L, result);
    }

    @Test
    void countMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccursDuringGettingNumberOfInvoices() throws DatabaseOperationException {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(database).count();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.count());
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        //given
        database.save(InvoiceGenerator.getRandomInvoice());
        database.save(InvoiceGenerator.getRandomInvoice());

        //when
        database.deleteAll();

        //then
        assertEquals(0L, database.count());
    }

    @Test
    void deleteAllMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccursDuringDeletingAllInvoices() throws DatabaseOperationException {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(database).deleteAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteAll());
    }

    @Test
    void shouldReturnInvoicesFilteredByIssueDate() throws DatabaseOperationException {
        //given
        LocalDate startDate = LocalDate.of(2019, 8, 24);
        pl.coderstrust.model.Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate);
        pl.coderstrust.model.Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.plusDays(1L));
        pl.coderstrust.model.Invoice invoice3 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.plusDays(2L));
        pl.coderstrust.model.Invoice invoice4 = InvoiceGenerator.getRandomInvoiceWithSpecificIssuedDate(startDate.plusDays(4L));
        pl.coderstrust.model.Invoice savedInvoice1 = database.save(invoice1);
        pl.coderstrust.model.Invoice savedInvoice2 = database.save(invoice2);
        pl.coderstrust.model.Invoice savedInvoice3 = database.save(invoice3);
        database.save(invoice4);
        List<pl.coderstrust.model.Invoice> filteredInvoices = List.of(savedInvoice1, savedInvoice2, savedInvoice3);

        //when
        Collection<pl.coderstrust.model.Invoice> filteredInvoicesResult = database.getByIssueDate(startDate, startDate.plusDays(2L));

        //then
        assertEquals(filteredInvoices, filteredInvoicesResult);
    }

    @ParameterizedTest
    @MethodSource("invalidIssuedDateArgumentsAndExceptionMessages")
    void getByIssuedDateMethodShouldThrowExceptionWhenInvalidArgumentsArePassed(LocalDate startDate, LocalDate endDate, String message) {
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

    @Test
    void getByIssuedDateShouldThrowExceptionWhenNonTransientDataAccessExceptionOccursDuringFilteringInvoicesByIssuedDate() throws DatabaseOperationException {
        LocalDate startDate = LocalDate.now();
        doThrow(new NonTransientDataAccessException(" ") {}).when(database).getByIssueDate(startDate, startDate.plusDays(2L));

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> database.getByIssueDate(startDate, startDate.plusDays(2L)));
        assertEquals("An error occurred during getting invoices filtered by issue date", exception.getMessage());
    }
}
