package pl.coderstrust.soap.mappers;

import pl.coderstrust.model.Company;
import pl.coderstrust.soap.bindingclasses.CompanySoap;

public class CompanyMapper {

    public static Company mapCompany(CompanySoap companySoap) {
        Company mappedCompany = Company.builder()
            .withId(companySoap.getId())
            .withName(companySoap.getName())
            .withAddress(companySoap.getAddress())
            .withTaxId(companySoap.getTaxId())
            .withAccountNumber(companySoap.getAccountNumber())
            .withPhoneNumber(companySoap.getPhoneNumber())
            .withEmail(companySoap.getEmail())
            .build();
        return mappedCompany;
    }

    public static CompanySoap mapCompany(Company company) {
        CompanySoap mappedCompany = new CompanySoap();
        mappedCompany.setId(company.getId());
        mappedCompany.setName(company.getName());
        mappedCompany.setAddress(company.getAddress());
        mappedCompany.setTaxId(company.getTaxId());
        mappedCompany.setAccountNumber(company.getAccountNumber());
        mappedCompany.setPhoneNumber(company.getPhoneNumber());
        mappedCompany.setEmail(company.getEmail());
        return mappedCompany;
    }
}
