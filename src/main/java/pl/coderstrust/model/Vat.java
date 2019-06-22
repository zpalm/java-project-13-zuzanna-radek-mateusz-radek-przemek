package pl.coderstrust.model;

import java.math.BigDecimal;

public enum Vat {

    VAT_0(BigDecimal.ZERO), VAT_5(new BigDecimal("5")), VAT_8(new BigDecimal("8")), VAT_23(new BigDecimal("23"));

    private final BigDecimal vatRate;

    Vat(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }
}
