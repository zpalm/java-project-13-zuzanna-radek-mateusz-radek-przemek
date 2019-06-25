package pl.coderstrust.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import pl.coderstrust.model.Invoice;

public class InMemoryDatabase implements Database {

    private Map<Long, Invoice> invoiceCollection = new HashMap<>();
    private Long id = 0L;

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Passed invoice cannot be null.");
        }
        Long checkedId = invoice.getId();
        if (checkedId == null) {
            id++;
            Invoice invoiceToSave = new Invoice(id, invoice.getNumber(), invoice.getIssuedDate(), invoice.getDueDate(),
                    invoice.getSeller(), invoice.getBuyer(), invoice.getEntries());
            invoiceCollection.put(id, invoiceToSave);
            return invoiceToSave;
        } else {
            for (Map.Entry<Long, Invoice> entry : invoiceCollection.entrySet()) {
                if (checkedId.equals(entry.getKey())) {
                    invoiceCollection.replace(id, entry.getValue(), invoice);
                    return invoice;
                }
            }
        }
        id++;
        Invoice invoiceToSave = new Invoice(id, invoice.getNumber(), invoice.getIssuedDate(), invoice.getDueDate(),
                invoice.getSeller(), invoice.getBuyer(), invoice.getEntries());
        invoiceCollection.put(id, invoiceToSave);
        return invoiceToSave;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        invoiceCollection.remove(id);
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        if (invoiceCollection.containsKey(id)) {
            return Optional.of(invoiceCollection.get(id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Passed number cannot be null.");
        }
        for (Map.Entry<Long, Invoice> entry : invoiceCollection.entrySet()) {
            if (number.equals(entry.getValue().getNumber())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        Collection<Invoice> collection = new HashSet<>();
        if (invoiceCollection.size() > 0) {
            for (Map.Entry<Long, Invoice> entry : invoiceCollection.entrySet()) {
                collection.add(entry.getValue());
            }
        }
        return collection;
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        invoiceCollection.clear();
        id = 0L;
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        return invoiceCollection.containsKey(id);
    }

    @Override
    public long count() throws DatabaseOperationException {
        return (long) invoiceCollection.size();
    }
}
