package pl.coderstrust.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
public class InMemoryDatabase implements Database {

    private Map<Long, Invoice> storage;
    private AtomicLong nextId = new AtomicLong(0);


    public InMemoryDatabase(Map<Long, Invoice> storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null.");
        }
        this.storage = storage;
    }

    @Override
    public synchronized Invoice save(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Passed invoice cannot be null.");
        }
        if (invoice.getId() == null || !storage.containsKey(invoice.getId())) {
            return insertInvoice(invoice);
        }
        return updateInvoice(invoice);
    }

    private Invoice insertInvoice(Invoice invoice) {
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
        return insertedInvoice;
    }

    private Invoice updateInvoice(Invoice invoice) {
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
        return updatedInvoice;
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        if (!storage.containsKey(id)) {
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        storage.remove(id);
    }

    @Override
    public Optional<Invoice> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException("Passed number cannot be null.");
        }
        return storage.values()
            .stream()
            .filter(invoice -> invoice.getNumber().equals(number))
            .findFirst();
    }

    @Override
    public Collection<Invoice> getAll() {
        return storage.values();
    }

    @Override
    public synchronized void deleteAll() {
        storage.clear();
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }
}
