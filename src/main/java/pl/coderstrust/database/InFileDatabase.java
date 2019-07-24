package pl.coderstrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
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

    private Logger log = LoggerFactory.getLogger(InMemoryDatabase.class);
    private String path;
    private ObjectMapper mapper;

    private static FileHelper fileHelper = new FileHelper();

    @Autowired
    public InFileDatabase(InFileDatabaseProperties inFileDatabaseProperties, ObjectMapper objectMapper) {
        this.path = inFileDatabaseProperties.getPath();
        this.mapper = objectMapper;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
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
        long newId = 1L;
        if (invoice.getId() == null) {
            try {
                if (fileHelper.exists(path) && !fileHelper.isEmpty(path)) {
                    String lastInvoice = fileHelper.readLastLine(path);
                    Pattern pattern = Pattern.compile("\"invoiceId\":(\\d+)");
                    Matcher matcher = pattern.matcher(lastInvoice);
                    if (matcher.find()) {
                        newId = newId + Long.parseLong((matcher.group(1)));
                    }
                }
            } catch (IOException e) {
                throw new DatabaseOperationException("An error occurred during opening the file", e);
            }
        } else {
            newId = invoice.getId();
        }
        Invoice insertedInvoice = Invoice.builder()
            .withId(newId)
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
            throw new DatabaseOperationException("An error occurred during inserting the invoice.", e);
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
        try (LineIterator lineIterator = FileUtils.lineIterator(new File(path))) {
            int lineCount = 0;
            while (lineIterator.hasNext()) {
                lineCount++;
                String invoiceLine = lineIterator.nextLine();
                if (invoiceLine.contains("\"invoiceId\":" + invoice.getId())) {
                    fileHelper.replaceLine(path, mapper.writeValueAsString(updatedInvoice), lineCount);
                    break;
                }
            }
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during updating the invoice.", e);
        }
        return updatedInvoice;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        if (!exists(id)) {
            log.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        int lineCount = 0;
        try (LineIterator lineIterator = FileUtils.lineIterator(new File(path))) {
            while (lineIterator.hasNext()) {
                lineCount++;
                String nextInvoice = lineIterator.nextLine();
                if (nextInvoice.contains("\"invoiceId\":" + id)) {
                    fileHelper.removeLine(path, lineCount);
                    break;
                }
            }
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during deleting the invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by id providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        Optional<Invoice> invoice = Optional.empty();
        try (LineIterator lineIterator = FileUtils.lineIterator(new File(path))) {
            while (lineIterator.hasNext()) {
                String nextInvoice = lineIterator.nextLine();
                if (nextInvoice.contains("\"invoiceId\":" + id)) {
                    invoice = Optional.ofNullable(mapper.readValue(nextInvoice, Invoice.class));
                    break;
                }
            }
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
        return invoice;
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        Optional<Invoice> invoice = Optional.empty();
        try (LineIterator lineIterator = FileUtils.lineIterator(new File(path))) {
            while (lineIterator.hasNext()) {
                String nextInvoice = lineIterator.nextLine();
                if (nextInvoice.contains("\"number\":\"" + number)) {
                    invoice = Optional.ofNullable(mapper.readValue(nextInvoice, Invoice.class));
                    break;
                }
            }
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
        return invoice;
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        Collection<Invoice> invoices = new ArrayList<>(Collections.emptyList());
        try (LineIterator lineIterator = FileUtils.lineIterator(new File(path))) {
            while (lineIterator.hasNext()) {
                String nextInvoice = lineIterator.nextLine();
                invoices.add(mapper.readValue(nextInvoice, Invoice.class));
            }
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
        return invoices;
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            fileHelper.clear(path);
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred deleting getting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if invoice exists providing null id.");
            throw new IllegalArgumentException("Passed id cannot be null.");
        }
        if (fileHelper.exists(path)) {
            try (LineIterator lineIterator = FileUtils.lineIterator(new File(path))) {
                while (lineIterator.hasNext()) {
                    String nextInvoice = lineIterator.nextLine();
                    if (nextInvoice.contains("\"invoiceId\":" + id)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
            }
        }
        return false;
    }

    @Override
    public long count() throws DatabaseOperationException {
        long count = 0;
        try (LineIterator lineIterator = FileUtils.lineIterator(new File(path))) {
            while (lineIterator.hasNext()) {
                count++;
                lineIterator.nextLine();
            }
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during counting invoice entries.", e);
        }
        return count;
    }
}
