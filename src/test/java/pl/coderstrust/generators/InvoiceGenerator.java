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
        String number = WordGenerator.getRandomWord("1");
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompany();
        Company buyer = CompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
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
        String number = WordGenerator.getRandomWord("1");
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompany();
        Company buyer = CompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
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
        String number = WordGenerator.getRandomWord("1");
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompany();
        Company buyer = CompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
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
        Long id = IdGenerator.getNextId();
        String number = WordGenerator.getRandomWord("1");
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompany();
        Company buyer = CompanyGenerator.getRandomCompany();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
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

    public static Invoice getRandomInvoiceWithFixedIdsAndIssuedDate(Long id, LocalDate issuedDate) {
        String number = WordGenerator.getRandomWord("1");
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompanyWithIdEqualZero();
        Company buyer = CompanyGenerator.getRandomCompanyWithIdEqualZero();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntryWithIdEqualZero());
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

    public static Invoice getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithIdsEqualZero(Long id) {
        String number = WordGenerator.getRandomWord("1");
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompanyWithIdEqualZero();
        Company buyer = CompanyGenerator.getRandomCompanyWithIdEqualZero();
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntryWithIdEqualZero());
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

    public static Invoice getRandomInvoiceWithSpecificIdCompaniesAndEntriesWithSubsequentIdsStartingFrom(Long id, Long startingCompanyId, Long startingEntryId) {
        String number = WordGenerator.getRandomWord("1");
        LocalDate issuedDate = LocalDate.now();
        LocalDate dueDate = issuedDate.plusDays(2);
        Company seller = CompanyGenerator.getRandomCompanyWithSpecificId(startingCompanyId);
        Company buyer = CompanyGenerator.getRandomCompanyWithSpecificId(startingCompanyId + 1L);
        List<InvoiceEntry> entries = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntryWithSpecificId(i + startingEntryId));
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
