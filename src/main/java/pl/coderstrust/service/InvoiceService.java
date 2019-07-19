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

    private Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    private final Database database;

    public InvoiceService(Database database) {
        this.database = database;
    }

    public Collection<Invoice> getAllInvoices() throws ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during getting all invoices.", e);
            throw new ServiceOperationException("An error occurred during getting all invoices.", e);
        }
    }

    public Optional<Invoice> getInvoiceById(Long id) throws ServiceOperationException {
        if (id == null) {
            logger.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during getting invoice by id.", e);
            throw new ServiceOperationException("An error occurred during getting invoice.", e);
        }
    }

    public Invoice addInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            logger.error("Attempt to add null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId != null && database.exists(invoiceId)) {
                logger.error("Attempt to add invoice already existing in database.");
                throw new ServiceOperationException("Invoice already exists in database.");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during adding invoice.", e);
            throw new ServiceOperationException("An error occurred during adding invoice.", e);
        }
    }

    public Invoice updateInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            logger.error("Attempt to update invoice providing null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId == null || !database.exists(invoiceId)) {
                logger.error("Attempt to update not existing invoice.");
                throw new ServiceOperationException("Given invoice does not exist in database.");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during updating invoice.", e);
            throw new ServiceOperationException("An error occurred during updating invoice.", e);
        }
    }

    public void deleteInvoiceById(Long id) throws ServiceOperationException {
        if (id == null) {
            logger.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            database.delete(id);
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during deleting invoice.", e);
            throw new ServiceOperationException("An error occurred during deleting invoice.", e);
        }
    }

    public void deleteAllInvoices() throws ServiceOperationException {
        try {
            database.deleteAll();
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during deleting all invoice.", e);
            throw new ServiceOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    public boolean invoiceExists(Long id) throws ServiceOperationException {
        if (id == null) {
            logger.error("Attempt to check if invoice exists providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during checking if invoice exists.", e);
            throw new ServiceOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    public Optional<Invoice> getInvoiceByNumber(String number) throws ServiceOperationException {
        if (number == null) {
            logger.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during getting invoice by number.", e);
            throw new ServiceOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    public long invoicesCount() throws ServiceOperationException {
        try {
            return database.count();
        } catch (DatabaseOperationException e) {
            logger.error("An error occurred during getting number of invoices.", e);
            throw new ServiceOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
