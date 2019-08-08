package pl.coderstrust.database.sql.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.coderstrust.generators.CompanyGenerator;
import pl.coderstrust.generators.InvoiceEntryGenerator;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.generators.SqlCompanyGenerator;
import pl.coderstrust.generators.SqlInvoiceEntryGenerator;
import pl.coderstrust.generators.SqlInvoiceGenerator;
import pl.coderstrust.generators.VatRateGenerator;

public class SqlModelMapperTest {

    private SqlModelMapper sqlModelMapper = Mappers.getMapper(SqlModelMapper.class);

    @Test
    void shouldMapInvoiceToSqlInvoice() {
        pl.coderstrust.model.Invoice invoice = InvoiceGenerator.getRandomInvoice();

        Invoice sqlInvoice = sqlModelMapper.toSqlInvoice(invoice);

        assertEquals(invoice.getId(), sqlInvoice.getId());
        assertEquals(invoice.getNumber(), sqlInvoice.getNumber());
        assertEquals(invoice.getIssuedDate(), sqlInvoice.getIssuedDate());
        assertEquals(invoice.getDueDate(), sqlInvoice.getDueDate());
    }

    @Test
    void shouldMapSqlInvoiceToInvoice() {
        Invoice sqlInvoice = SqlInvoiceGenerator.getRandomInvoice();

        pl.coderstrust.model.Invoice invoice = sqlModelMapper.toInvoice(sqlInvoice);

        assertEquals(sqlInvoice.getId(), invoice.getId());
        assertEquals(sqlInvoice.getNumber(), invoice.getNumber());
        assertEquals(sqlInvoice.getIssuedDate(), invoice.getIssuedDate());
        assertEquals(sqlInvoice.getDueDate(), invoice.getDueDate());
    }

    @Test
    void  shouldMapInvoiceEntryToSqlInvoiceEntry() {
        pl.coderstrust.model.InvoiceEntry invoiceEntry = InvoiceEntryGenerator.getRandomEntry();

        InvoiceEntry sqlInvoiceEntry = sqlModelMapper.toSqlInvoiceEntry(invoiceEntry);

        assertEquals(invoiceEntry.getId(), sqlInvoiceEntry.getId());
        assertEquals(invoiceEntry.getDescription(), sqlInvoiceEntry.getDescription());
        assertEquals(invoiceEntry.getQuantity(), sqlInvoiceEntry.getQuantity());
        assertEquals(invoiceEntry.getPrice(), sqlInvoiceEntry.getPrice());
        assertEquals(invoiceEntry.getNetValue(), sqlInvoiceEntry.getNetValue());
        assertEquals(invoiceEntry.getGrossValue(), sqlInvoiceEntry.getGrossValue());
    }

    @Test
    void  shouldMapSqlInvoiceEntryToInvoiceEntry() {
        InvoiceEntry sqlInvoiceEntry = SqlInvoiceEntryGenerator.getRandomEntry();

        pl.coderstrust.model.InvoiceEntry invoiceEntry = sqlModelMapper.toInvoiceEntry(sqlInvoiceEntry);

        assertEquals(sqlInvoiceEntry.getId(), invoiceEntry.getId());
        assertEquals(sqlInvoiceEntry.getDescription(), invoiceEntry.getDescription());
        assertEquals(sqlInvoiceEntry.getQuantity(), invoiceEntry.getQuantity());
        assertEquals(sqlInvoiceEntry.getPrice(), invoiceEntry.getPrice());
        assertEquals(sqlInvoiceEntry.getNetValue(), invoiceEntry.getNetValue());
        assertEquals(sqlInvoiceEntry.getGrossValue(), invoiceEntry.getGrossValue());
    }

    @Test
    void  shouldMapCompanyToSqlCompany() {
        pl.coderstrust.model.Company company = CompanyGenerator.getRandomCompany();

        Company sqlCompany = sqlModelMapper.toSqlCompany(company);

        assertEquals(company.getId(), sqlCompany.getId());
        assertEquals(company.getName(), sqlCompany.getName());
        assertEquals(company.getAddress(), sqlCompany.getAddress());
        assertEquals(company.getTaxId(), sqlCompany.getTaxId());
        assertEquals(company.getAccountNumber(), sqlCompany.getAccountNumber());
        assertEquals(company.getPhoneNumber(), sqlCompany.getPhoneNumber());
        assertEquals(company.getEmail(), sqlCompany.getEmail());
    }

    @Test
    void  shouldMapSqlCompanyToCompany() {
        Company sqlCompany = SqlCompanyGenerator.getRandomCompany();

        pl.coderstrust.model.Company company = sqlModelMapper.toCompany(sqlCompany);

        assertEquals(sqlCompany.getId(), company.getId());
        assertEquals(sqlCompany.getName(), company.getName());
        assertEquals(sqlCompany.getAddress(), company.getAddress());
        assertEquals(sqlCompany.getTaxId(), company.getTaxId());
        assertEquals(sqlCompany.getAccountNumber(), company.getAccountNumber());
        assertEquals(sqlCompany.getPhoneNumber(), company.getPhoneNumber());
        assertEquals(sqlCompany.getEmail(), company.getEmail());
    }

    @Test
    void  shouldMapVatToSqlVat() {
        pl.coderstrust.model.Vat vat = VatRateGenerator.getRandomVatRate(pl.coderstrust.model.Vat.class);

        Vat sqlVat = sqlModelMapper.toSqlVat(vat);

        assertEquals(vat.getValue(), sqlVat.getValue());
    }

    @Test
    void  shouldMapSqlVatToVat() {
        Vat sqlVat = VatRateGenerator.getRandomVatRate(Vat.class);

        pl.coderstrust.model.Vat vat = sqlModelMapper.toVat(sqlVat);

        assertEquals(sqlVat.getValue(), vat.getValue());
    }
}
