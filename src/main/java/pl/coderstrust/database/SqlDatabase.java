package pl.coderstrust.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.sql.model.SqlModelMapper;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "sql")
public class SqlDatabase implements Database {

    private Logger log = LoggerFactory.getLogger(SqlDatabase.class);

    private final JdbcTemplate template;
    private final SqlModelMapper sqlModelMapper;

    @Autowired
    public SqlDatabase(JdbcTemplate template, SqlModelMapper sqlModelMapper) {
        this.template = template;
        this.sqlModelMapper = sqlModelMapper;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        return null;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {

    }

    @Override
    public Optional<Invoice> getById(Long id) {
        if (id == null) {
            log.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        String sqlQuery = getInvoiceByIdSqlQuery(id);
        Map<Long, Invoice> invoices = createMapOfInvoices(sqlQuery);
        Optional<Invoice> foundInvoice = Optional.of(invoices.values().stream().findFirst().get());
        if (foundInvoice.isPresent()) {
            return foundInvoice;
        }
        return Optional.empty();
    }

    public static String getInvoiceByIdSqlQuery(Long invoiceId) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id= ies.invoice_id AND i.id = ")
            .append(invoiceId + " ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }


    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Passed number cannot be null.");
        }
        String sqlQuery = getInvoiceByNumberSqlQuery(number);
        Map<Long, Invoice> invoices = createMapOfInvoices(sqlQuery);
        Optional<Invoice> foundInvoice = Optional.of(invoices.values().stream().findFirst().get());
        if (foundInvoice.isPresent()) {
            return foundInvoice;
        }
        return Optional.empty();
    }

    public static String getInvoiceByNumberSqlQuery(String invoiceNumber) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id= ies.invoice_id AND i.number = ")
            .append("'" + invoiceNumber + "' ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            String sqlQuery = getAllInvoicesAndEntriesSqlQuery();
            Map<Long, Invoice> invoices = createMapOfInvoices(sqlQuery);
            return invoices.values();
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private Map<Long, Invoice> createMapOfInvoices(String sqlQuery) {
        Map<Long, Invoice> invoices = new HashMap<>();
        template.query(sqlQuery, rs -> {
            long invoiceId = rs.getLong("id");
            if (invoices.containsKey(invoiceId)) {
                Invoice existingInvoice = invoices.get(invoiceId);
                Invoice newInvoice = Invoice.builder()
                    .withInvoice(existingInvoice)
                    .withEntries(ListUtils.union(existingInvoice.getEntries(), Arrays.asList(createInvoiceEntry(rs))))
                    .build();
                invoices.put(invoiceId, newInvoice);
            } else {
                invoices.put(invoiceId, createInvoice(rs));
            }
        });
        return invoices;
    }

    private Invoice createInvoice(ResultSet rs) throws SQLException {
        return Invoice.builder()
            .withId(rs.getLong("id"))
            .withNumber(rs.getString("number"))
            .withIssuedDate(rs.getDate("issued_date").toLocalDate())
            .withDueDate(rs.getDate("due_date").toLocalDate())
            .withSeller(template.queryForObject(getCompanySqlQuery(rs.getLong("seller_id")),
                (cRs, cNumRow) -> createCompany(cRs)))
            .withBuyer(template.queryForObject(getCompanySqlQuery(rs.getLong("buyer_id")),
                (cRs, cNumRow) -> createCompany(cRs)))
            .withEntries(Arrays.asList(createInvoiceEntry(rs)))
            .build();
    }

    private Company createCompany(ResultSet rs) throws SQLException {
        return Company.builder()
            .withId(rs.getLong("id"))
            .withName(rs.getString("name"))
            .withAddress(rs.getString("address"))
            .withTaxId(rs.getString("tax_id"))
            .withAccountNumber(rs.getString("account_number"))
            .withPhoneNumber(rs.getString("phone_number"))
            .withEmail(rs.getString("email"))
            .build();
    }

    private InvoiceEntry createInvoiceEntry(ResultSet rs) throws SQLException {
        return InvoiceEntry.builder()
            .withId(rs.getLong("entries_id"))
            .withDescription(rs.getString("description"))
            .withQuantity(rs.getLong("quantity"))
            .withPrice(rs.getBigDecimal("price"))
            .withNetValue(rs.getBigDecimal("net_value"))
            .withGrossValue(rs.getBigDecimal("gross_value"))
            .withVatRate(createVatRate(rs.getInt("vat_rate")))
            .build();
    }

    private Vat createVatRate(int index) {
        Vat vatRate;
        switch (index) {
          case 0:
              vatRate = Vat.VAT_0;
              break;
          case 1:
              vatRate = Vat.VAT_5;
              break;
          case 2:
              vatRate = Vat.VAT_8;
              break;
          case 3:
              vatRate = Vat.VAT_23;
              break;
          default:
              String message = "An error occurred during getting VAT rate.";
              log.error(message);
              throw new IllegalArgumentException(message);
        }
        return vatRate;
    }

    public static String getAllInvoicesAndEntriesSqlQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id = ies.invoice_id ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }

    public static String getCompanySqlQuery(Long id) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM company WHERE id = ")
            .append(id);
        return select.toString();
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
        return template.queryForObject(countNumberOfInvoicesSqlQuery(),
            (rs, numRow) -> rs.getLong("count"));
    }

    public static String countNumberOfInvoicesSqlQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT COUNT (*) FROM invoice");
        return select.toString();
    }

    @Override
    public Collection<Invoice> getByIssueDate(LocalDate startDate, LocalDate endDate) throws DatabaseOperationException {
        if (startDate == null) {
            log.error("Attempt to get invoices from date interval without providing start date");
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            log.error("Attempt to get invoices from date interval without providing end date");
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            log.error("Attempt to get invoices from date interval when passed start date is after end date");
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        try {
            String sqlQuery = getInvoicesAndEntriesByIssueDateSqlQuery(startDate, endDate);
            Map<Long, Invoice> invoices = createMapOfInvoices(sqlQuery);
            return invoices.values();
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    public static String getInvoicesAndEntriesByIssueDateSqlQuery(LocalDate startDate, LocalDate endDate) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id = ies.invoice_id ")
            .append("AND i.issued_date >= ")
            .append("'" + startDate + "' ")
            .append("AND i.issued_date <= ")
            .append("'" + endDate + "' ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }

    /*    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            String sqlQuery = getAllInvoicesSqlQuery();
            List<Invoice> results = template.query(sqlQuery, (rs, numRow) ->
                Invoice.builder()
                    .withId(rs.getLong("id"))
                    .withNumber(rs.getString("number"))
                    .withIssuedDate(rs.getDate("issued_date").toLocalDate())
                    .withDueDate(rs.getDate("due_date").toLocalDate())
                    .withSeller(createCompany(rs.getLong("seller_id")))
                    .withBuyer(createCompany(rs.getLong("buyer_id")))
                    .withEntries(createInvoiceEntries(rs.getLong("id")))
                    .build());
            return results;
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private Company createCompany(Long id) {
        return template.queryForObject(getCompanySqlQuery(id), (rs, cNumRow) ->
            Company.builder()
                .withId(rs.getLong("id"))
                .withName(rs.getString("name"))
                .withAddress(rs.getString("address"))
                .withTaxId(rs.getString("tax_id"))
                .withAccountNumber(rs.getString("account_number"))
                .withPhoneNumber(rs.getString("phone_number"))
                .withEmail(rs.getString("email"))
                .build());
    }

    private List<InvoiceEntry> createInvoiceEntries(Long invoiceId) {
        return template.query(getAllInvoiceEntrySqlQuery(invoiceId), (rs, numRow) ->
            InvoiceEntry.builder()
                .withId(rs.getLong("entries_id"))
                .withDescription(rs.getString("description"))
                .withQuantity(rs.getLong("quantity"))
                .withPrice(rs.getBigDecimal("price"))
                .withNetValue(rs.getBigDecimal("net_value"))
                .withGrossValue(rs.getBigDecimal("gross_value"))
                .withVatRate(createVatRate(rs.getInt("vat_rate")))
                .build());
    }

    public static String getAllInvoicesSqlQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice");
        return select.toString();
    }

    public static String getCompanySqlQuery(Long id) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM company WHERE id = ")
            .append(id);
        return select.toString();
    }

    public static String getAllInvoiceEntrySqlQuery(Long invoiceId) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice_entry AS ie ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON ies.invoice_id = ")
            .append(invoiceId)
            .append("AND ies.entries_id = ie.id");
        return select.toString();
    }*/
}
