package pl.coderstrust.generators;

import pl.coderstrust.database.nosql.model.Company;

public class NoSqlCompanyGenerator {
    public static Company getRandomCompany() {
        String name = WordGenerator.getRandomWord();
        String address = WordGenerator.getRandomWord();
        String taxId = WordGenerator.getRandomWord();
        String accountNumber = WordGenerator.getRandomWord();
        String phoneNumber = WordGenerator.getRandomWord();
        String email = WordGenerator.getRandomWord();

        return Company.builder()
            .withName(name)
            .withAddress(address)
            .withTaxId(taxId)
            .withAccountNumber(accountNumber)
            .withPhoneNumber(phoneNumber)
            .withEmail(email)
            .build();
    }
}
