package pl.coderstrust.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.coderstrust.database.sql.model.Company;
import pl.coderstrust.database.sql.model.InvoiceEntry;
import pl.coderstrust.database.sql.model.SqlModelMapper;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "sql")
public class SqlDatabase implements Database {

    private static Logger log = LoggerFactory.getLogger(SqlDatabase.class);

    private final JdbcTemplate template;
    private final SqlModelMapper sqlModelMapper;

    @Autowired
    public SqlDatabase(JdbcTemplate template, SqlModelMapper sqlModelMapper) {
        this.template = template;
        this.sqlModelMapper = sqlModelMapper;
    }

    @Override
    public Invoice save(pl.coderstrust.model.Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            pl.coderstrust.database.sql.model.Invoice sqlInvoice = sqlModelMapper.toSqlInvoice(invoice);
            Long id = sqlInvoice.getId();
            String sqlQuery = getInvoiceByIdSqlQuery(id);
            Map<Long, pl.coderstrust.database.sql.model.Invoice> invoices = createMapOfInvoices(sqlQuery);
            if (id == null || invoices.isEmpty()) {
                return insertInvoice(sqlInvoice);
            }
            pl.coderstrust.database.sql.model.Invoice foundInvoice = invoices.values().stream().findFirst().get();
            return updateInvoice(sqlInvoice, foundInvoice);
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Transactional
    pl.coderstrust.model.Invoice insertInvoice(pl.coderstrust.database.sql.model.Invoice invoice) {
        Company insertedSeller = saveSeller(invoice);
        Long sellerId = insertedSeller.getId();
        Company insertedBuyer = saveBuyer(invoice);
        Long buyerId = insertedBuyer.getId();
        Long invoiceId = insertIntoInvoiceTable(invoice, sellerId, buyerId);
        List<pl.coderstrust.database.sql.model.InvoiceEntry> insertedEntries = saveEntries(invoice, invoiceId);
        pl.coderstrust.database.sql.model.Invoice insertedInvoice = pl.coderstrust.database.sql.model.Invoice.builder()
            .withId(invoiceId)
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(insertedSeller)
            .withBuyer(insertedBuyer)
            .withEntries(insertedEntries)
            .build();
        return sqlModelMapper.toInvoice(insertedInvoice);
    }

    private Long insertIntoInvoiceTable(pl.coderstrust.database.sql.model.Invoice invoice, Long sellerId, Long buyerId) {
        String sqlQuery = insertIntoInvoiceSqlQuery(invoice, sellerId, buyerId);
        Long invoiceId = template.queryForObject(sqlQuery, (rs, numRow) -> rs.getLong("id"));
        return invoiceId;
    }

    private static String insertIntoInvoiceSqlQuery(pl.coderstrust.database.sql.model.Invoice invoice, Long sellerId, Long buyerId) {
        StringBuilder select = new StringBuilder();
        select.append("INSERT INTO invoice(number, issued_date, due_date, seller_id, buyer_id) ")
            .append("VALUES ")
            .append("('").append(invoice.getNumber()).append("', ")
            .append("'").append(invoice.getIssuedDate()).append("', ")
            .append("'").append(invoice.getDueDate()).append("', ")
            .append(sellerId).append(", ")
            .append(buyerId).append(") ")
            .append("RETURNING id");
        return select.toString();
    }

    @Transactional
    pl.coderstrust.model.Invoice updateInvoice(pl.coderstrust.database.sql.model.Invoice invoice, pl.coderstrust.database.sql.model.Invoice foundInvoice) {

        List<Long> companyIdsForDelete = Arrays.asList(foundInvoice.getSeller().getId(), foundInvoice.getBuyer().getId());
        List<Long> invoiceEntryIdsForDelete = new ArrayList<>();
        for (InvoiceEntry invoiceEntry : foundInvoice.getEntries()) {
            invoiceEntryIdsForDelete.add(invoiceEntry.getId());
        }

        Long invoiceId = foundInvoice.getId();

        Company updatedSeller = saveSeller(invoice);
        Long sellerId = updatedSeller.getId();
        Company updatedBuyer = saveBuyer(invoice);
        Long buyerId = updatedBuyer.getId();

        updateIntoInvoiceTable(invoice, sellerId, buyerId);

        deleteFromCompanyTable(companyIdsForDelete);
        deleteFromInvoiceEntriesTable(invoiceId);
        deleteFromInvoiceEntryTable(invoiceEntryIdsForDelete);

        List<pl.coderstrust.database.sql.model.InvoiceEntry> updatedEntries = saveEntries(invoice, invoiceId);

        pl.coderstrust.database.sql.model.Invoice updatedInvoice = pl.coderstrust.database.sql.model.Invoice.builder()
            .withId(invoiceId)
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(updatedSeller)
            .withBuyer(updatedBuyer)
            .withEntries(updatedEntries)
            .build();
        return sqlModelMapper.toInvoice(updatedInvoice);
    }

    private pl.coderstrust.database.sql.model.Company saveSeller(pl.coderstrust.database.sql.model.Invoice invoice) {
        Company seller = invoice.getSeller();
        Long sellerId = insertIntoCompanyTable(seller);
        Company insertedSeller = Company.builder()
            .withId(sellerId)
            .withName(seller.getName())
            .withAddress(seller.getAddress())
            .withTaxId(seller.getTaxId())
            .withAccountNumber(seller.getAccountNumber())
            .withPhoneNumber(seller.getPhoneNumber())
            .withEmail(seller.getEmail())
            .build();
        return insertedSeller;
    }

    private pl.coderstrust.database.sql.model.Company saveBuyer(pl.coderstrust.database.sql.model.Invoice invoice) {
        Company buyer = invoice.getBuyer();
        Long buyerId = insertIntoCompanyTable(buyer);
        Company insertedBuyer = Company.builder()
            .withId(buyerId)
            .withName(buyer.getName())
            .withAddress(buyer.getAddress())
            .withTaxId(buyer.getTaxId())
            .withAccountNumber(buyer.getAccountNumber())
            .withPhoneNumber(buyer.getPhoneNumber())
            .withEmail(buyer.getEmail())
            .build();
        return insertedBuyer;
    }

    private Long insertIntoCompanyTable(pl.coderstrust.database.sql.model.Company company) {
        String sqlQuery = insertIntoCompanySqlQuery(company);
        Long companyId = template.queryForObject(sqlQuery, (rs, numRow) -> rs.getLong("id"));
        return companyId;
    }

    private static String insertIntoCompanySqlQuery(pl.coderstrust.database.sql.model.Company company) {
        StringBuilder select = new StringBuilder();
        select.append("INSERT INTO company(name, address, tax_id, account_number, phone_number, email) ")
            .append("VALUES ")
            .append("('").append(company.getName()).append("', ")
            .append("'").append(company.getAddress()).append("', ")
            .append("'").append(company.getTaxId()).append("', ")
            .append("'").append(company.getAccountNumber()).append("', ")
            .append("'").append(company.getPhoneNumber()).append("', ")
            .append("'").append(company.getEmail()).append("') ")
            .append("RETURNING id");
        return select.toString();
    }

    private List<pl.coderstrust.database.sql.model.InvoiceEntry> saveEntries(pl.coderstrust.database.sql.model.Invoice invoice, Long invoiceId) {
        List<pl.coderstrust.database.sql.model.InvoiceEntry> entries = invoice.getEntries();
        List<pl.coderstrust.database.sql.model.InvoiceEntry> insertedEntries = new ArrayList<>();
        List<Long> insertedEntryIds = insertIntoInvoiceEntryTable(entries);
        for (int i = 0; i <  entries.size(); i++) {
            pl.coderstrust.database.sql.model.InvoiceEntry insertedEntry = InvoiceEntry.builder()
                .withId(insertedEntryIds.get(i))
                .withDescription(entries.get(i).getDescription())
                .withQuantity(entries.get(i).getQuantity())
                .withPrice(entries.get(i).getPrice())
                .withNetValue(entries.get(i).getNetValue())
                .withGrossValue(entries.get(i).getGrossValue())
                .withVatRate(entries.get(i).getVatRate())
                .build();
            insertedEntries.add(insertedEntry);
        }
        insertIntoInvoiceEntriesTable(invoiceId, insertedEntryIds);
        return insertedEntries;
    }

    private List<Long> insertIntoInvoiceEntryTable(List<pl.coderstrust.database.sql.model.InvoiceEntry> entries) {
        String sqlQuery = insertIntoInvoiceEntrySqlQuery(entries);
        List<Long> invoiceEntryIds = template.query(sqlQuery, (rs, numRow) -> rs.getLong("id"));
        return invoiceEntryIds;
    }

    private static String insertIntoInvoiceEntrySqlQuery(List<pl.coderstrust.database.sql.model.InvoiceEntry> entries) {
        StringBuilder select = new StringBuilder();
        select.append("INSERT INTO invoice_entry(description, quantity, price, net_value, gross_value, vat_rate) ")
            .append("VALUES ");
        Iterator<InvoiceEntry> entriesIterator = entries.iterator();
        while (entriesIterator.hasNext()) {
            select.append(extractedBuildQuery(entriesIterator.next()));
            if (entriesIterator.hasNext()) {
                select.append(",");
            }
        }
        select.append(" ")
            .append("RETURNING id");
        return select.toString();
    }

    private static String extractedBuildQuery(pl.coderstrust.database.sql.model.InvoiceEntry invoiceEntry) {
        StringBuilder sb = new StringBuilder();
        sb.append("('").append(invoiceEntry.getDescription()).append("', ")
            .append(invoiceEntry.getQuantity()).append(", ")
            .append(invoiceEntry.getPrice()).append(", ")
            .append(invoiceEntry.getNetValue()).append(", ")
            .append(invoiceEntry.getGrossValue()).append(", ")
            .append(encodeVatRate(invoiceEntry.getVatRate())).append(") ");
        return sb.toString();
    }

    private void insertIntoInvoiceEntriesTable(Long invoiceId, List<Long> invoiceEntryIds) {
        String sqlQuery = insertIntoInvoiceEntriesSqlQuery(invoiceId, invoiceEntryIds);
        template.execute(sqlQuery);
    }

    private static String insertIntoInvoiceEntriesSqlQuery(Long invoiceId, List<Long> invoiceEntryIds) {
        StringBuilder select = new StringBuilder();
        select.append("INSERT INTO invoice_entries(invoice_id, entries_id) ").append("VALUES ");
        for (Long invoiceEntryId : invoiceEntryIds) {
            select.append("(").append(invoiceId).append(", ").append(invoiceEntryId).append(")").append(", ");
        }
        select.deleteCharAt(select.lastIndexOf(","));
        return select.toString();
    }

    private static int encodeVatRate(pl.coderstrust.database.sql.model.Vat vatRate) {
        switch (vatRate) {
          case VAT_0:
              return 0;
          case VAT_5:
              return 1;
          case VAT_8:
              return 2;
          case VAT_23:
              return 3;
          default:
              String message = "An error occurred during encoding VAT rate.";
              log.error(message);
              throw new IllegalArgumentException(message);
        }
    }

    private void updateIntoInvoiceTable(pl.coderstrust.database.sql.model.Invoice invoice, Long sellerId, Long buyerId) {
        String sqlQuery = updateIntoInvoiceSqlQuery(invoice, sellerId, buyerId);
        template.execute(sqlQuery);
    }

    private static String updateIntoInvoiceSqlQuery(pl.coderstrust.database.sql.model.Invoice invoice, Long sellerId, Long buyerId) {
        StringBuilder select = new StringBuilder();
        select.append("UPDATE invoice ")
            .append("SET number = '").append(invoice.getNumber()).append("', ")
            .append("issued_date = '").append(invoice.getIssuedDate()).append("', ")
            .append("due_date = '").append(invoice.getDueDate()).append("', ")
            .append("seller_id = ").append(sellerId).append(", ")
            .append("buyer_id = ").append(buyerId).append(" ")
            .append("WHERE id = ").append(invoice.getId());
        return select.toString();
    }

    private void deleteFromInvoiceEntriesTable(Long invoiceId) {
        String sqlQuery = deleteInvoiceEntriesSqlQuery(invoiceId);
        template.execute(sqlQuery);
    }

    private static String deleteInvoiceEntriesSqlQuery(Long invoiceId) {
        StringBuilder select = new StringBuilder();
        select.append("DELETE FROM invoice_entries WHERE ")
            .append("invoice_id = ").append(invoiceId);
        return select.toString();
    }

    private void deleteFromCompanyTable(List<Long> companyIds) {
        if (companyIds.size() == 2) {
            String sqlQuery = deleteFromCompanySqlQuery(companyIds.get(0), companyIds.get(1));
            template.execute(sqlQuery);
        }
    }

    private static String deleteFromCompanySqlQuery(Long sellerId, Long buyerId) {
        StringBuilder select = new StringBuilder();
        select.append("DELETE FROM company ")
            .append("WHERE id = ")
            .append(sellerId)
            .append(" OR id = ")
            .append(buyerId);
        return select.toString();
    }

    private void deleteFromInvoiceEntryTable(List<Long> invoiceEntryIds) {
        if (invoiceEntryIds.size() > 0) {
            String sqlQuery = deleteFromInvoiceEntrySqlQuery(invoiceEntryIds);
            template.execute(sqlQuery);
        }
    }

    private static String deleteFromInvoiceEntrySqlQuery(List<Long> invoiceEntryIds) {
        String ids = invoiceEntryIds.stream().map(String::valueOf).reduce((result, element) -> String.format("%s, %s", result, element)).get();
        StringBuilder select = new StringBuilder();
        select.append("DELETE FROM invoice_entry WHERE id IN (")
            .append(ids)
            .append(")");
        return select.toString();
    }

    @Transactional
    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            String sqlQuery = getInvoiceByIdSqlQuery(id);
            Map<Long, pl.coderstrust.database.sql.model.Invoice> invoices = createMapOfInvoices(sqlQuery);
            if (invoices == null) {
                log.error("Attempt to delete not existing invoice.");
                throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
            }
            pl.coderstrust.database.sql.model.Invoice foundInvoice = invoices.values().stream().findFirst().get();
            List<Long> invoiceEntryIdsForDelete = new ArrayList<>();
            for (InvoiceEntry invoiceEntry : foundInvoice.getEntries()) {
                invoiceEntryIdsForDelete.add(invoiceEntry.getId());
            }
            deleteFromInvoiceEntriesTable(id);
            deleteFromInvoiceEntryTable(invoiceEntryIdsForDelete);
            template.execute(deleteInvoiceSqlQuery(id));
            List<Long> companyIdsForDelete = Arrays.asList(foundInvoice.getSeller().getId(), foundInvoice.getBuyer().getId());
            deleteFromCompanyTable(companyIdsForDelete);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            String message = "An error occurred during deleting invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private static String deleteInvoiceSqlQuery(Long invoiceId) {
        StringBuilder select = new StringBuilder();
        select.append("DELETE FROM invoice WHERE ")
            .append("id = ").append(invoiceId);
        return select.toString();
    }

