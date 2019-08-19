package pl.coderstrust.database;

import java.time.LocalDate;
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
import pl.coderstrust.database.sql.model.Invoice;
import pl.coderstrust.database.sql.model.SqlModelMapper;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "hibernate")
public class HibernateDatabase implements Database {

    private Logger log = LoggerFactory.getLogger(HibernateDatabase.class);

    private final InvoiceRepository invoiceRepository;
    private SqlModelMapper sqlModelMapper;

    public HibernateDatabase(InvoiceRepository invoiceRepository, SqlModelMapper sqlModelMapper) {
        this.invoiceRepository = invoiceRepository;
        this.sqlModelMapper = sqlModelMapper;
    }

    @Override
    public pl.coderstrust.model.Invoice save(pl.coderstrust.model.Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Invoice sqlInvoice = sqlModelMapper.toSqlInvoice(invoice);
            return sqlModelMapper.toInvoice(invoiceRepository.save(sqlInvoice));
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            log.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        try {
            invoiceRepository.deleteById(id);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            String message = "An error occurred during deleting invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Optional<pl.coderstrust.model.Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Optional<Invoice> foundInvoice = invoiceRepository.findById(id);
            if (foundInvoice.isPresent()) {
                return Optional.of(sqlModelMapper.toInvoice(foundInvoice.get()));
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            String message = "An error occurred during getting invoice by id.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Optional<pl.coderstrust.model.Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        Example<Invoice> example = Example.of(new Invoice.Builder().withNumber(number).build());
        try {
            Optional<Invoice> foundInvoice = invoiceRepository.findOne(example);
            if (foundInvoice.isPresent()) {
                return Optional.of(sqlModelMapper.toInvoice(foundInvoice.get()));
            }
            return Optional.empty();
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Collection<pl.coderstrust.model.Invoice> getAll() throws DatabaseOperationException {
        try {
            return sqlModelMapper.mapToInvoices(invoiceRepository.findAll());
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            invoiceRepository.deleteAll();
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
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return invoiceRepository.existsById(id);
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return invoiceRepository.count();
        } catch (NonTransientDataAccessException e) {
            String message = "An error occurred during getting number of invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Collection<Invoice> getByIssueDate(LocalDate startDate, LocalDate endDate) throws DatabaseOperationException {
        try {
            return invoiceRepository.findAllByIssuedDate(startDate, endDate);
        } catch (NonTransientDataAccessException e) {
            String message = "An error occured during getting invoices filtered by issue date";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }

    }
}
