package pl.coderstrust.soap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;
import pl.coderstrust.service.ServiceOperationException;
import pl.coderstrust.soap.bindingclasses.CompanySoap;
import pl.coderstrust.soap.bindingclasses.EntriesList;
import pl.coderstrust.soap.bindingclasses.InvoiceEntrySoap;
import pl.coderstrust.soap.bindingclasses.InvoiceSoap;
import pl.coderstrust.soap.bindingclasses.VatSoap;

class ModelConverter {

    private static Logger log = LoggerFactory.getLogger(ModelConverter.class);

    private static final Pattern DATE_PATTERN = Pattern.compile("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]");

    static Invoice convertInvoiceSoapToInvoice(InvoiceSoap invoiceSoap) throws ServiceOperationException {
        if (!DATE_PATTERN.matcher(invoiceSoap.getIssuedDate()).matches() || !DATE_PATTERN.matcher(invoiceSoap.getDueDate()).matches()) {
            log.error("InvoiceSoap could not be converted to Invoice due to incorrect date format");
            throw new ServiceOperationException("Incorrect date formats");
        }

        LocalDate issuedDate = convertStringDateToDate(invoiceSoap.getIssuedDate());
        LocalDate dueDate = convertStringDateToDate(invoiceSoap.getDueDate());

        log.debug("Converting object of InvoiceSoap.class to Invoice");

        Invoice invoice = Invoice.builder()
            .withId(invoiceSoap.getId())
            .withNumber(invoiceSoap.getNumber())
            .withIssuedDate(issuedDate)
            .withDueDate(dueDate)
            .withSeller(convertCompanySoapToCompany(invoiceSoap.getSeller()))
            .withBuyer(convertCompanySoapToCompany(invoiceSoap.getBuyer()))
            .withEntries((List<InvoiceEntry>) convertEntriesListToInvoiceEntries(invoiceSoap.getEntries()))
            .build();
        return invoice;
    }

    private static LocalDate convertStringDateToDate(String stringDate) {
        log.debug("Converting string to date");
        int dateYear = Integer.parseInt(stringDate.substring(0, 4));
        int dateMonth = Integer.parseInt(stringDate.substring(5, 7));
        int dateDay = Integer.parseInt(stringDate.substring(8, 10));
        return LocalDate.of(dateYear, dateMonth, dateDay);
    }

    static InvoiceSoap convertInvoiceToInvoiceSoap(Invoice invoice) {
        log.debug("Converting invoice to invoice soap");
        InvoiceSoap invoiceSoap = new InvoiceSoap();
        invoiceSoap.setId(invoice.getId());
        invoiceSoap.setNumber(invoice.getNumber());
        invoiceSoap.setIssuedDate(invoice.getIssuedDate().toString());
        invoiceSoap.setDueDate(invoice.getDueDate().toString());
        invoiceSoap.setBuyer(convertCompanyToCompanySoap(invoice.getBuyer()));
        invoiceSoap.setSeller(convertCompanyToCompanySoap(invoice.getSeller()));
        List<InvoiceEntrySoap> invoiceEntriesSoap = convertInvoiceEntriesToInvoiceEntriesSoap(invoice.getEntries());
        invoiceSoap.setEntries(convertInvoiceEntriesSoapToEntriesList(invoiceEntriesSoap));
        return invoiceSoap;
    }

    static Collection<InvoiceSoap> convertInvoiceCollectionToInvoicesSoap(Collection<Invoice> invoiceCollection) {
        log.debug("Converting invoice collection to list of InvoiceSoap objects");
        Collection<InvoiceSoap> invoices = new ArrayList<>();
        for (Invoice invoice : invoiceCollection) {
            invoices.add(convertInvoiceToInvoiceSoap(invoice));
        }
        return invoices;
    }

    private static Company convertCompanySoapToCompany(CompanySoap companySoap) {
        log.debug("Converting CompanySoap object to Company");
        Company company = Company.builder()
            .withId(companySoap.getId())
            .withName(companySoap.getName())
            .withAddress(companySoap.getAddress())
            .withTaxId(companySoap.getTaxId())
            .withAccountNumber(companySoap.getAccountNumber())
            .withPhoneNumber(companySoap.getPhoneNumber())
            .withEmail(companySoap.getEmail())
            .build();
        return company;
    }

    private static CompanySoap convertCompanyToCompanySoap(Company company) {
        log.debug("Converting Company to CompanySoap object");
        CompanySoap companySoap = new CompanySoap();
        companySoap.setId(company.getId());
        companySoap.setName(company.getName());
        companySoap.setAddress(company.getAddress());
        companySoap.setTaxId(company.getTaxId());
        companySoap.setAccountNumber(company.getAccountNumber());
        companySoap.setPhoneNumber(company.getPhoneNumber());
        companySoap.setEmail(company.getEmail());
        return companySoap;
    }

