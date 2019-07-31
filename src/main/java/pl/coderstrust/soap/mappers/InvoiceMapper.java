package pl.coderstrust.soap.mappers;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.soap.bindingclasses.InvoiceSoap;

public class InvoiceMapper {

    public static Invoice mapInvoice(InvoiceSoap invoiceSoap) {
        Long id = invoiceSoap.getId() == null ? null : invoiceSoap.getId().getValue();
        Invoice mappedInvoice = Invoice.builder()
            .withId(id)
            .withNumber(invoiceSoap.getNumber())
            .withIssuedDate(invoiceSoap.getIssuedDate().toGregorianCalendar().toZonedDateTime().toLocalDate())
            .withDueDate(invoiceSoap.getDueDate().toGregorianCalendar().toZonedDateTime().toLocalDate())
            .withSeller(CompanyMapper.mapCompany(invoiceSoap.getSeller()))
            .withBuyer(CompanyMapper.mapCompany(invoiceSoap.getBuyer()))
            .withEntries(InvoiceEntryMapper.mapInvoiceEntries(invoiceSoap.getEntries()))
            .build();
        return mappedInvoice;
    }

    public static InvoiceSoap mapInvoice(Invoice invoice) throws DatatypeConfigurationException {
        InvoiceSoap mappedInvoice = new InvoiceSoap();
        JAXBElement<Long> id = new JAXBElement<>(QName.valueOf("ns2:id"), Long.class, invoice.getId());
        mappedInvoice.setId(id);
        mappedInvoice.setNumber(invoice.getNumber());
        mappedInvoice.setIssuedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(invoice.getIssuedDate().toString()));
        mappedInvoice.setDueDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(invoice.getDueDate().toString()));
        mappedInvoice.setBuyer(CompanyMapper.mapCompany(invoice.getBuyer()));
        mappedInvoice.setSeller(CompanyMapper.mapCompany(invoice.getSeller()));
        mappedInvoice.setEntries(InvoiceEntryMapper.mapInvoiceEntries(invoice.getEntries()));
        return mappedInvoice;
    }

    public static Collection<InvoiceSoap> mapInvoices(Collection<Invoice> invoices) throws DatatypeConfigurationException {
        Collection<InvoiceSoap> mappedInvoices = new ArrayList<>();
        for (Invoice invoice : invoices) {
            mappedInvoices.add(mapInvoice(invoice));
        }
        return mappedInvoices;
    }
}
