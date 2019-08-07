package pl.coderstrust.generators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pl.coderstrust.database.sql.model.Company;
import pl.coderstrust.database.sql.model.Invoice;
import pl.coderstrust.database.sql.model.InvoiceEntry;

public class SqlInvoiceGenerator {

    public static Invoice getRandomInvoice() {
        Long id = IdGenerator.getNextId();
        String number = WordGenerator.getRandomWord();
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = SqlCompanyGenerator.getRandomCompany();
        Company buyer = SqlCompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(SqlInvoiceEntryGenerator.getRandomEntry());
        }

        return Invoice.builder()
            .withId(id)
            .withNumber(number)
            .withIssuedDate(issuedDate)
            .withDueDate(dueDate)
            .withSeller(seller)
            .withBuyer(buyer)
            .withEntries(entries)
            .build();
    }

    public static Invoice getRandomInvoiceWithNullId() {
        String number = WordGenerator.getRandomWord();
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = SqlCompanyGenerator.getRandomCompany();
        Company buyer = SqlCompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(SqlInvoiceEntryGenerator.getRandomEntry());
        }

        return Invoice.builder()
            .withNumber(number)
            .withIssuedDate(issuedDate)
            .withDueDate(dueDate)
            .withSeller(seller)
            .withBuyer(buyer)
            .withEntries(entries)
            .build();
    }

    public static Invoice getRandomInvoiceWithSpecificId(Long id) {
        String number = WordGenerator.getRandomWord();
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = SqlCompanyGenerator.getRandomCompany();
        Company buyer = SqlCompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(SqlInvoiceEntryGenerator.getRandomEntry());
        }

        return Invoice.builder()
            .withId(id)
            .withNumber(number)
            .withIssuedDate(issuedDate)
            .withDueDate(dueDate)
            .withSeller(seller)
            .withBuyer(buyer)
            .withEntries(entries)
            .build();
    }
}
