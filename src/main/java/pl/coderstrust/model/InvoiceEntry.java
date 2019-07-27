package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@JsonDeserialize(builder = InvoiceEntry.Builder.class)
@ApiModel(value = "InvoiceEntry", description = "List of invoice entries.")
public final class InvoiceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "The unique identifier of the entry.", position = -1, dataType = "Long")
    private final Long id;

    @ApiModelProperty(value = "Description of the entry.", example = "Kurs")
    private final String description;

    @ApiModelProperty(value = "Number of units of the entry.", example = "1")
    private final Long quantity;

    @ApiModelProperty(value = "Unit price of the entry.", example = "1000")
    private final BigDecimal price;

    @ApiModelProperty(value = "Price without tax.", example = "1000")
    private final BigDecimal netValue;

    @ApiModelProperty(value = "Price with tax.", example = "1230")
    private final BigDecimal grossValue;

    @ApiModelProperty(value = "Vat tax rate.", example = "VAT_23")
    private final Vat vatRate;

    private InvoiceEntry() {
        id = null;
        description = null;
        quantity = null;
        price = null;
        netValue = null;
        grossValue = null;
        vatRate = null;
    }

    private InvoiceEntry(Builder builder) {
        id = builder.id;
        description = builder.description;
        quantity = builder.quantity;
        price = builder.price;
        netValue = builder.netValue;
        grossValue = builder.grossValue;
        vatRate = builder.vatRate;
    }

    public static InvoiceEntry.Builder builder() {
        return new InvoiceEntry.Builder();
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public BigDecimal getGrossValue() {
        return grossValue;
    }

    public Vat getVatRate() {
        return vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceEntry)) {
            return false;
        }
        InvoiceEntry that = (InvoiceEntry) o;
        return id.equals(that.id)
            && description.equals(that.description)
            && quantity.equals(that.quantity)
            && price.equals(that.price)
            && netValue.equals(that.netValue)
            && grossValue.equals(that.grossValue)
            && vatRate == that.vatRate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntry{"
            + "id=" + id
            + ", description='" + description + '\''
            + ", quantity=" + quantity
            + ", price=" + price
            + ", netValue=" + netValue
            + ", grossValue=" + grossValue
            + ", vatRate=" + vatRate
            + '}';
    }

    @JsonPOJOBuilder
    public static class Builder {

        private Long id;
        private String description;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private Vat vatRate;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder withNetValue(BigDecimal netValue) {
            this.netValue = netValue;
            return this;
        }

        public Builder withGrossValue(BigDecimal grossValue) {
            this.grossValue = grossValue;
            return this;
        }

        public Builder withVatRate(Vat vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public InvoiceEntry build() {
            return new InvoiceEntry(this);
        }
    }
}
