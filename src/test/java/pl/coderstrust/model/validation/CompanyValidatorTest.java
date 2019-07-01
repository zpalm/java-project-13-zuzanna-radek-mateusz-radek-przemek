package pl.coderstrust.model.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.coderstrust.model.Company;

class CompanyValidatorTest {

    @ParameterizedTest
    @MethodSource("setOfNamesAndValidationResults")
    void shouldValidateName(String name, List<String> expected) {
        Company companyWithNameVariable = Company.builder()
            .withId(1L)
            .withName(name)
            .withAddress("ul. Warszawska 15, 06-456 Kraków")
            .withTaxId("5842751979")
            .withAccountNumber("82 1020 5226 0000 6102 0417 7895")
            .withPhoneNumber("+48505609023")
            .withEmail("xyz@gmail.com")
            .build();

        List<String> resultOfValidation = CompanyValidator.validate(companyWithNameVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfNamesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Name cannot be null")),
            Arguments.of("     ", Arrays.asList("Name must contain at least 1 character")),
            Arguments.of("Company Name", Arrays.asList()),
            Arguments.of("$^#!@^^($#@#", Arrays.asList()),
            Arguments.of("12345678", Arrays.asList())

        );
    }

    @ParameterizedTest
    @MethodSource("setOfAddressesAndValidationResults")
    void shouldValidateAddress(String address, List<String> expected) {
        Company companyWithAddressVariable = Company.builder()
            .withId(1L)
            .withName("xyz")
            .withAddress(address)
            .withTaxId("5842751979")
            .withAccountNumber("82 1020 5226 0000 6102 0417 7895")
            .withPhoneNumber("+48505609023")
            .withEmail("xyz@gmail.com")
            .build();

        List<String> resultOfValidation = CompanyValidator.validate(companyWithAddressVariable);

        assertEquals(expected, resultOfValidation);

    }

