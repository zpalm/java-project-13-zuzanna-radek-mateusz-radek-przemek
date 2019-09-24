package pl.coderstrust.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            String sqlQuery = getAllInvoicesAndEntriesSqlQuery();
            List<Invoice> results = template.query(sqlQuery, (rs, numRow) ->
                Invoice.builder()
                    .withId(rs.getLong("id"))
                    .withNumber(rs.getString("number"))
                    .withIssuedDate(rs.getDate("issued_date").toLocalDate())
                    .withDueDate(rs.getDate("due_date").toLocalDate())
                    .withSeller(template.queryForObject(getCompanySqlQuery(rs.getLong("seller_id")),
                        (cRs, cNumRow) -> Company.builder()
                            .withId(cRs.getLong("id"))
                            .withName(cRs.getString("name"))
                            .withAddress(cRs.getString("address"))
                            .withTaxId(cRs.getString("tax_id"))
                            .withAccountNumber(cRs.getString("account_number"))
                            .withPhoneNumber(cRs.getString("phone_number"))
                            .build()))
                    .withBuyer(template.queryForObject(getCompanySqlQuery(rs.getLong("buyer_id")),
                        (cRs, cNumRow) -> Company.builder()
                            .withId(cRs.getLong("id"))
                            .withName(cRs.getString("name"))
                            .withAddress(cRs.getString("address"))
                            .withTaxId(cRs.getString("tax_id"))
                            .withAccountNumber(cRs.getString("account_number"))
                            .withPhoneNumber(cRs.getString("phone_number"))
                            .build()))
                    .withEntries(createInvoiceEntries(rs))
                    .build());
            return results;
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    public static String getAllInvoicesAndEntriesSqlQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id= ies.invoice_id ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id= ie.id");
        return select.toString();
    }

    public static String getCompanySqlQuery(Long id) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM company WHERE id = ")
            .append(id);
        return select.toString();
    }

    private List<InvoiceEntry> createInvoiceEntries(ResultSet rs) throws SQLException {
        List<InvoiceEntry> entries = new ArrayList<>();
        InvoiceEntry entry = InvoiceEntry.builder()
            .withId(rs.getLong("id"))
            .withDescription(rs.getString("description"))
            .withQuantity(rs.getLong("quantity"))
            .withPrice(rs.getBigDecimal("price"))
            .withNetValue(rs.getBigDecimal("net_value"))
            .withGrossValue(rs.getBigDecimal("gross_value"))
            .withVatRate(vatRate(rs.getInt("vat_rate")))
            .build();
        entries.add(entry);
        return entries;
    }

    private Vat vatRate(int index) {
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
