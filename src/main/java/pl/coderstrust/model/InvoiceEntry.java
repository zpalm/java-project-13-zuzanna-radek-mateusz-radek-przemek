package pl.coderstrust.model;

import java.math.BigDecimal;

public final class InvoiceEntry {

    private final Long id;
    private final String description;
    private final BigDecimal quantity;
    private final BigDecimal price;
    private final BigDecimal netValue;
    private final BigDecimal grossValue;
    private final Vat vatRate;

    public InvoiceEntry(Long id, String description, BigDecimal quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, Vat vatRate) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
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
        if (this == o) return true;
        if (!(o instanceof InvoiceEntry)) return false;
        InvoiceEntry that = (InvoiceEntry) o;
        if (!id.equals(that.id)) return false;
        if (!description.equals(that.description)) return false;
        if (!quantity.equals(that.quantity)) return false;
        if (!price.equals(that.price)) return false;
        if (!netValue.equals(that.netValue)) return false;
        if (!grossValue.equals(that.grossValue)) return false;
        return vatRate == that.vatRate;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + quantity.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + netValue.hashCode();
        result = 31 * result + grossValue.hashCode();
        result = 31 * result + vatRate.hashCode();
        return result;
    }
}
