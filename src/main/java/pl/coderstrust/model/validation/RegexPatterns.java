package pl.coderstrust.model.validation;

import java.util.regex.Pattern;

class RegexPatterns {

    private static Pattern taxIdPattern = Pattern.compile("[0-9]{3}-?[0-9]{2}-?[0-9]{2}-?[0-9]{3}");
    private static Pattern accountNumberPattern = Pattern.compile("[0-9]{2}( ?[0-9]{4}){6}");
    private static Pattern mobilePhoneNumberPattern = Pattern.compile("\\+?48?[4-8][0-9]{8}");
    private static Pattern landlinePhoneNumberPattern = Pattern.compile("\\+?([1-9][0-9])?[4-8][0-9]{6}");
    private static Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static Pattern invoiceNumberPattern = Pattern.compile("^(?=.*[0-9]).*$");

    static boolean matchesTaxIdPattern(String taxId) {
        return taxIdPattern.matcher(taxId).matches();
    }

    static boolean matchesAccountNumberPattern(String accountNumber) {
        return accountNumberPattern.matcher(accountNumber).matches();
    }

    static boolean matchesPhoneNumberPattern(String phoneNumber) {
        String inputPhoneNumber = phoneNumber.replaceAll(" ", "");
        return mobilePhoneNumberPattern.matcher(inputPhoneNumber).matches() || landlinePhoneNumberPattern.matcher(inputPhoneNumber).matches();
    }

    static boolean matchesEmailPattern(String email) {
        return emailPattern.matcher(email).matches();
    }

    static boolean matchesInvoiceNumberPattern(String invoiceNumber) {
        return invoiceNumberPattern.matcher(invoiceNumber).matches();
    }
}
