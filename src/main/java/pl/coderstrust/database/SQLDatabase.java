package pl.coderstrust.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.sql.model.SqlModelMapper;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "postgresql")
public class SQLDatabase implements Database {

    private Logger log = LoggerFactory.getLogger(SQLDatabase.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SqlModelMapper sqlModelMapper;

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        return null;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {

    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        String sqlQuery = getAllInvoicesAndEntriesSQLQuery();
//        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRow(rs, rowNum));
//        return new ArrayList<>();
//        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper(Invoice.class));
        List<Invoice> results = jdbcTemplate.query(sqlQuery, (rs, numRow) ->
            Invoice.builder()
                .withId(rs.getLong("id"))
                .build());

       /* ArrayList<InvoiceEntry> entries = new ArrayList<>();
        ArrayList<Invoice> invoices = new ArrayList<>();
        results.stream().filter(o -> )*/

        return new ArrayList<>();
    }

    public static String getAllInvoicesAndEntriesSQLQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT id ")
            .append("FROM invoice i ")
            .append("JOIN invoice_entry ie ON ie.invoice_id = i.id");
        return select.toString();
    }

    public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong(1);
        return Invoice.builder().withId(id).build();
    }

    public Collection<Invoice> getAllOld() throws DatabaseOperationException {
        Map<Long, Invoice> storage = new HashMap<>();
        List<InvoiceEntry> entries = new ArrayList<>();
        Long id = null;
        String number = null;
        LocalDate issuedDate = null;
        LocalDate dueDate = null;
        Company seller = null;
        Company buyer = null;
        Long tempId = 0L;
        try (Connection connection = DriverManager.getConnection(
            "jdbc:postgresql://127.0.0.1:5432/invoices", "postgres", "postgres"
        )) {
            if (connection != null) {
                log.info("Connection to database has been initialized.");
            } else {
                log.error("Failed to make connection.");
                throw new IllegalStateException("An error occurred during initializing connection.");
            }
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT i.*, s.*, b.*, ie.*, ies.* " +
                    "FROM invoice i " +
                    "JOIN company s ON i.seller_id = s.id " +
                    "JOIN company b ON i.buyer_id = b.id " +
                    "JOIN invoice_entries ies ON i.id = ies.invoice_id " +
                    "JOIN invoice_entry ie ON ie.id = ies.entry_id");
                while (resultSet.next()) {
                    Long entriesInvoiceId = resultSet.getLong(28);
                    if (entriesInvoiceId != tempId && tempId > 0) {
                        Invoice invoice = Invoice.builder()
                            .withId(id)
                            .withNumber(number)
                            .withIssuedDate(issuedDate)
                            .withDueDate(dueDate)
                            .withSeller(seller)
                            .withBuyer(buyer)
                            .withEntries(new ArrayList<>(entries))
                            .build();
                        storage.put(id, invoice);
                        entries.clear();
                    }
                    id = resultSet.getLong(1);
                    if (id != tempId) {
                        number = resultSet.getString(2);
                        issuedDate = (LocalDate) resultSet.getObject(3);
                        dueDate = (LocalDate) resultSet.getObject(4);
                        Long sellerId = resultSet.getLong(7);
                        String sellerName = resultSet.getString(8);
                        String sellerAddress = resultSet.getString(9);
                        String sellerTaxId = resultSet.getString(10);
                        String sellerAccountNumber = resultSet.getString(11);
                        String sellerPhoneNumber = resultSet.getString(12);
                        String sellerEmail = resultSet.getString(13);
                        Long buyerId = resultSet.getLong(14);
                        String buyerName = resultSet.getString(15);
                        String buyerAddress = resultSet.getString(16);
                        String buyerTaxId = resultSet.getString(17);
                        String buyerAccountNumber = resultSet.getString(18);
                        String buyerPhoneNumber = resultSet.getString(19);
                        String buyerEmail = resultSet.getString(20);
                        seller = Company.builder()
                            .withId(sellerId)
                            .withName(sellerName)
                            .withAddress(sellerAddress)
                            .withTaxId(sellerTaxId)
                            .withAccountNumber(sellerAccountNumber)
                            .withPhoneNumber(sellerPhoneNumber)
                            .withEmail(sellerEmail)
                            .build();
                        buyer = Company.builder()
                            .withId(buyerId)
                            .withName(buyerName)
                            .withAddress(buyerAddress)
                            .withTaxId(buyerTaxId)
                            .withAccountNumber(buyerAccountNumber)
                            .withPhoneNumber(buyerPhoneNumber)
                            .withEmail(buyerEmail)
                            .build();
                    }
                    Long entryId = resultSet.getLong(21);
                    String entryDescription = resultSet.getString(22);
                    Long entryQuantity = resultSet.getLong(23);
                    BigDecimal entryPrice = resultSet.getBigDecimal(24);
                    BigDecimal entryNetValue = resultSet.getBigDecimal(25);
                    BigDecimal entryGrossValue = resultSet.getBigDecimal(26);
                    Vat entryVatRate = Vat.valueOf(String.valueOf(resultSet.getInt(27)));
                    InvoiceEntry invoiceEntry = InvoiceEntry.builder()
                        .withId(entryId)
                        .withDescription(entryDescription)
                        .withQuantity(entryQuantity)
                        .withPrice(entryPrice)
                        .withNetValue(entryNetValue)
                        .withGrossValue(entryGrossValue)
                        .withVatRate(entryVatRate)
                        .build();
                    entries.add(invoiceEntry);
                    tempId = id;
                }
            }
        } catch (SQLException e) {
            String message = e.getMessage();
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
        return storage.values();
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {

    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        return false;
    }

    @Override
    public long count() throws DatabaseOperationException {
        return 0;
    }

    @Override
    public Collection<Invoice> getByIssueDate(LocalDate startDate, LocalDate endDate) throws DatabaseOperationException {
        return null;
    }


}
