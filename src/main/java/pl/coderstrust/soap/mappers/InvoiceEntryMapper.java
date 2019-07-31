package pl.coderstrust.soap.mappers;

import java.util.List;
import java.util.stream.Collectors;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.soap.bindingclasses.EntriesList;
import pl.coderstrust.soap.bindingclasses.InvoiceEntrySoap;

public class InvoiceEntryMapper {

    private static InvoiceEntry mapInvoiceEntry(InvoiceEntrySoap invoiceEntrySoap) {
        InvoiceEntry invoiceEntry = InvoiceEntry.builder()
            .withId(invoiceEntrySoap.getId())
            .withDescription(invoiceEntrySoap.getDescription())
            .withQuantity(invoiceEntrySoap.getQuantity())
            .withPrice(invoiceEntrySoap.getPrice())
            .withNetValue(invoiceEntrySoap.getNetValue())
            .withGrossValue(invoiceEntrySoap.getGrossValue())
            .withVatRate(VatMapper.mapVat(invoiceEntrySoap.getVatRate()))
            .build();
        return invoiceEntry;
    }

    private static InvoiceEntrySoap mapInvoiceEntry(InvoiceEntry invoiceEntry) {
        InvoiceEntrySoap invoiceEntrySoap = new InvoiceEntrySoap();
        invoiceEntrySoap.setId(invoiceEntry.getId());
        invoiceEntrySoap.setDescription(invoiceEntry.getDescription());
        invoiceEntrySoap.setQuantity(invoiceEntry.getQuantity());
        invoiceEntrySoap.setPrice(invoiceEntry.getPrice());
        invoiceEntrySoap.setNetValue(invoiceEntry.getNetValue());
        invoiceEntrySoap.setGrossValue(invoiceEntry.getGrossValue());
        invoiceEntrySoap.setVatRate(VatMapper.mapVat(invoiceEntry.getVatRate()));
        return invoiceEntrySoap;
    }

    public static List<InvoiceEntry> mapInvoiceEntries(EntriesList entriesList) {
        return entriesList.getEntry().stream().map(InvoiceEntryMapper::mapInvoiceEntry).collect(Collectors.toList());
    }

    public static EntriesList mapInvoiceEntries(List<InvoiceEntry> invoiceEntries) {
        List<InvoiceEntrySoap> mappedInvoiceEntries = invoiceEntries.stream().map(InvoiceEntryMapper::mapInvoiceEntry).collect(Collectors.toList());
        EntriesList entriesList = new EntriesList();
        entriesList.getEntry().addAll(mappedInvoiceEntries);
        return entriesList;
    }
}