    private static Stream<Arguments> setOfAddressesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Address cannot be null")),
            Arguments.of("     ", Arrays.asList("Address must contain at least 1 character")),
            Arguments.of("ul. Krakowska 32/1, 02-786 Warszawa", Arrays.asList()),
            Arguments.of("a", Arrays.asList()),
            Arguments.of("District 1 1656 Union Street Eureka 707-445-6600", Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfTaxIdsAndValidationResults")
    void shouldValidateTaxId(String taxId, List<String> expected) {
        Company companyWithTaxIdVariable = Company.builder()
            .withId(1L)
            .withName("xyz")
            .withAddress("ul. Warszawska 15, 06-456 Kraków")
            .withTaxId(taxId)
            .withAccountNumber("82 1020 5226 0000 6102 0417 7895")
            .withPhoneNumber("+48505609023")
            .withEmail("xyz@gmail.com")
            .build();

        List<String> resultOfValidation = CompanyValidator.validate(companyWithTaxIdVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfTaxIdsAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Tax id cannot be null")),
            Arguments.of("345-678-o6-79", Arrays.asList("Tax id does not match correct tax id pattern")),
            Arguments.of("324-214-237-8", Arrays.asList("Tax id does not match correct tax id pattern")),
            Arguments.of("829-678-12--4", Arrays.asList("Tax id does not match correct tax id pattern")),
            Arguments.of("    ", Arrays.asList("Tax id does not match correct tax id pattern")),
            Arguments.of("4-567-890-34", Arrays.asList("Tax id does not match correct tax id pattern")),
            Arguments.of("935-78-43-209", Arrays.asList()),
            Arguments.of("2300423149", Arrays.asList()),
            Arguments.of("523-02-44324", Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfAccountNumbersAndValidationResults")
    void shouldValidateAccountNumber(String accountNumber, List<String> expected) {
        Company companyWithAccountNumberVariable = Company.builder()
            .withId(1L)
            .withName("xyz")
            .withAddress("ul. Warszawska 15, 06-456 Kraków")
            .withTaxId("5842751979")
            .withAccountNumber(accountNumber)
            .withPhoneNumber("+48505609023")
            .withEmail("xyz@gmail.com")
            .build();

        List<String> resultOfValidation = CompanyValidator.validate(companyWithAccountNumberVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfAccountNumbersAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Account number cannot be null")),
            Arguments.of("28 1020 4476 0000 8802 0410521", Arrays.asList("Account number does not match correct account number pattern")),
            Arguments.of("7 1910 1048 2944 0335 8250 0001", Arrays.asList("Account number does not match correct account number pattern")),
            Arguments.of("      ", Arrays.asList("Account number does not match correct account number pattern")),
            Arguments.of("28 1020 4476 0000 8802 0410 5319", Arrays.asList("Incorrect account number - please verify")),
            Arguments.of("47191010482944033582500002", Arrays.asList("Incorrect account number - please verify")),
            Arguments.of("28 1020 4476 0000 8802 0410 5219", Arrays.asList()),
            Arguments.of("47191010482944033582500001", Arrays.asList()),
            Arguments.of("93 1020 1068 1230 7223 1226 0763", Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfPhoneNumbersAndValidationResults")
    void shouldValidatePhoneNumber(String phoneNumber, List<String> expected) {
        Company companyWithPhoneNumberVariable = Company.builder()
            .withId(1L)
            .withName("xyz")
            .withAddress("ul. Warszawska 15, 06-456 Kraków")
            .withTaxId("5842751979")
            .withAccountNumber("82 1020 5226 0000 6102 0417 7895")
            .withPhoneNumber(phoneNumber)
            .withEmail("xyz@gmail.com")
            .build();

        List<String> resultOfValidation = CompanyValidator.validate(companyWithPhoneNumberVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfPhoneNumbersAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Phone number cannot be null")),
            Arguments.of("512345 67", Arrays.asList("Phone number does not match correct phone number pattern")),
            Arguments.of("          ", Arrays.asList("Phone number does not match correct phone number pattern")),
            Arguments.of("+48 511-346-789", Arrays.asList("Phone number does not match correct phone number pattern")),
            Arguments.of("+08 057 921 345", Arrays.asList("Phone number does not match correct phone number pattern")),
            Arguments.of("050821990", Arrays.asList("Phone number does not match correct phone number pattern")),
            Arguments.of("+48 357 921 345", Arrays.asList("Phone number does not match correct phone number pattern")),
            Arguments.of("259821990", Arrays.asList("Phone number does not match correct phone number pattern")),
            Arguments.of("+48 657 921 345", Arrays.asList()),
            Arguments.of("256821990", Arrays.asList()),
            Arguments.of("607923521", Arrays.asList()),
            Arguments.of("+61 514 0507", Arrays.asList()),
            Arguments.of("48510424373", Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfEmailsAndValidationResults")
    void shouldValidateEmail(String email, List<String> expected) {
        Company companyWithEmailVariable = Company.builder()
            .withId(1L)
            .withName("xyz")
            .withAddress("ul. Warszawska 15, 06-456 Kraków")
            .withTaxId("5842751979")
            .withAccountNumber("82 1020 5226 0000 6102 0417 7895")
            .withPhoneNumber("+48505609023")
            .withEmail(email)
            .build();

        List<String> resultOfValidation = CompanyValidator.validate(companyWithEmailVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfEmailsAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Email cannot be null")),
            Arguments.of("jan.nowak.gmail.com", Arrays.asList("Email does not match correct email pattern")),
            Arguments.of("        ", Arrays.asList("Email does not match correct email pattern")),
            Arguments.of("john.doe@yahoo,us", Arrays.asList("Email does not match correct email pattern")),
            Arguments.of("andrzejkowalski@op.p", Arrays.asList("Email does not match correct email pattern")),
            Arguments.of("@gmail.com", Arrays.asList("Email does not match correct email pattern")),
            Arguments.of("jan.nowak@gmail.com", Arrays.asList()),
            Arguments.of("jp_smith@op-wp.pl", Arrays.asList()),
            Arguments.of("%@yandex.ru", Arrays.asList()),
            Arguments.of("andrzejkowalski@op.pl", Arrays.asList())
        );
    }
}
