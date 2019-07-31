package pl.coderstrust.database.nosql.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.coderstrust.generators.CompanyGenerator;
import pl.coderstrust.generators.InvoiceEntryGenerator;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.generators.VatRateGenerator;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

class ModelMapperTest {

    private ModelMapper modelMapper = Mappers.getMapper(ModelMapper.class);

    @Test
    void shouldMapInvoiceToNoSqlInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.getRandomInvoice();

        //when
        pl.coderstrust.database.nosql.model.Invoice noSqlInvoice = modelMapper.toNoSqlInvoice(invoice);

        //then
        assertEquals(invoice.getId().toString(), noSqlInvoice.getId());
        assertEquals(invoice.getNumber(), noSqlInvoice.getNumber());
        assertEquals(invoice.getIssuedDate(), noSqlInvoice.getIssuedDate());
        assertEquals(invoice.getDueDate(), noSqlInvoice.getDueDate());
    }

    @Test
    void shouldMapCompanyToNoSqlCompany() {
        //given
        Company company = CompanyGenerator.getRandomCompany();

        //when
        pl.coderstrust.database.nosql.model.Company noSqlCompany = modelMapper.toNoSqlCompany(company);

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
        pl.coderstrust.database.nosql.model.InvoiceEntry noSqlInvoiceEntry = modelMapper.toNoSqlInvoiceEntry(invoiceEntry);

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
        pl.coderstrust.database.nosql.model.Vat noSqlVat = modelMapper.toNoSqlVat(vat);

        //then
        assertEquals(vat.getValue(), noSqlVat.getValue());
    }

    @Test
    void shouldMapNoSqlCompanyIdToZero() {
        //given
        Company company = CompanyGenerator.getRandomCompany();

        //when
        pl.coderstrust.database.nosql.model.Company noSqlCompany = modelMapper.toNoSqlCompany(company);
        Company sqlCompany = modelMapper.toSqlCompany(noSqlCompany);

        //then
        assertEquals(0, sqlCompany.getId());
    }

    @Test
    void shouldMapNoSqlInvoiceEntryIdToZero() {
        //given
        InvoiceEntry invoiceEntry = InvoiceEntryGenerator.getRandomEntry();

        //when
        pl.coderstrust.database.nosql.model.InvoiceEntry noSqlInvoiceEntry = modelMapper.toNoSqlInvoiceEntry(invoiceEntry);
        InvoiceEntry sqlInvoiceEntry = modelMapper.toSqlInvoiceEntry(noSqlInvoiceEntry);

        //then
        assertEquals(0, sqlInvoiceEntry.getId());
    }
}
