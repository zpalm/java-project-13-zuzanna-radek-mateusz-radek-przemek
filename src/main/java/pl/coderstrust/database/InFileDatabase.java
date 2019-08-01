package pl.coderstrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-file")
public class InFileDatabase implements Database {

    private Logger log = LoggerFactory.getLogger(InFileDatabase.class);
    private String path;
    private ObjectMapper mapper;
    private FileHelper fileHelper;
    private AtomicLong nextId;

    @Autowired
    public InFileDatabase(InFileDatabaseProperties inFileDatabaseProperties, ObjectMapper mapper, FileHelper fileHelper) {
        this.path = inFileDatabaseProperties.getPath();
        this.mapper = mapper;
        this.fileHelper = fileHelper;
        init();
    }

    private void init() {
        if (!fileHelper.exists(path)) {
            try {
                fileHelper.create(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (fileHelper.isEmpty(path)) {
                this.nextId = new AtomicLong(0);
            } else {
                Invoice lastInvoice = mapper.readValue(fileHelper.readLastLine(path), Invoice.class);
                this.nextId = new AtomicLong(lastInvoice.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Passed invoice cannot be null.");
        }
        if (invoice.getId() == null || !exists(invoice.getId())) {
            return insertInvoice(invoice);
        }
        return updateInvoice(invoice);
    }

    private Invoice insertInvoice(Invoice invoice) throws DatabaseOperationException {
        if (!(invoice.getId() == null)) {
            nextId.set(invoice.getId() - 1);
        }
        Invoice insertedInvoice = Invoice.builder()
            .withId(nextId.incrementAndGet())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();
        try {
            fileHelper.writeLine(path, mapper.writeValueAsString(insertedInvoice));
        } catch (IOException e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
        return insertedInvoice;
    }

    private Invoice updateInvoice(Invoice invoice) throws DatabaseOperationException {
        Invoice updatedInvoice = Invoice.builder()
            .withId(invoice.getId())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();
        try {
            int index = fileHelper.readLines(path).stream()
                .map(this::deserialize)
                .collect(Collectors.toList())
                .indexOf(getById(invoice.getId()).get()) + 1;
            fileHelper.replaceLine(path, mapper.writeValueAsString(updatedInvoice), index);
        } catch (IOException e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
        return updatedInvoice;
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        if (!exists(id)) {
            log.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        try {
            int index = fileHelper.readLines(path).stream()
                .map(this::deserialize)
                .collect(Collectors.toList())
                .indexOf(getById(id).get()) + 1;
            fileHelper.removeLine(path, index);
        } catch (IOException e) {
            String message = "An error occurred during deleting invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        try {
            return fileHelper.readLines(path).stream()
                .map(this::deserialize)
                .filter(invoice -> invoice.getId().equals(id))
                .findFirst();
        } catch (IOException e) {
            String message = "An error occurred during getting invoice by id.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        try {
            return fileHelper.readLines(path).stream()
                .map(this::deserialize)
                .filter(invoice -> invoice.getNumber().equals(number))
                .findFirst();
        } catch (IOException e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return fileHelper.readLines(path).stream()
                .map(this::deserialize)
                .collect(Collectors.toList());
        } catch (IOException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        try {
            fileHelper.clear(path);
        } catch (IOException e) {
            String message = "An error occurred during deleting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if invoice exists providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        try {
            return fileHelper.readLines(path).stream()
                .map(this::deserialize)
                .anyMatch(invoice -> invoice.getId().equals(id));
        } catch (IOException e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return fileHelper.readLines(path).stream()
                .map(this::deserialize)
                .count();
        } catch (IOException e) {
            String message = "An error occurred during getting number of invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private Invoice deserialize(String invoice) {
        try {
            return mapper.readValue(invoice, Invoice.class);
        } catch (IOException e) {
            String message = "An error occurred during deserializing invoices.";
            log.error(message, e);
            throw new DeserializationException(message, e);
        }
    }
}
