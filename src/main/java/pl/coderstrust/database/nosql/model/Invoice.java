package pl.coderstrust.database.nosql.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invoice")
public final class Invoice {

    @Id
    @JsonIgnore
    private String mongoId;
    @Indexed(unique = true)
    private final Long id;
    private final String number;
    private final LocalDate issuedDate;
    private final LocalDate dueDate;
    private final Company seller;
    private final Company buyer;
    private final List<InvoiceEntry> entries;

    @PersistenceConstructor
    private Invoice(String mongoId, Long id, String number, LocalDate issuedDate, LocalDate dueDate, Company seller, Company buyer, List<InvoiceEntry> entries) {
        this.mongoId = mongoId;
        this.id = id;
        this.number = number;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.seller = seller;
        this.buyer = buyer;
        this.entries = entries;
    }

    private Invoice(Builder builder) {
        mongoId = builder.mongoId;
        id = builder.id;
        number = builder.number;
        issuedDate = builder.issuedDate;
        dueDate = builder.dueDate;
        seller = builder.seller;
        buyer = builder.buyer;
        entries = builder.entries;
    }

    public static Invoice.Builder builder() {
        return new Invoice.Builder();
    }

    public String getMongoId() {
        return mongoId;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Company getSeller() {
        return seller;
    }

    public Company getBuyer() {
        return buyer;
    }

    public List<InvoiceEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        Invoice invoice = (Invoice) o;
        return mongoId.equals(invoice.mongoId)
            && id.equals(invoice.id)
            && number.equals(invoice.number)
            && issuedDate.equals(invoice.issuedDate)
            && dueDate.equals(invoice.dueDate)
            && seller.equals(invoice.seller)
            && buyer.equals(invoice.buyer)
            && entries.equals(invoice.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoId, id, number, issuedDate, dueDate, seller, buyer, entries);
    }

    @Override
    public String toString() {
        return "Invoice{"
            + "mongoId" + mongoId
            + "id=" + id
            + ", number='" + number + '\''
            + ", issuedDate=" + issuedDate
            + ", dueDate=" + dueDate
            + ", seller=" + seller
            + ", buyer=" + buyer
            + ", entries=" + entries
            + '}';
    }

    public static class Builder {
        private String mongoId;
        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private Company seller;
        private Company buyer;
        private List<InvoiceEntry> entries;

        public Builder withMongoId(String mongoId) {
            this.mongoId = mongoId;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder withIssuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder withSeller(Company seller) {
            this.seller = seller;
            return this;
        }

        public Builder withBuyer(Company buyer) {
            this.buyer = buyer;
            return this;
        }

        public Builder withEntries(List<InvoiceEntry> entries) {
            this.entries = entries != null ? new ArrayList(entries) : new ArrayList();
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }
}
