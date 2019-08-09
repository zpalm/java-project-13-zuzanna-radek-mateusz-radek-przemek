package pl.coderstrust.database;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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
        try {
            Invoice lastInvoice = mongoTemplate.findOne(findQuery, Invoice.class);
            if (lastInvoice == null) {
                this.nextId = new AtomicLong(0);
            } else {
                this.nextId = new AtomicLong(lastInvoice.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public pl.coderstrust.model.Invoice save(pl.coderstrust.model.Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to save null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(invoice.getId()));
        if (invoice.getId() == null || !mongoTemplate.exists(findQuery, Invoice.class)) {
            return insertInvoice(invoice);
        }
        return updateInvoice(invoice);
    }

    private pl.coderstrust.model.Invoice insertInvoice(pl.coderstrust.model.Invoice invoice) throws DatabaseOperationException {
        pl.coderstrust.model.Invoice invoiceToInsert = pl.coderstrust.model.Invoice.builder()
            .withId(nextId.getAndIncrement())
            .withNumber(invoice.getNumber())
            .withIssuedDate(invoice.getIssuedDate())
            .withDueDate(invoice.getDueDate())
            .withSeller(invoice.getSeller())
            .withBuyer(invoice.getBuyer())
            .withEntries(invoice.getEntries())
            .build();
        Invoice noSqlInvoiceToInsert = noSqlModelMapper.toNoSqlInvoice(invoiceToInsert);
        try {
            Invoice noSqlInsertedInvoice = mongoTemplate.save(noSqlInvoiceToInsert);
            return noSqlModelMapper.toInvoice(noSqlInsertedInvoice);
        } catch (Exception e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private pl.coderstrust.model.Invoice updateInvoice(pl.coderstrust.model.Invoice invoice) throws DatabaseOperationException {
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(invoice.getId()));
        Invoice existingNoSqlInvoice = mongoTemplate.findOne(findQuery, Invoice.class);
        Invoice noSqlInvoiceToUpdate = noSqlModelMapper.toNoSqlInvoice(invoice);
        Invoice updatedNoSqlInvoice = Invoice.builder()
            .withMongoId(existingNoSqlInvoice.getMongoId())
            .withId(noSqlInvoiceToUpdate.getId())
            .withNumber(noSqlInvoiceToUpdate.getNumber())
            .withIssuedDate(noSqlInvoiceToUpdate.getIssuedDate())
            .withDueDate(noSqlInvoiceToUpdate.getDueDate())
            .withSeller(noSqlInvoiceToUpdate.getSeller())
            .withBuyer(noSqlInvoiceToUpdate.getBuyer())
            .withEntries(noSqlInvoiceToUpdate.getEntries())
            .build();
        try {
            Invoice updatedInvoice = mongoTemplate.save(updatedNoSqlInvoice);
            return noSqlModelMapper.toInvoice(updatedInvoice);
        } catch (Exception e) {
            String message = "An error occurred during updating invoice.";
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
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(id));
        if (!mongoTemplate.exists(findQuery, Invoice.class)) {
            log.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There was no invoice in database with id: %s", id));
        }
        try {
            mongoTemplate.remove(findQuery, Invoice.class);
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
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(id));
        try {
            Invoice invoice = mongoTemplate.findOne(findQuery, Invoice.class);
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

    @Override
    public Optional<pl.coderstrust.model.Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by number providing null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("number").is(number));
        try {
            Invoice invoice = mongoTemplate.findOne(findQuery, Invoice.class);
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
            List<Invoice> noSqlInvoices = mongoTemplate.findAll(Invoice.class);
            return noSqlModelMapper.mapToInvoices(noSqlInvoices);
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
        Query existsQuery = new Query();
        existsQuery.addCriteria(Criteria.where("id").is(id));
        try {
            return mongoTemplate.exists(existsQuery, Invoice.class);
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
}
