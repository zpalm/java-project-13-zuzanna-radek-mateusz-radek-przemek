package pl.coderstrust.service;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceService {

    private Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final Database database;

    public InvoiceService(Database database) {
        this.database = database;
    }

    public Collection<Invoice> getAllInvoices() throws ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during getting all invoices.", e);
        }
    }

    public Optional<Invoice> getInvoiceById(Long id) throws ServiceOperationException {
        if (id == null) {
            String message = "Attempt to get invoice by id providing null id.";
            log.error(message);
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during getting invoice by id.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during getting invoice.", e);
        }
    }

    public Invoice addInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            String message = "Attempt to add null invoice.";
            log.error(message);
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId != null && database.exists(invoiceId)) {
                String message = "Attempt to add invoice already existing in database.";
                log.error(message);
                throw new ServiceOperationException("Invoice already exists in database.");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during adding invoice.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during adding invoice.", e);
        }
    }

    public Invoice updateInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            String message = "Attempt to update invoice providing null invoice.";
            log.error(message);
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId == null || !database.exists(invoiceId)) {
                String message = "Attempt to update not existing invoice.";
                log.error(message);
                throw new ServiceOperationException("Given invoice does not exist in database.");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during updating invoice.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during updating invoice.", e);
        }
    }

    public void deleteInvoiceById(Long id) throws ServiceOperationException {
        if (id == null) {
            String message = "Attempt to delete invoice providing null id.";
            log.error(message);
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            database.delete(id);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during deleting invoice.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during deleting invoice.", e);
        }
    }

    public void deleteAllInvoices() throws ServiceOperationException {
        try {
            database.deleteAll();
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during deleting all invoice.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    public boolean invoiceExists(Long id) throws ServiceOperationException {
        if (id == null) {
            String message = "Attempt to check if invoice exists providing null id.";
            log.error(message);
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    public Optional<Invoice> getInvoiceByNumber(String number) throws ServiceOperationException {
        if (number == null) {
            String message = "Attempt to get invoice by number providing null number.";
            log.error(message);
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    public long invoicesCount() throws ServiceOperationException {
        try {
            return database.count();
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during getting number of invoices.";
            log.error(message, e);
            throw new ServiceOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
