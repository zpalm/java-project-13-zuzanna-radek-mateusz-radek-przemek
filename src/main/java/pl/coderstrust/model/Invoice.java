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
}
