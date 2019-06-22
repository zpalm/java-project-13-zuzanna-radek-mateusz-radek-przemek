package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Invoice {

    private final Long id;
    private final String number;
    private final LocalDate issuedDate;
    private final LocalDate dueDate;
    private final Company companyFrom;
    private final Company companyTo;
    private final List<InvoiceEntry> invoiceEntries = new ArrayList<>();

    public Invoice(Long id, String number, LocalDate issuedDate, LocalDate dueDate,
                   Company companyFrom, Company companyTo) {
        this.id = id;
        this.number = number;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.companyFrom = companyFrom;
        this.companyTo = companyTo;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Company getCompanyFrom() {
        return companyFrom;
    }

    public Company getCompanyTo() {
        return companyTo;
    }

    public List<InvoiceEntry> getInvoiceEntries() {
        return new ArrayList<>(invoiceEntries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice)) return false;
        Invoice invoice = (Invoice) o;
        if (!id.equals(invoice.id)) return false;
        if (!number.equals(invoice.number)) return false;
        if (!issuedDate.equals(invoice.issuedDate)) return false;
        if (!dueDate.equals(invoice.dueDate)) return false;
        if (!companyFrom.equals(invoice.companyFrom)) return false;
        if (!companyTo.equals(invoice.companyTo)) return false;
        return invoiceEntries.equals(invoice.invoiceEntries);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + number.hashCode();
        result = 31 * result + issuedDate.hashCode();
        result = 31 * result + dueDate.hashCode();
        result = 31 * result + companyFrom.hashCode();
        result = 31 * result + companyTo.hashCode();
        result = 31 * result + invoiceEntries.hashCode();
        return result;
    }
}
