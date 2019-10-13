package pl.coderstrust.database;

import java.util.Iterator;
import java.util.List;

import pl.coderstrust.database.sql.model.InvoiceEntry;

public class SqlQueries {

    final static String insertIntoInvoiceSqlQuery = "INSERT INTO invoice(number, issued_date, due_date, seller_id, buyer_id) " +
        "VALUES (?, ?, ?, ?, ?) RETURNING id";

    final static String insertIntoCompanySqlQuery = "INSERT INTO company(name, address, tax_id, account_number, phone_number, email) " +
        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

//    final static String insertIntoInvoiceEntrySqlQuery(List<InvoiceEntry> entries) {
//        StringBuilder select = new StringBuilder();
//        select.append("INSERT INTO invoice_entry(description, quantity, price, net_value, gross_value, vat_rate) ")
//            .append("VALUES ");
//        Iterator<InvoiceEntry> entriesIterator = entries.iterator();
//        while (entriesIterator.hasNext()) {
//            select.append("(?, ?, ?, ?, ?, ?)");
//            entriesIterator.next();
//            if (entriesIterator.hasNext()) {
//                select.append(", ");
//            }
//        }
//        select.append(" ")
//            .append("RETURNING id");
//        return select.toString();
//    }

    final static String insertIntoInvoiceEntrySqlQuery(List<InvoiceEntry> entries) {
        String query = "INSERT INTO invoice_entry(description, quantity, price, net_value, gross_value, vat_rate) VALUES ";
        Iterator<InvoiceEntry> entriesIterator = entries.iterator();
        while (entriesIterator.hasNext()) {
            query += "(?, ?, ?, ?, ?, ?)";
            entriesIterator.next();
            if (entriesIterator.hasNext()) {
                query += ", ";
            }
        }
        query += " RETURNING id";
        return query;
    }

    final static String insertIntoInvoiceEntriesSqlQuery = "INSERT INTO invoice_entries(invoice_id, entries_id) VALUES (?, ?)";

    final static String updateIntoInvoiceSqlQuery = "UPDATE invoice SET number = ?, " +
        "issued_date = ?, due_date = ?, seller_id = ?, buyer_id = ? WHERE id = ?";

    final static String deleteInvoiceEntriesSqlQuery = "DELETE FROM invoice_entries WHERE invoice_id = ?";

    final static String deleteFromCompanySqlQuery = "DELETE FROM company WHERE id = ? OR id = ?";

//    private static String deleteFromInvoiceEntrySqlQuery(List<Long> invoiceEntryIds) {
//        StringBuilder select = new StringBuilder();
//        select.append("DELETE FROM invoice_entry WHERE id IN (");
//        Iterator<Long> idsIterator = invoiceEntryIds.iterator();
//        while (idsIterator.hasNext()) {
//            select.append("?");
//            idsIterator.next();
//            if (idsIterator.hasNext()) {
//                select.append(", ");
//            }
//        }
//        select.append(")");
//        return select.toString();
//    }

    final static String deleteFromInvoiceEntrySqlQuery(List<Long> invoiceEntryIds) {
        String select = "DELETE FROM invoice_entry WHERE id IN (";
        Iterator<Long> idsIterator = invoiceEntryIds.iterator();
        while (idsIterator.hasNext()) {
            select += "?";
            idsIterator.next();
            if (idsIterator.hasNext()) {
                select += ", ";
            }
        }
        select += ")";
        return select;
    }

    final static String deleteInvoiceSqlQuery = "DELETE FROM invoice WHERE id = ?";

    final static String getInvoiceByIdSqlQuery = "SELECT * FROM invoice AS i JOIN invoice_entries AS ies " +
        "ON i.id = ies.invoice_id AND i.id = ? JOIN invoice_entry AS ie ON ies.entries_id = ie.id";

    final static String getInvoiceByNumberSqlQuery = "SELECT * FROM invoice AS i JOIN invoice_entries AS ies " +
        "ON i.id = ies.invoice_id AND i.number = ? JOIN invoice_entry AS ie ON ies.entries_id = ie.id";

    final static String getAllInvoicesAndEntriesSqlQuery = "SELECT * FROM invoice AS i JOIN invoice_entries AS ies " +
        "ON i.id = ies.invoice_id JOIN invoice_entry AS ie ON ies.entries_id = ie.id";

    final static String getCompanySqlQuery = "SELECT * FROM company WHERE id = ?";

    final static String sqlQueryDeleteFromInvoiceEntries = "DELETE FROM invoice_entries";
    final static String sqlQueryDeleteFromInvoiceEntry = "DELETE FROM invoice_entry";
    final static String sqlQueryDeleteFromInvoice = "DELETE FROM invoice";
    final static String sqlQueryDeleteFromCompany = "DELETE FROM company";

    final static String existsSqlQuery = "SELECT CASE WHEN EXISTS (SELECT 1 FROM invoice WHERE id = ?) THEN true ELSE false END";

    final static String countNumberOfInvoicesSqlQuery = "SELECT COUNT (*) FROM invoice";

    final static String getInvoicesAndEntriesByIssueDateSqlQuery = "SELECT * FROM invoice AS i JOIN invoice_entries AS ies " +
        "ON i.id = ies.invoice_id AND i.issued_date >= ? AND i.issued_date <= ? " +
        "JOIN invoice_entry AS ie ON ies.entries_id = ie.id";

    final static String dropAllTables = "DROP TABLE IF EXISTS company, invoice, invoice_entry, invoice_entries CASCADE";

    final static String createDatabase = "SELECT 'CREATE DATABASE invoices1' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'invoices1')";

    final static String createTableCompany = "CREATE TABLE IF NOT EXISTS company " +
        "(id BIGSERIAL, name VARCHAR(255), address VARCHAR(255), tax_id VARCHAR(255), account_number VARCHAR(255), " +
        "phone_number VARCHAR(255), email VARCHAR(255), CONSTRAINT company_pkey PRIMARY KEY (id))";

    final static String createTableInvoice = "CREATE TABLE IF NOT EXISTS invoice " +
        "(id BIGSERIAL, number VARCHAR(255), issued_date DATE, due_date DATE, seller_id bigint, buyer_id bigint, " +
        "CONSTRAINT invoice_pkey PRIMARY KEY (id), FOREIGN KEY (seller_id) REFERENCES company(id), " +
        "FOREIGN KEY (buyer_id) REFERENCES company(id))";

    final static String alterTableInvoice = "ALTER TABLE invoice OWNER TO postgres";

    final static String createTableInvoiceEntry = "CREATE TABLE IF NOT EXISTS invoice_entry " +
        "(id BIGSERIAL, description VARCHAR(255), quantity bigint, price numeric(19, 2), net_value numeric(19, 2), " +
        "gross_value numeric(19, 2), vat_rate int, CONSTRAINT invoice_entry_pkey PRIMARY KEY (id))";

    final static String createTableInvoiceEntries = "CREATE TABLE IF NOT EXISTS invoice_entries " +
        "(invoice_id bigint, entries_id bigint, FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
        "FOREIGN KEY (entries_id) REFERENCES invoice_entry(id) ON DELETE CASCADE ON UPDATE CASCADE)";
}