    private static InvoiceEntry convertInvoiceEntrySoapToInvoiceEntry(InvoiceEntrySoap invoiceEntrySoap) {
        log.debug("Converting InvoiceEntrySoap object to InvoiceEntry");
        InvoiceEntry invoiceEntry = InvoiceEntry.builder()
            .withId(invoiceEntrySoap.getId())
            .withDescription(invoiceEntrySoap.getDescription())
            .withQuantity(invoiceEntrySoap.getQuantity())
            .withPrice(invoiceEntrySoap.getPrice())
            .withNetValue(invoiceEntrySoap.getNetValue())
            .withGrossValue(invoiceEntrySoap.getGrossValue())
            .withVatRate(convertVatSoapToVat(invoiceEntrySoap.getVatRate()))
            .build();
        return invoiceEntry;
    }

    private static InvoiceEntrySoap convertInvoiceEntryToInvoiceEntrySoap(InvoiceEntry invoiceEntry) {
        log.debug("Converting InvoiceEntry to object of InvoiceEntrySoap.class");
        InvoiceEntrySoap invoiceEntrySoap = new InvoiceEntrySoap();
        invoiceEntrySoap.setId(invoiceEntry.getId());
        invoiceEntrySoap.setDescription(invoiceEntry.getDescription());
        invoiceEntrySoap.setQuantity(invoiceEntry.getQuantity());
        invoiceEntrySoap.setPrice(invoiceEntry.getPrice());
        invoiceEntrySoap.setNetValue(invoiceEntry.getNetValue());
        invoiceEntrySoap.setGrossValue(invoiceEntry.getGrossValue());
        invoiceEntrySoap.setVatRate(convertVatToVatSoap(invoiceEntry.getVatRate()));
        return invoiceEntrySoap;
    }

    private static List<InvoiceEntrySoap> convertInvoiceEntriesToInvoiceEntriesSoap(Collection<InvoiceEntry> invoiceEntries) {
        log.debug("Converting collection of InvoiceEntries to list of InvoiceEntrySoap objects");
        List<InvoiceEntrySoap> invoiceEntriesSoap = new ArrayList<>();
        for (InvoiceEntry invoiceEntry : invoiceEntries) {
            invoiceEntriesSoap.add(convertInvoiceEntryToInvoiceEntrySoap(invoiceEntry));
        }
        return invoiceEntriesSoap;
    }

    private static EntriesList convertInvoiceEntriesSoapToEntriesList(List<InvoiceEntrySoap> invoiceEntrySoapList) {
        log.debug("Converting list of InvoiceEntrySoap.class objects to EntriesList");
        EntriesList entriesList = new EntriesList();
        List<InvoiceEntrySoap> entries = entriesList.getEntry();
        for (InvoiceEntrySoap invoiceEntry : invoiceEntrySoapList) {
            entries.add(invoiceEntry);
        }
        return entriesList;
    }

    private static Collection<InvoiceEntry> convertEntriesListToInvoiceEntries(EntriesList entriesList) {
        log.debug("Converting EntriesList to list of InvoiceEntrySoap objects");
        List<InvoiceEntrySoap> invoiceEntrySoapList = new ArrayList<>();
        for (InvoiceEntrySoap invoiceEntrySoap : entriesList.getEntry()) {
            ((ArrayList<InvoiceEntrySoap>) invoiceEntrySoapList).add(invoiceEntrySoap);
        }
        return convertInvoiceEntriesSoapToInvoiceEntries(invoiceEntrySoapList);
    }

    private static Collection<InvoiceEntry> convertInvoiceEntriesSoapToInvoiceEntries(Collection<InvoiceEntrySoap> invoiceEntriesSoap) {
        log.debug("Converting collection of InvoiceEntrySoap objects to collections of InvoiceEntries");
        Collection<InvoiceEntry> invoiceEntries = new ArrayList<>();
        for (InvoiceEntrySoap invoiceEntrySoap : invoiceEntriesSoap) {
            invoiceEntries.add(convertInvoiceEntrySoapToInvoiceEntry(invoiceEntrySoap));
        }
        return invoiceEntries;
    }

    private static Vat convertVatSoapToVat(VatSoap vatSoap) {
        log.debug("Converting VatSoap to Vat");
        switch (vatSoap) {
          case VAT_0:
              return Vat.VAT_0;
          case VAT_5:
              return Vat.VAT_5;
          case VAT_8:
              return Vat.VAT_8;
          default:
              return Vat.VAT_23;
        }
    }

    private static VatSoap convertVatToVatSoap(Vat vat) {
        log.debug("Converting Vat to VatSoap");
        switch (vat) {
          case VAT_0:
              return VatSoap.VAT_0;
          case VAT_5:
              return VatSoap.VAT_5;
          case VAT_8:
              return VatSoap.VAT_8;
          default:
              return VatSoap.VAT_23;
        }
    }
}
