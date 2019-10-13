package pl.coderstrust.generators;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import pl.coderstrust.model.Company;

public class CompanyGenerator {

    public static Company getRandomCompany() {
        long id = IdGenerator.getNextId();
        String name = WordGenerator.getRandomWord();
        String address = WordGenerator.getRandomWord();
        String taxId = RegexWordGenerator.getRandomRegexWord("[0-9]{3}-?[0-9]{2}-?[0-9]{2}-?[0-9]{3}");
        String accountNumber = Iban.random(CountryCode.PL).toString().substring(2);
        String phoneNumber = RegexWordGenerator.getRandomRegexWord("(+48|48)?[4-8][0-9]{8}");
        String email = RegexWordGenerator.getRandomRegexWord("[a-z]{3}\\@[a-z]{3}\\.[a-z]{3}");

        return Company.builder()
            .withId(id)
            .withName(name)
            .withAddress(address)
            .withTaxId(taxId)
            .withAccountNumber(accountNumber)
            .withPhoneNumber(phoneNumber)
            .withEmail(email)
            .build();
    }

    public static Company getRandomCompanyWithIdEqualZero() {
        String name = WordGenerator.getRandomWord();
        String address = WordGenerator.getRandomWord();
        String taxId = RegexWordGenerator.getRandomRegexWord("[0-9]{3}-?[0-9]{2}-?[0-9]{2}-?[0-9]{3}");
        String accountNumber = Iban.random(CountryCode.PL).toString().substring(2);
        String phoneNumber = RegexWordGenerator.getRandomRegexWord("(+48|48)?[4-8][0-9]{8}");
        String email = RegexWordGenerator.getRandomRegexWord("[a-z]{3}\\@[a-z]{3}\\.[a-z]{3}");

        return Company.builder()
            .withId(0L)
            .withName(name)
            .withAddress(address)
            .withTaxId(taxId)
            .withAccountNumber(accountNumber)
            .withPhoneNumber(phoneNumber)
            .withEmail(email)
            .build();
    }

    public static Company getRandomCompanyWithSpecificId(Long id) {
        String name = WordGenerator.getRandomWord();
        String address = WordGenerator.getRandomWord();
        String taxId = RegexWordGenerator.getRandomRegexWord("[0-9]{3}-?[0-9]{2}-?[0-9]{2}-?[0-9]{3}");
        String accountNumber = Iban.random(CountryCode.PL).toString().substring(2);
        String phoneNumber = RegexWordGenerator.getRandomRegexWord("(+48|48)?[4-8][0-9]{8}");
        String email = RegexWordGenerator.getRandomRegexWord("[a-z]{3}\\@[a-z]{3}\\.[a-z]{3}");

        return Company.builder()
            .withId(id)
            .withName(name)
            .withAddress(address)
            .withTaxId(taxId)
            .withAccountNumber(accountNumber)
            .withPhoneNumber(phoneNumber)
            .withEmail(email)
            .build();
    }
}
