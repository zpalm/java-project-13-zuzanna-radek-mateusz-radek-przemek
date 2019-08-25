package pl.coderstrust.generators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.jni.Local;
import pl.coderstrust.database.nosql.model.Company;
import pl.coderstrust.database.nosql.model.Invoice;
import pl.coderstrust.database.nosql.model.InvoiceEntry;

public class NoSqlInvoiceGenerator {

    public static Invoice getRandomInvoice() {
        long id = IdGenerator.getNextId();
        String number = WordGenerator.getRandomWord();
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = NoSqlCompanyGenerator.getRandomCompany();
        Company buyer = NoSqlCompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(NoSqlInvoiceEntryGenerator.getRandomEntry());
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
        Company seller = NoSqlCompanyGenerator.getRandomCompany();
        Company buyer = NoSqlCompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(NoSqlInvoiceEntryGenerator.getRandomEntry());
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
        Company seller = NoSqlCompanyGenerator.getRandomCompany();
        Company buyer = NoSqlCompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(NoSqlInvoiceEntryGenerator.getRandomEntry());
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

    public static Invoice getRandomInvoiceWithSpecificIssuedDate(LocalDate issuedDate) {
        long id = IdGenerator.getNextId();
        String number = WordGenerator.getRandomWord();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = NoSqlCompanyGenerator.getRandomCompany();
        Company buyer = NoSqlCompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(NoSqlInvoiceEntryGenerator.getRandomEntry());
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
