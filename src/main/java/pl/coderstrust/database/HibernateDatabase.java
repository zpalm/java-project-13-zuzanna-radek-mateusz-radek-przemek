package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import pl.coderstrust.database.hibernate.InvoiceRepository;
import pl.coderstrust.model.Invoice;

public class HibernateDatabase implements Database {

    private final InvoiceRepository invoiceRepository;

    public HibernateDatabase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        return invoiceRepository.save(invoice);
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        invoiceRepository.deleteById(id);
    }

    @Override
    public Optional<Invoice> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        return invoiceRepository.findById(id);
    }

    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null.");
        }
        return invoiceRepository.findAll().stream().filter(i -> i.getNumber().equals(number)).findFirst();
    }

    @Override
    public Collection<Invoice> getAll() {
        Iterable<Invoice> invoices = invoiceRepository.findAll();
        return StreamSupport.stream(invoices.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        invoiceRepository.deleteAll();
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        return invoiceRepository.existsById(id);
    }

    @Override
    public long count() {
        return invoiceRepository.count();
    }
}
