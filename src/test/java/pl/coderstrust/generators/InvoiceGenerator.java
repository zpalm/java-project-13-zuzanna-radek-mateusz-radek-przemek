package pl.coderstrust.generators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

public class InvoiceGenerator {

    public static Invoice getRandomInvoice() {
        long id = IdGenerator.getNextId();
        String number = WordGenerator.getRandomWord();
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompany();
        Company buyer = CompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }

        return new Invoice(id, number, issueDate, dueDate, seller, buyer, entries);
    }

    public static Invoice getRandomInvoiceWithNullId() {
        String number = WordGenerator.getRandomWord();
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompany();
        Company buyer = CompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }

        return new Invoice(null, number, issueDate, dueDate, seller, buyer, entries);
    }

    public static Invoice getRandomInvoiceWithSpecificId(Long id) {
        String number = WordGenerator.getRandomWord();
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompany();
        Company buyer = CompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }

        return new Invoice(id, number, issueDate, dueDate, seller, buyer, entries);
    }
}
