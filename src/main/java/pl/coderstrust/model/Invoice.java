package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
@JsonDeserialize(builder = Invoice.Builder.class)
@ApiModel(value = "Invoice", description = "Invoice")
public final class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "The unique identifier of the invoice.", position = -1, dataType = "Long")
    private final Long id;

    @ApiModelProperty(value = "The number of the invoice.", example = "1/2/3")
    private final String number;

    @ApiModelProperty(value = "The date when invoice was created.", example = "2019-07-16")
    private final LocalDate issuedDate;

    @ApiModelProperty(value = "The date when payment will be required.", example = "2019-07-30")
    private final LocalDate dueDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @ApiModelProperty(value = "The company issuing the invoice.")
    private final Company seller;

    @ManyToOne(cascade = CascadeType.ALL)
    @ApiModelProperty(value = "The company accepting the invoice.")
    private final Company buyer;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ApiModelProperty(value = "List of items on the invoice.")
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
        return entries != null ? new ArrayList(entries) : new ArrayList();
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

    @JsonPOJOBuilder
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
