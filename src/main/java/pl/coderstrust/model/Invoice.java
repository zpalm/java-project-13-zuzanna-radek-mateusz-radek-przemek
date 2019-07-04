package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public final class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    private final String number;
    private final LocalDate issuedDate;
    private final LocalDate dueDate;

    @ManyToOne(cascade = CascadeType.ALL)
    private final Company seller;

    @ManyToOne(cascade = CascadeType.ALL)
    private final Company buyer;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<InvoiceEntry> entries;

    private Invoice() {
        id = null;
        number = null;
        issuedDate = null;
        dueDate = null;
        seller = null;
        buyer = null;
        entries = null;
    }

    private Invoice(Builder builder) {
        this.id = builder.id;
        this.number = builder.number;
        this.issuedDate = builder.issuedDate;
        this.dueDate = builder.dueDate;
        this.seller = builder.seller;
        this.buyer = builder.buyer;
        this.entries = builder.entries;
    }

    public static Invoice.Builder builder() {
        return new Invoice.Builder();
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
        return id.equals(invoice.id)
            && number.equals(invoice.number)
            && issuedDate.equals(invoice.issuedDate)
            && dueDate.equals(invoice.dueDate)
            && seller.equals(invoice.seller)
            && buyer.equals(invoice.buyer)
            && entries.equals(invoice.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, issuedDate, dueDate, seller, buyer, entries);
    }

    @Override
    public String toString() {
        return "Invoice{"
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

        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private Company seller;
        private Company buyer;
        private List<InvoiceEntry> entries;

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
