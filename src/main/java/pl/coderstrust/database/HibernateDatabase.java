package pl.coderstrust.database;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.hibernate.InvoiceRepository;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "hibernate")
public class HibernateDatabase implements Database {

    private Logger logger = LoggerFactory.getLogger(HibernateDatabase.class);

    private final InvoiceRepository invoiceRepository;

    public HibernateDatabase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            logger.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            return invoiceRepository.save(invoice);
        } catch (NonTransientDataAccessException e) {
            logger.error("An error occurred during saving invoice.", e);
            throw new DatabaseOperationException("An error occurred during saving invoice.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            logger.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            logger.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        try {
            invoiceRepository.deleteById(id);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            logger.error("An error occurred during deleting invoice.", e);
            throw new DatabaseOperationException("An error occurred during deleting invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            logger.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return invoiceRepository.findById(id);
        } catch (NoSuchElementException e) {
            logger.error("An error occurred during getting invoice by id.", e);
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            logger.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        Example<Invoice> example = Example.of(new Invoice.Builder().withNumber(number).build());
        try {
            return invoiceRepository.findOne(example);
        } catch (NonTransientDataAccessException e) {
            logger.error("An error occurred during getting invoice by number.", e);
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return invoiceRepository.findAll();
        } catch (NonTransientDataAccessException e) {
            logger.error("An error occurred during getting all invoices.", e);
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            invoiceRepository.deleteAll();
        } catch (NonTransientDataAccessException e) {
            logger.error("An error occurred during deleting all invoices.", e);
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            logger.error("Attempt to check if invoice exists providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return invoiceRepository.existsById(id);
        } catch (NonTransientDataAccessException e) {
            logger.error("An error occurred during checking if invoice exists.", e);
            throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return invoiceRepository.count();
        } catch (NonTransientDataAccessException e) {
            logger.error("An error occurred during getting number of invoices.", e);
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
