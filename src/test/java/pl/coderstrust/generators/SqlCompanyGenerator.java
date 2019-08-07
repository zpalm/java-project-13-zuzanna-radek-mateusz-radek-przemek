package pl.coderstrust.generators;

import pl.coderstrust.database.sql.model.Company;

public class SqlCompanyGenerator {

    public static Company getRandomCompany() {
        Long id = IdGenerator.getNextId();
        String name = WordGenerator.getRandomWord();
        String address = WordGenerator.getRandomWord();
        String taxId = WordGenerator.getRandomWord();
        String accountNumber = WordGenerator.getRandomWord();
        String phoneNumber = WordGenerator.getRandomWord();
        String email = WordGenerator.getRandomWord();

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
