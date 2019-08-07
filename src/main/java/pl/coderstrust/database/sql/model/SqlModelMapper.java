package pl.coderstrust.database.sql.model;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SqlModelMapper {

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    Invoice toSqlInvoice(pl.coderstrust.model.Invoice invoice);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    pl.coderstrust.model.Invoice toInvoice(Invoice invoice);

    List<pl.coderstrust.model.Invoice> mapToInvoices(List<Invoice> invoices);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    Company toSqlCompany(pl.coderstrust.model.Company company);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    pl.coderstrust.model.Company toCompany(Company company);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntry toSqlInvoiceEntry(pl.coderstrust.model.InvoiceEntry invoiceEntry);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    pl.coderstrust.model.InvoiceEntry toInvoiceEntry(InvoiceEntry invoiceEntry);

    Vat toSqlVat(pl.coderstrust.model.Vat vat);

    pl.coderstrust.model.Vat toVat(Vat vat);
}
