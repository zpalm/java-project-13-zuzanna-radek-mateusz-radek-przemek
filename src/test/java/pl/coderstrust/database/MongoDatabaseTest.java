package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        pl.coderstrust.model.Invoice invoiceToSave = InvoiceGenerator.getRandomInvoice();
        pl.coderstrust.model.Invoice savedInvoice = InvoiceGenerator.getRandomInvoice();
        Invoice noSqlInvoiceToSave = noSqlModelMapper.toNoSqlInvoice(invoiceToSave);
        Invoice noSqlSavedInvoice = noSqlModelMapper.toNoSqlInvoice(savedInvoice);
        Query existsQuery = new Query();
        existsQuery.addCriteria(Criteria.where("id").is(invoiceToSave.getId()));
        when(mongoTemplate.exists(existsQuery, Invoice.class)).thenReturn(false);
        when(mongoTemplate.save(noSqlInvoiceToSave)).thenReturn(noSqlSavedInvoice);

        //when
        pl.coderstrust.model.Invoice result = mongoDatabase.save(invoiceToSave);

        //then
        assertEquals(savedInvoice, result);
        verify(mongoTemplate).exists(any(Query.class), Invoice.class);
        verify(mongoTemplate).insert(noSqlInvoiceToSave);
    }
}
