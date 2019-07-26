package pl.coderstrust.database.nosql.model;

import java.util.Objects;

public final class Company {

    private final String name;

    private final String address;

    private final String taxId;

    private final String accountNumber;

    private final String phoneNumber;

    private final String email;

    private Company(Builder builder) {
        this.name = builder.name;
        this.address = builder.address;
        this.taxId = builder.taxId;
        this.accountNumber = builder.accountNumber;
        this.phoneNumber = builder.phoneNumber;
        this.email = builder.email;
    }

    public static Company.Builder builder() {
        return new Company.Builder();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        Company company = (Company) o;
        return name.equals(company.name)
            && address.equals(company.address)
            && taxId.equals(company.taxId)
            && accountNumber.equals(company.accountNumber)
            && phoneNumber.equals(company.phoneNumber)
            && email.equals(company.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, taxId, accountNumber, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "Company{"
            + ", name='" + name + '\''
            + ", address='" + address + '\''
            + ", taxId='" + taxId + '\''
            + ", accountNumber='" + accountNumber + '\''
            + ", phoneNumber='" + phoneNumber + '\''
            + ", email='" + email + '\''
            + '}';
    }

    public static class Builder {

        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}
