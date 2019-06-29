package pl.coderstrust.model.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

public class InvoiceValidator extends Validator {

    public static List<String> validate(Invoice invoice) {
        if (invoice == null) {
            return Collections.singletonList("Invoice cannot be null");
        }

        List<String> result = new ArrayList<>();
        addResultOfValidation(result, validateNumber(invoice.getNumber()));

        String resultOfValidationIssuedDate = validateIssuedDate(invoice.getIssuedDate());
        addResultOfValidation(result, resultOfValidationIssuedDate);

        String resultOfValidationDueDate = validateDueDate(invoice.getDueDate());
        addResultOfValidation(result, resultOfValidationDueDate);

        if (resultOfValidationIssuedDate == null && resultOfValidationDueDate == null) {
            addResultOfValidation(result, validateRelationOfIssuedDateAndDueDate(invoice.getIssuedDate(), invoice.getDueDate()));
        }

        addResultOfValidation(result, validateSeller(invoice.getSeller()));
        addResultOfValidation(result, validateBuyer(invoice.getBuyer()));

        for (InvoiceEntry invoiceEntry : invoice.getEntries()) {
            addResultOfValidation(result, validateEntry(invoiceEntry));
        }

        return result;
    }

    private static String validateNumber(String number) {
        if (number == null) {
            return "Number cannot be null";
        }

        if (!RegexPatterns.matchesInvoiceNumberPattern(number)) {
            return "Number must contain at least 1 digit";
        }

        return null;
    }

    private static String validateIssuedDate(LocalDate issuedDate) {
        if (issuedDate == null) {
            return "Issued date cannot be null";
        }

        return null;
    }

    private static String validateDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            return "Due date cannot be null";
        }

        return null;
    }

    private static String validateRelationOfIssuedDateAndDueDate(LocalDate issuedDate, LocalDate dueDate) {
        if (dueDate.isBefore(issuedDate)) {
            return "Issued date must be earlier than due date";
        }

        return null;
    }

    private static List<String> validateSeller(Company seller) {
        return CompanyValidator.validate(seller);
    }

    private static List<String> validateBuyer(Company buyer) {
        return CompanyValidator.validate(buyer);
    }

    private static List<String> validateEntry(InvoiceEntry invoiceEntry) {
        return InvoiceEntryValidator.validate(invoiceEntry);
    }
}
