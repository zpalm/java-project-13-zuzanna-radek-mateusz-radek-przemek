package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.coderstrust.database.nosql.model.Invoice;
import pl.coderstrust.database.nosql.model.NoSqlModelMapper;
import pl.coderstrust.database.nosql.model.NoSqlModelMapperImpl;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.generators.NoSqlInvoiceGenerator;

@ExtendWith(MockitoExtension.class)
class MongoDatabaseTest {

    @Mock
    private MongoTemplate mongoTemplate;
    private NoSqlModelMapper noSqlModelMapper = new NoSqlModelMapperImpl();
    private MongoDatabase mongoDatabase;

    @BeforeEach
    void setUp() {
        mongoDatabase = new MongoDatabase(mongoTemplate, noSqlModelMapper);
    }

    @Test
    void shouldSaveInvoice() throws DatabaseOperationException {
        //given
        Invoice noSqlSavedInvoice = NoSqlInvoiceGenerator.getRandomInvoice();
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(noSqlSavedInvoice.getId()));
        pl.coderstrust.model.Invoice savedInvoice = noSqlModelMapper.toInvoice(noSqlSavedInvoice);
        when(mongoTemplate.findOne(findQuery, Invoice.class)).thenReturn(null);
        when(mongoTemplate.save(any(Invoice.class))).thenReturn(noSqlSavedInvoice);

        //when
        pl.coderstrust.model.Invoice result = mongoDatabase.save(savedInvoice);

