package pl.coderstrust.generators;

import pl.coderstrust.model.Company;

public class CompanyGenerator {

    public static Company getRandomCompany() {
        long id = IdGenerator.getNextId();
        String name = WordGenerator.getRandomWord();
        String address = WordGenerator.getRandomWord();
        String taxId = WordGenerator.getRandomWord();
        String accountNumber = WordGenerator.getRandomWord();
        String phoneNumber = WordGenerator.getRandomWord();
        String email = WordGenerator.getRandomWord();

        return new Company(id, name, address, taxId, accountNumber, phoneNumber, email);
    }
}
