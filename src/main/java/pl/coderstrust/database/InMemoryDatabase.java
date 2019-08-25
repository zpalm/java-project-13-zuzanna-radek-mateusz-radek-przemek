package pl.coderstrust.database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.nosql.model.Invoice;
import pl.coderstrust.database.nosql.model.NoSqlModelMapper;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-memory")
public class InMemoryDatabase implements Database {

    private Logger log = LoggerFactory.getLogger(InMemoryDatabase.class);

    private Map<Long, Invoice> storage;
    private AtomicLong nextId = new AtomicLong(0);
    private NoSqlModelMapper noSqlModelMapper;

    public InMemoryDatabase(Map<Long, Invoice> storage, NoSqlModelMapper noSqlModelMapper) {
        if (storage == null) {
            log.error("Attempt to set null storage.");
            throw new IllegalArgumentException("Storage cannot be null.");
        }
        this.storage = storage;
        this.noSqlModelMapper = noSqlModelMapper;
    }

    @Override
    public synchronized pl.coderstrust.model.Invoice save(pl.coderstrust.model.Invoice invoice) {
        if (invoice == null) {
            log.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Passed invoice cannot be null.");
        }
        Invoice noSqlInvoice = noSqlModelMapper.toNoSqlInvoice(invoice);
        if (noSqlInvoice.getId() == null || !storage.containsKey(noSqlInvoice.getId())) {
            return insertInvoice(noSqlInvoice);
        }
        return updateInvoice(noSqlInvoice);
    }

    private pl.coderstrust.model.Invoice insertInvoice(Invoice invoice) {
        Long id = nextId.incrementAndGet();
        Invoice insertedInvoice = Invoice.builder()
            .withId(id)
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();

        storage.put(id, insertedInvoice);
        return noSqlModelMapper.toInvoice(insertedInvoice);
    }

    private pl.coderstrust.model.Invoice updateInvoice(Invoice invoice) {
        Invoice updatedInvoice = Invoice.builder()
            .withId(invoice.getId())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();

        storage.put(invoice.getId(), updatedInvoice);
        return noSqlModelMapper.toInvoice(updatedInvoice);
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        if (!storage.containsKey(id)) {
            log.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        storage.remove(id);
    }

    @Override
    public Optional<pl.coderstrust.model.Invoice> getById(Long id) {
        if (id == null) {
            log.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        return Optional.ofNullable(noSqlModelMapper.toInvoice(storage.get(id)));
    }

    @Override
    public Optional<pl.coderstrust.model.Invoice> getByNumber(String number) {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Passed number cannot be null.");
        }
        Optional<Invoice> foundInvoice = storage.values()
            .stream()
            .filter(invoice -> invoice.getNumber().equals(number))
            .findFirst();
        if (foundInvoice.isPresent()) {
            return Optional.of(noSqlModelMapper.toInvoice(foundInvoice.get()));
        }
        return Optional.empty();
    }

    @Override
    public Collection<pl.coderstrust.model.Invoice> getAll() {
        return noSqlModelMapper.mapToInvoices(new ArrayList<>(storage.values()));
    }

    @Override
    public synchronized void deleteAll() {
        storage.clear();
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            log.error("Attempt to check if invoice exists providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
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
        return storage.values()
            .stream()
            .filter(invoice -> invoice.getIssuedDate().compareTo(startDate) >= 0 && invoice.getIssuedDate().compareTo(endDate) <= 0)
            .collect(Collectors.toList());
    }
}
