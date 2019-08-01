package pl.coderstrust.database.nosql.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.coderstrust.generators.CompanyGenerator;
import pl.coderstrust.generators.InvoiceEntryGenerator;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.generators.NoSqlCompanyGenerator;
import pl.coderstrust.generators.NoSqlInvoiceEntryGenerator;
import pl.coderstrust.generators.NoSqlInvoiceGenerator;
import pl.coderstrust.generators.VatRateGenerator;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

class NoSqlModelMapperTest {

    private NoSqlModelMapper noSqlModelMapper = Mappers.getMapper(NoSqlModelMapper.class);

    @Test
    void shouldMapInvoiceToNoSqlInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();

        //when
        pl.coderstrust.database.nosql.model.Invoice noSqlInvoice = noSqlModelMapper.toNoSqlInvoice(invoice);

        //then
        assertEquals(invoice.getId(), noSqlInvoice.getId());
        assertEquals(invoice.getNumber(), noSqlInvoice.getNumber());
        assertEquals(invoice.getIssuedDate(), noSqlInvoice.getIssuedDate());
        assertEquals(invoice.getDueDate(), noSqlInvoice.getDueDate());
    }

    @Test
    void shouldMapCompanyToNoSqlCompany() {
        //given
        Company company = CompanyGenerator.getRandomCompany();

        //when
        pl.coderstrust.database.nosql.model.Company noSqlCompany = noSqlModelMapper.toNoSqlCompany(company);

        //then
        assertEquals(company.getName(), noSqlCompany.getName());
        assertEquals(company.getAddress(), noSqlCompany.getAddress());
        assertEquals(company.getTaxId(), noSqlCompany.getTaxId());
        assertEquals(company.getAccountNumber(), noSqlCompany.getAccountNumber());
        assertEquals(company.getPhoneNumber(), noSqlCompany.getPhoneNumber());
        assertEquals(company.getEmail(), noSqlCompany.getEmail());
    }

    @Test
    void shouldMapInvoiceEntryToNoSqlInvoiceEntry() {
        //given
        InvoiceEntry invoiceEntry = InvoiceEntryGenerator.getRandomEntry();

        //when
        pl.coderstrust.database.nosql.model.InvoiceEntry noSqlInvoiceEntry = noSqlModelMapper.toNoSqlInvoiceEntry(invoiceEntry);

        //then
        assertEquals(invoiceEntry.getDescription(), noSqlInvoiceEntry.getDescription());
        assertEquals(invoiceEntry.getQuantity(), noSqlInvoiceEntry.getQuantity());
        assertEquals(invoiceEntry.getPrice(), noSqlInvoiceEntry.getPrice());
        assertEquals(invoiceEntry.getNetValue(), noSqlInvoiceEntry.getNetValue());
        assertEquals(invoiceEntry.getGrossValue(), noSqlInvoiceEntry.getGrossValue());
    }

    @Test
    void shouldMapVatToNoSqlVat() {
        //given
        Vat vat = VatRateGenerator.getRandomVatRate(Vat.class);

        //when
        pl.coderstrust.database.nosql.model.Vat noSqlVat = noSqlModelMapper.toNoSqlVat(vat);

        //then
        assertEquals(vat.getValue(), noSqlVat.getValue());
    }

    @Test
    void shouldMapNoSqlInvoiceToInvoice() {
        //given
        pl.coderstrust.database.nosql.model.Invoice noSqlInvoice = NoSqlInvoiceGenerator.getRandomInvoice();

        //when
        Invoice invoice = noSqlModelMapper.toInvoice(noSqlInvoice);

        //then
        assertEquals(noSqlInvoice.getId(), invoice.getId());
        assertEquals(noSqlInvoice.getNumber(), invoice.getNumber());
        assertEquals(noSqlInvoice.getIssuedDate(), invoice.getIssuedDate());
        assertEquals(noSqlInvoice.getDueDate(), invoice.getDueDate());
    }

    @Test
    void shouldMapNoSqlCompanyToCompany() {
        //given
        pl.coderstrust.database.nosql.model.Company noSqlCompany = NoSqlCompanyGenerator.getRandomCompany();

        //when
        Company company = noSqlModelMapper.toCompany(noSqlCompany);

        //then
        assertEquals(noSqlCompany.getName(), company.getName());
        assertEquals(noSqlCompany.getAddress(), company.getAddress());
        assertEquals(noSqlCompany.getTaxId(), company.getTaxId());
        assertEquals(noSqlCompany.getAccountNumber(), company.getAccountNumber());
        assertEquals(noSqlCompany.getPhoneNumber(), company.getPhoneNumber());
        assertEquals(noSqlCompany.getEmail(), company.getEmail());
    }

    @Test
    void shouldMapNoSqlInvoiceEntryToInvoiceEntry() {
        //given
        pl.coderstrust.database.nosql.model.InvoiceEntry noSqlInvoiceEntry = NoSqlInvoiceEntryGenerator.getRandomEntry();

        //when
        InvoiceEntry invoiceEntry = noSqlModelMapper.toInvoiceEntry(noSqlInvoiceEntry);

        //then
        assertEquals(noSqlInvoiceEntry.getDescription(), invoiceEntry.getDescription());
        assertEquals(noSqlInvoiceEntry.getQuantity(), invoiceEntry.getQuantity());
        assertEquals(noSqlInvoiceEntry.getPrice(), invoiceEntry.getPrice());
        assertEquals(noSqlInvoiceEntry.getNetValue(), invoiceEntry.getNetValue());
        assertEquals(noSqlInvoiceEntry.getGrossValue(), invoiceEntry.getGrossValue());
    }

    @Test
    void shouldMapNoSqlVatToVat() {
        //given
        pl.coderstrust.database.nosql.model.Vat noSqlVat = VatRateGenerator.getRandomVatRate(pl.coderstrust.database.nosql.model.Vat.class);

        //when
        Vat vat = noSqlModelMapper.toVat(noSqlVat);

        //then
        assertEquals(noSqlVat.getValue(), vat.getValue());
    }
}
