package pl.coderstrust.model.validation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.coderstrust.model.Company;

public class CompanyValidator extends Validator {

    public static List<String> validate(Company company) {
        if (company == null) {
            return Collections.singletonList("Company cannot be null");
        }

        List<String> result = new ArrayList<>();

        addResultOfValidation(result, validateName(company.getName()));
        addResultOfValidation(result, validateAddress(company.getAddress()));
        addResultOfValidation(result, validateTaxId(company.getTaxId()));
        addResultOfValidation(result, validateAccountNumber(company.getAccountNumber()));
        addResultOfValidation(result, validatePhoneNumber(company.getPhoneNumber()));
        addResultOfValidation(result, validateEmail(company.getEmail()));

        return result;
    }

    private static String validateName(String name) {
        if (name == null) {
            return "Name cannot be null";
        }

        if (name.trim().isEmpty()) {
            return "Name must contain at least 1 character";
        }

        return null;
    }

    private static String validateAddress(String address) {
        if (address == null) {
            return "Address cannot be null";
        }

        if (address.trim().isEmpty()) {
            return "Address must contain at least 1 character";
        }

        return null;
    }

    private static String validateTaxId(String taxId) {
        if (taxId == null) {
            return "Tax id cannot be null";
        }

        if (!RegexPatterns.matchesTaxIdPattern(taxId)) {
            return "Tax id does not match correct tax id pattern";
        }

        return null;
    }

    private static String validateAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return "Account number cannot be null";
        }

        if (!RegexPatterns.matchesAccountNumberPattern(accountNumber)) {
            return "Account number does not match correct account number pattern";
        }

        if (!isCorrectAccountNumber(accountNumber)) {
            return "Incorrect account number - please verify";
        }

        return null;
    }

    private static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "Phone number cannot be null";
        }

        if (!RegexPatterns.matchesPhoneNumberPattern(phoneNumber)) {
            return "Phone number does not match correct phone number pattern";
        }

        return null;
    }

    private static String validateEmail(String email) {
        if (email == null) {
            return "Email cannot be null";
        }

        if (!RegexPatterns.matchesEmailPattern(email)) {
            return "Email does not match correct email pattern";
        }

        return null;
    }

    private static boolean isCorrectAccountNumber(String accountNumber) {
        String inputAccountNumber = accountNumber.replaceAll(" ", "");
        final String countryCode = "2521";

        StringBuilder accountNumberBuilder = new StringBuilder(inputAccountNumber);

        String accountNumberToVerify = accountNumberBuilder.append(countryCode)
            .append(accountNumberBuilder.subSequence(0, 2)).delete(0, 2).toString();

        BigInteger accountNumberToCalculate = new BigInteger(accountNumberToVerify);

        BigInteger calculationResult = accountNumberToCalculate.mod(BigInteger.valueOf(97L));

        if (calculationResult.equals(BigInteger.valueOf(1L))) {
            return true;
        }

        return false;
    }
}
