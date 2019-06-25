package pl.coderstrust.database;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import pl.coderstrust.model.Invoice;

public class InMemoryDatabase implements Database {

    private Collection<Invoice> invoiceCollection = new HashSet<>();
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
            invoiceCollection.add(invoiceToSave);
            return invoiceToSave;
        } else {
            for (Invoice invoiceForUpdate : invoiceCollection) {
                if (checkedId.equals(invoiceForUpdate.getId())) {
                    invoiceCollection.remove(invoiceForUpdate);
                    invoiceCollection.add(invoice);
                    return invoiceForUpdate;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        Invoice invoiceToRemove = null;
        for (Invoice searchedInvoice : invoiceCollection) {
            if (id.equals(searchedInvoice.getId())) {
                invoiceToRemove = searchedInvoice;
            }
        }
        if (invoiceToRemove != null) {
            invoiceCollection.remove(invoiceToRemove);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        for (Invoice searchedInvoice : invoiceCollection) {
            if (id.equals(searchedInvoice.getId())) {
                return Optional.of(searchedInvoice);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Passed number cannot be null.");
        }
        for (Invoice searchedInvoice : invoiceCollection) {
            if (number.equals(searchedInvoice.getNumber())) {
                return Optional.of(searchedInvoice);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        return invoiceCollection;
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
        for (Invoice searchedInvoice : invoiceCollection) {
            if (id.equals(searchedInvoice.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long count() throws DatabaseOperationException {
        return (long) invoiceCollection.size();
    }
}
