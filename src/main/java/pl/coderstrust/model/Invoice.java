package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Invoice {

    private final Long id;
    private final String number;
    private final LocalDate issuedDate;
    private final LocalDate dueDate;
    private final Company companyFrom;
    private final Company companyTo;
    private final List<InvoiceEntry> entries;

    public Invoice(Long id, String number, LocalDate issuedDate, LocalDate dueDate,
                   Company companyFrom, Company companyTo, List<InvoiceEntry> entries) {
        this.id = id;
        this.number = number;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.companyFrom = companyFrom;
        this.companyTo = companyTo;
        this.entries = entries != null ? new ArrayList(entries) : new ArrayList();
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

    public List<InvoiceEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice)) return false;
        Invoice invoice = (Invoice) o;
        return id.equals(invoice.id) &&
                number.equals(invoice.number) &&
                issuedDate.equals(invoice.issuedDate) &&
                dueDate.equals(invoice.dueDate) &&
                companyFrom.equals(invoice.companyFrom) &&
                companyTo.equals(invoice.companyTo) &&
                entries.equals(invoice.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, issuedDate, dueDate, companyFrom, companyTo, entries);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", issuedDate=" + issuedDate +
                ", dueDate=" + dueDate +
                ", companyFrom=" + companyFrom +
                ", companyTo=" + companyTo +
                ", entries=" + entries +
                '}';
    }
}