    @Override
    public Optional<pl.coderstrust.model.Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        try {
            String sqlQuery = getInvoiceByIdSqlQuery(id);
            Map<Long, pl.coderstrust.database.sql.model.Invoice> invoices = createMapOfInvoices(sqlQuery);
            if (invoices != null) {
                Optional<pl.coderstrust.model.Invoice> foundInvoice = Optional.of(sqlModelMapper.toInvoice(invoices.values()
                    .stream().findFirst().get()));
                return foundInvoice;
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            String message = "An error occurred during getting invoice by id.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private static String getInvoiceByIdSqlQuery(Long invoiceId) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id= ies.invoice_id AND i.id = ")
            .append(invoiceId).append(" ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }

    @Override
    public Optional<pl.coderstrust.model.Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Passed number cannot be null.");
        }
        try {
            String sqlQuery = getInvoiceByNumberSqlQuery(number);
            Map<Long, pl.coderstrust.database.sql.model.Invoice> invoices = createMapOfInvoices(sqlQuery);
            if (invoices != null) {
                Optional<pl.coderstrust.model.Invoice> foundInvoice = Optional.of(sqlModelMapper.toInvoice(invoices.values()
                    .stream().findFirst().get()));
                return foundInvoice;
            }
            return Optional.empty();
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private static String getInvoiceByNumberSqlQuery(String invoiceNumber) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id= ies.invoice_id AND i.number = ")
            .append("'").append(invoiceNumber).append("' ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }

    @Override
    public Collection<pl.coderstrust.model.Invoice> getAll() throws DatabaseOperationException {
        try {
            String sqlQuery = getAllInvoicesAndEntriesSqlQuery();
            Map<Long, pl.coderstrust.database.sql.model.Invoice> invoices = createMapOfInvoices(sqlQuery);
            List<pl.coderstrust.database.sql.model.Invoice> invoiceList = new ArrayList<>(invoices.values());
            return sqlModelMapper.mapToInvoices(invoiceList);
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private static String getAllInvoicesAndEntriesSqlQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id = ies.invoice_id ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }

    private Map<Long, pl.coderstrust.database.sql.model.Invoice> createMapOfInvoices(String sqlQuery) {
        Map<Long, pl.coderstrust.database.sql.model.Invoice> invoices = new HashMap<>();
        template.query(sqlQuery, rs -> {
            long invoiceId = rs.getLong("id");
            if (invoices.containsKey(invoiceId)) {
                pl.coderstrust.database.sql.model.Invoice existingInvoice = invoices.get(invoiceId);
                pl.coderstrust.database.sql.model.Invoice newInvoice = pl.coderstrust.database.sql.model.Invoice.builder()
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

    private pl.coderstrust.database.sql.model.InvoiceEntry createInvoiceEntry(ResultSet rs) throws SQLException {
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

    private pl.coderstrust.database.sql.model.Vat createVatRate(int index) {
        pl.coderstrust.database.sql.model.Vat vatRate;
        switch (index) {
          case 0:
              vatRate = pl.coderstrust.database.sql.model.Vat.VAT_0;
              break;
          case 1:
              vatRate = pl.coderstrust.database.sql.model.Vat.VAT_5;
              break;
          case 2:
              vatRate = pl.coderstrust.database.sql.model.Vat.VAT_8;
              break;
          case 3:
              vatRate = pl.coderstrust.database.sql.model.Vat.VAT_23;
              break;
          default:
              String message = "An error occurred during getting VAT rate.";
              log.error(message);
              throw new IllegalArgumentException(message);
        }
        return vatRate;
    }

    private pl.coderstrust.database.sql.model.Invoice createInvoice(ResultSet rs) throws SQLException {
        return pl.coderstrust.database.sql.model.Invoice.builder()
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

    private static String getCompanySqlQuery(Long id) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM company WHERE id = ")
            .append(id);
        return select.toString();
    }

    private pl.coderstrust.database.sql.model.Company createCompany(ResultSet rs) throws SQLException {
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

    @Transactional
    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            String sqlQueryDeleteFromInvoiceEntries = "DELETE FROM invoice_entries";
            String sqlQueryDeleteFromInvoiceEntry = "DELETE FROM invoice_entry";
            String sqlQueryDeleteFromInvoice = "DELETE FROM invoice";
            String sqlQueryDeleteFromCompany = "DELETE FROM company";
            template.execute(sqlQueryDeleteFromInvoiceEntries);
            template.execute(sqlQueryDeleteFromInvoiceEntry);
            template.execute(sqlQueryDeleteFromInvoice);
            template.execute(sqlQueryDeleteFromCompany);
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during deleting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if invoice exists providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        try {
            String sqlQuery = existsSqlQuery(id);
            return template.queryForObject(sqlQuery, (rs, numRow) -> rs.getBoolean(1));
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private static String existsSqlQuery(Long id) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT CASE ")
            .append("WHEN EXISTS (SELECT 1 FROM invoice WHERE id = ")
            .append(id).append(") ")
            .append("THEN true ")
            .append("ELSE false ")
            .append("END");
        return select.toString();
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return template.queryForObject(countNumberOfInvoicesSqlQuery(),
                (rs, numRow) -> rs.getLong("count"));
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting number of invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private static String countNumberOfInvoicesSqlQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT COUNT (*) FROM invoice");
        return select.toString();
    }

    @Override
    public Collection<pl.coderstrust.model.Invoice> getByIssueDate(LocalDate startDate, LocalDate endDate) throws DatabaseOperationException {
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
            Map<Long, pl.coderstrust.database.sql.model.Invoice> invoices = createMapOfInvoices(sqlQuery);
            List<pl.coderstrust.database.sql.model.Invoice> invoiceList = new ArrayList<>(invoices.values());
            return sqlModelMapper.mapToInvoices(invoiceList);
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting invoices filtered by issue date";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private static String getInvoicesAndEntriesByIssueDateSqlQuery(LocalDate startDate, LocalDate endDate) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
            .append("FROM invoice AS i ")
            .append("JOIN invoice_entries AS ies ")
            .append("ON i.id = ies.invoice_id ")
            .append("AND i.issued_date >= ")
            .append("'").append(startDate).append("' ")
            .append("AND i.issued_date <= ")
            .append("'").append(endDate).append("' ")
            .append("JOIN invoice_entry AS ie ")
            .append("ON ies.entries_id = ie.id");
        return select.toString();
    }
}