        //then
        assertEquals(savedInvoice, result);
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
        verify(mongoTemplate).save(any(Invoice.class));
    }

    @Test
    void saveMethodShouldThrowIllegalArgumentExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> mongoDatabase.save(null));
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException {
        //given
        Invoice noSqlInvoiceToUpdate = NoSqlInvoiceGenerator.getRandomInvoice();
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(noSqlInvoiceToUpdate.getId()));
        pl.coderstrust.model.Invoice updatedInvoice = noSqlModelMapper.toInvoice(noSqlInvoiceToUpdate);
        when(mongoTemplate.findOne(findQuery, Invoice.class)).thenReturn(noSqlInvoiceToUpdate);
        when(mongoTemplate.save(any(Invoice.class))).thenReturn(noSqlInvoiceToUpdate);

        //when
        pl.coderstrust.model.Invoice result = mongoDatabase.save(updatedInvoice);

        //then
        assertEquals(updatedInvoice, result);
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
        verify(mongoTemplate).save(any(Invoice.class));
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringSavingInvoice() {
        //given
        pl.coderstrust.model.Invoice invoice = InvoiceGenerator.getRandomInvoice();
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(invoice.getId()));
        when(mongoTemplate.findOne(findQuery, Invoice.class)).thenThrow(new MongoException(""));

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.save(invoice));
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        //given
        Invoice invoice = NoSqlInvoiceGenerator.getRandomInvoice();
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(1L));
        when(mongoTemplate.findAndRemove(findQuery, Invoice.class)).thenReturn(invoice);

        //when
        mongoDatabase.delete(1L);

        //then
        verify(mongoTemplate).findAndRemove(findQuery, Invoice.class);
    }

    @Test
    void deleteMethodShouldThrowIllegalArgumentExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> mongoDatabase.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingInvoice() {
        //given
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(1L));
        when(mongoTemplate.findAndRemove(findQuery, Invoice.class)).thenReturn(null);

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.delete(1L));
        verify(mongoTemplate).findAndRemove(findQuery, Invoice.class);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException {
        //given
        Invoice noSqlInvoice = NoSqlInvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice invoice = noSqlModelMapper.toInvoice(noSqlInvoice);
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(noSqlInvoice.getId()));
        when(mongoTemplate.findOne(findQuery, Invoice.class)).thenReturn(noSqlInvoice);

        //when
        Optional<pl.coderstrust.model.Invoice> result = mongoDatabase.getById(invoice.getId());

        //then
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceById() throws DatabaseOperationException {
        //when
        Optional<pl.coderstrust.model.Invoice> invoice = mongoDatabase.getById(1L);
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(1L));

        //then
        assertTrue(invoice.isEmpty());
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
    }

    @Test
    void getByIdMethodShouldThrowIllegalArgumentExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> mongoDatabase.getById(null));
    }

    @Test
    void getByIdMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingInvoiceById() {
        //given
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("id").is(1L));
        when(mongoTemplate.findOne(findQuery, Invoice.class)).thenThrow(new MongoException(""));

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.getById(1L));
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //given
        Invoice noSqlInvoice = NoSqlInvoiceGenerator.getRandomInvoice();
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("number").is("123"));
        pl.coderstrust.model.Invoice invoice = noSqlModelMapper.toInvoice(noSqlInvoice);
        when(mongoTemplate.findOne(findQuery, Invoice.class)).thenReturn(noSqlInvoice);

        //when
        Optional<pl.coderstrust.model.Invoice> result = mongoDatabase.getByNumber("123");

        //then
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceByNumber() throws DatabaseOperationException {
        //when
        Optional<pl.coderstrust.model.Invoice> invoice = mongoDatabase.getByNumber("123");
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("number").is("123"));

        //then
        assertTrue(invoice.isEmpty());
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
    }

    @Test
    void getByNumberMethodShouldThrowIllegalArgumentExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> mongoDatabase.getByNumber(null));
    }

    @Test
    void getByNumberMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingInvoiceByNumber() {
        //given
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("number").is("123"));
        when(mongoTemplate.findOne(findQuery, Invoice.class)).thenThrow(new MongoException(""));

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.getByNumber("123"));
        verify(mongoTemplate).findOne(findQuery, Invoice.class);
    }

    @Test
    void shouldReturnAllInvoices() throws DatabaseOperationException {
        //given
        List<Invoice> noSqlInvoices = List.of(NoSqlInvoiceGenerator.getRandomInvoice(), NoSqlInvoiceGenerator.getRandomInvoice());
        List<pl.coderstrust.model.Invoice> invoices = noSqlModelMapper.mapToInvoices(noSqlInvoices);
        when(mongoTemplate.findAll(Invoice.class)).thenReturn(noSqlInvoices);

        //when
        Collection<pl.coderstrust.model.Invoice> result = mongoDatabase.getAll();

        //then
        assertEquals(invoices, result);
        verify(mongoTemplate).findAll(Invoice.class);
    }

    @Test
    void getAllMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingAllInvoices() {
        //given
        when(mongoTemplate.findAll(Invoice.class)).thenThrow(new MongoException(""));

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.getAll());
        verify(mongoTemplate).findAll(Invoice.class);
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        //given
        doNothing().when(mongoTemplate).dropCollection(Invoice.class);

        //when
        mongoDatabase.deleteAll();

        //then
        verify(mongoTemplate).dropCollection(Invoice.class);
    }

    @Test
    void deleteAllMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringDeletingAllInvoices() {
        //given
        doThrow(new MongoException("")).when(mongoTemplate).dropCollection(Invoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.deleteAll());
        verify(mongoTemplate).dropCollection(Invoice.class);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        //given
        Query existsQuery = new Query();
        existsQuery.addCriteria(Criteria.where("id").is(1L));
        when(mongoTemplate.exists(existsQuery, Invoice.class)).thenReturn(true);

        //then
        assertTrue(mongoDatabase.exists(1L));
        verify(mongoTemplate).exists(existsQuery, Invoice.class);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws DatabaseOperationException {
        //given
        Query existsQuery = new Query();
        existsQuery.addCriteria(Criteria.where("id").is(1L));
        when(mongoTemplate.exists(existsQuery, Invoice.class)).thenReturn(false);

        //then
        assertFalse(mongoDatabase.exists(1L));
        verify(mongoTemplate).exists(existsQuery, Invoice.class);
    }

    @Test
    void existsMethodShouldThrowIllegalArgumentExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> mongoDatabase.exists(null));
    }

    @Test
    void existsMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringCheckingIfInvoiceExists() {
        //given
        Query existsQuery = new Query();
        existsQuery.addCriteria(Criteria.where("id").is(1L));
        doThrow(new MongoException("")).when(mongoTemplate).exists(existsQuery, Invoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.exists(1L));
        verify(mongoTemplate).exists(existsQuery, Invoice.class);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //given
        Query countQuery = new Query();
        when(mongoTemplate.count(countQuery, Invoice.class)).thenReturn(1L);

        //when
        long result = mongoDatabase.count();

        //then
        assertEquals(1L, result);
        verify(mongoTemplate).count(countQuery, Invoice.class);
    }

    @Test
    void countMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingNumberOfInvoices() {
        //given
        Query countQuery = new Query();
        doThrow(new MongoException("")).when(mongoTemplate).count(countQuery, Invoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> mongoDatabase.count());
        verify(mongoTemplate).count(countQuery, Invoice.class);
    }
}
