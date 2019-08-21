package pl.coderstrust.database;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.nosql.model.Invoice;
import pl.coderstrust.database.nosql.model.NoSqlModelMapper;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "mongo")
public class MongoDatabase implements Database {

    private Logger log = LoggerFactory.getLogger(MongoDatabase.class);

    private MongoTemplate mongoTemplate;
    private NoSqlModelMapper noSqlModelMapper;
    private AtomicLong nextId = new AtomicLong(0);

    @Autowired
    public MongoDatabase(MongoTemplate mongoTemplate, NoSqlModelMapper noSqlModelMapper) {
        this.mongoTemplate = mongoTemplate;
        this.noSqlModelMapper = noSqlModelMapper;
        init();
    }

    private void init() {
        Query findQuery = new Query();
        findQuery.with(new Sort(Direction.DESC, "id"));
        Invoice lastInvoice = mongoTemplate.findOne(findQuery, Invoice.class);
        if (lastInvoice == null) {
            nextId = new AtomicLong(0);
        } else {
            nextId = new AtomicLong(lastInvoice.getId());
        }
    }

    @Override
    public pl.coderstrust.model.Invoice save(pl.coderstrust.model.Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            Invoice invoiceInDatabase = getInvoiceById(invoice.getId());
            if (invoiceInDatabase == null) {
                return insertInvoice(invoice);
            }
            return updateInvoice(invoice, invoiceInDatabase.getMongoId());
        } catch (Exception e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private pl.coderstrust.model.Invoice insertInvoice(pl.coderstrust.model.Invoice invoice) {
        pl.coderstrust.model.Invoice invoiceToInsert = pl.coderstrust.model.Invoice.builder()
            .withId(nextId.getAndIncrement())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();
        Invoice insertedInvoice = mongoTemplate.save(noSqlModelMapper.toNoSqlInvoice(invoiceToInsert));
        return noSqlModelMapper.toInvoice(insertedInvoice);
    }

    private pl.coderstrust.model.Invoice updateInvoice(pl.coderstrust.model.Invoice invoice, String mongoId) {
        Invoice mappedInvoice = noSqlModelMapper.toNoSqlInvoice(invoice);
        Invoice invoiceToUpdate = Invoice.builder()
            .withMongoId(mongoId)
            .withId(mappedInvoice.getId())
            .withNumber(mappedInvoice.getNumber())
            .withIssuedDate(mappedInvoice.getIssuedDate())
            .withDueDate(mappedInvoice.getDueDate())
            .withSeller(mappedInvoice.getSeller())
            .withBuyer(mappedInvoice.getBuyer())
            .withEntries(mappedInvoice.getEntries())
            .build();
        return noSqlModelMapper.toInvoice(mongoTemplate.save(invoiceToUpdate));
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice providing null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Invoice removedInvoice = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Invoice.class);
            if (removedInvoice == null) {
                log.error("Attempt to delete not existing invoice.");
                throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
            }
        } catch (Exception e) {
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
            Invoice invoice = getInvoiceById(id);
            if (invoice == null) {
                return Optional.empty();
            }
            return Optional.of(noSqlModelMapper.toInvoice(invoice));
        } catch (Exception e) {
            String message = "An error occurred during getting invoice by id.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private Invoice getInvoiceById(Long id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), Invoice.class);
    }

    @Override
    public Optional<pl.coderstrust.model.Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            Invoice invoice = mongoTemplate.findOne(Query.query(Criteria.where("number").is(number)), Invoice.class);
            if (invoice == null) {
                return Optional.empty();
            }
            return Optional.of(noSqlModelMapper.toInvoice(invoice));
        } catch (Exception e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Collection<pl.coderstrust.model.Invoice> getAll() throws DatabaseOperationException {
        try {
            return noSqlModelMapper.mapToInvoices(mongoTemplate.findAll(Invoice.class));
        } catch (Exception e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            mongoTemplate.dropCollection(Invoice.class);
        } catch (Exception e) {
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
            return mongoTemplate.exists(Query.query(Criteria.where("id").is(id)), Invoice.class);
        } catch (Exception e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return mongoTemplate.count(new Query(), Invoice.class);
        } catch (Exception e) {
            String message = "An error occurred during getting number of invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Collection<pl.coderstrust.model.Invoice> getByIssueDate(LocalDate startDate, LocalDate endDate) throws DatabaseOperationException {
        if(startDate==null || endDate==null){
            log.error("Attempt to get invoices from date interval without providing start date or end date");
            throw new IllegalArgumentException("Both start date and end date cannot be null");
        }
        if(startDate.isAfter(endDate)){
            log.error("Attempt to get invoices from date interval when passed start date is after end date");
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        try{
            List<pl.coderstrust.model.Invoice> allInvoices = noSqlModelMapper.mapToInvoices(mongoTemplate.findAll(Invoice.class));
            return allInvoices.stream()
                .filter(invoice -> invoice.getIssuedDate().compareTo(startDate) >= 0 && invoice.getIssuedDate().compareTo(endDate) <= 0)
                .collect(Collectors.toList());
        }catch (Exception e){
            String message = "An error occurred during filtering invoices by issued date.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }
}
