package pl.coderstrust.service;

import java.util.Collection;
import java.util.Optional;

import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

public class InvoiceService {

    private final Database database;

    public InvoiceService(Database database) {
        this.database = database;
    }

    public Collection<Invoice> getAllInvoices() throws ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting all invoices.", e);
        }
    }

    public Optional<Invoice> getInvoiceById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting invoice.", e);
        }
    }

    public Invoice addInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId != null && database.exists(invoiceId)) {
                throw new ServiceOperationException("Invoice already exists in database.");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during adding invoice.", e);
        }
    }

    public Invoice updateInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId == null || !database.exists(invoiceId)) {
                throw new ServiceOperationException("Given invoice does not exist in database.");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during updating invoice.", e);
        }
    }

    public void deleteInvoiceById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            database.delete(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during deleting invoice.", e);
        }
    }

    public void deleteAllInvoices() throws ServiceOperationException {
        try {
            database.deleteAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    public boolean invoiceExists(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    public Optional<Invoice> getInvoiceByNumber(String number) throws ServiceOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    public long invoicesCount() throws ServiceOperationException {
        try {
            return database.count();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
