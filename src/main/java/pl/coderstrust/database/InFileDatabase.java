package pl.coderstrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
    private String filePath;
    private ObjectMapper mapper;
    private FileHelper fileHelper;
    private AtomicLong nextId;

    @Autowired
    public InFileDatabase(InFileDatabaseProperties inFileDatabaseProperties, ObjectMapper mapper, FileHelper fileHelper) throws IOException {
        this.filePath = inFileDatabaseProperties.getFilePath();
        this.mapper = mapper;
        this.fileHelper = fileHelper;
        init();
    }

    private void init() throws IOException {
        if (!fileHelper.exists(filePath)) {
            fileHelper.create(filePath);
        }
        String lastInvoiceAsJson = fileHelper.readLastLine(filePath);
        if (lastInvoiceAsJson == null) {
            nextId = new AtomicLong(0);
        } else {
            Invoice invoice = deserializeJsonToInvoice(lastInvoiceAsJson);
            if (invoice == null) {
                nextId = new AtomicLong(0);
            } else {
                nextId = new AtomicLong(invoice.getId());
            }
        }
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Passed invoice cannot be null.");
        }
        try {
            if (invoice.getId() == null || !exists(invoice.getId())) {
                return insertInvoice(invoice);
            }
            return updateInvoice(invoice);
        } catch (IOException e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private Invoice insertInvoice(Invoice invoice) throws IOException {
        Invoice insertedInvoice = Invoice.builder()
            .withId(nextId.incrementAndGet())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();
        fileHelper.writeLine(filePath, mapper.writeValueAsString(insertedInvoice));
        return insertedInvoice;
    }

    private Invoice updateInvoice(Invoice invoice) throws DatabaseOperationException, IOException {
        Invoice updatedInvoice = Invoice.builder()
            .withId(invoice.getId())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();
        List<Invoice> invoices = getInvoices();
        Optional<Invoice> optionalInvoice = invoices
            .stream()
            .filter(i -> i.getId().equals(invoice.getId()))
            .findFirst();
        if (optionalInvoice.isEmpty()) {
            throw new DatabaseOperationException("Invoice with following id doesn't exist");
        }
        fileHelper.replaceLine(filePath, mapper.writeValueAsString(updatedInvoice), invoices.indexOf(optionalInvoice.get()) + 1);
        return updatedInvoice;
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        try {
            List<Invoice> invoices = getInvoices();
            Optional<Invoice> optionalInvoice = invoices
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
            if (optionalInvoice.isEmpty()) {
                log.error("Attempt to delete not existing invoice.");
                throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
            }
            fileHelper.removeLine(filePath, invoices.indexOf(optionalInvoice.get()) + 1);
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
            return getInvoices().stream()
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
            return getInvoices().stream()
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
            return getInvoices();
        } catch (IOException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        try {
            fileHelper.clear(filePath);
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
            return isInvoiceExist(id);
        } catch (IOException e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return getInvoices().size();
        } catch (IOException e) {
            String message = "An error occurred during getting number of invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private Invoice deserializeJsonToInvoice(String json) {
        try {
            return mapper.readValue(json, Invoice.class);
        } catch (IOException e) {
            return null;
        }
    }

    private boolean isInvoiceExist(long id) throws IOException {
        List<Invoice> invoices = getInvoices();
        Optional<Invoice> invoice = invoices
            .stream()
            .filter(i -> i.getId().equals(id))
            .findFirst();
        return invoice.isPresent();
    }

    private List<Invoice> getInvoices() throws IOException {
        return fileHelper.readLines(filePath).stream()
            .map(this::deserializeJsonToInvoice)
            .collect(Collectors.toList());
    }
}
