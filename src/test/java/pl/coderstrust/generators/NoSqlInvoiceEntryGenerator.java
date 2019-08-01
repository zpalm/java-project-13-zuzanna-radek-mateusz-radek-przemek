package pl.coderstrust.generators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

import pl.coderstrust.database.nosql.model.InvoiceEntry;
import pl.coderstrust.database.nosql.model.Vat;

public class NoSqlInvoiceEntryGenerator {
    public static InvoiceEntry getRandomEntry() {
        String description = WordGenerator.getRandomWord();
        long quantity = ThreadLocalRandom.current().nextLong(1, 999);
        BigDecimal price = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1, 50));
        BigDecimal netValue = price.multiply(BigDecimal.valueOf(quantity));
        Vat vatRate = VatRateGenerator.getRandomVatRate(Vat.class);
        BigDecimal vatValue = netValue.multiply(BigDecimal.valueOf(vatRate.getValue())).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal grossValue = netValue.add(vatValue);

        return InvoiceEntry.builder()
            .withDescription(description)
            .withQuantity(quantity)
            .withPrice(price)
            .withNetValue(netValue)
            .withGrossValue(grossValue)
            .withVatRate(vatRate)
            .build();
    }
}
