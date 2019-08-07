package pl.coderstrust.generators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import pl.coderstrust.database.sql.model.InvoiceEntry;
import pl.coderstrust.database.sql.model.Vat;

public class SqlInvoiceEntryGenerator {

    public static InvoiceEntry getRandomEntry() {
        Long id = IdGenerator.getNextId();
        String description = WordGenerator.getRandomWord();
        long quantity = ThreadLocalRandom.current().nextLong(1, 999);
        BigDecimal price = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1, 50));
        BigDecimal netValue = price.multiply(BigDecimal.valueOf(quantity));
        Vat vatRate = VatRateGenerator.getRandomVatRate(Vat.class);
        BigDecimal vatValue = netValue.multiply(BigDecimal.valueOf(vatRate.getValue())).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal grossValue = netValue.add(vatValue);

        return InvoiceEntry.builder()
            .withId(id)
            .withDescription(description)
            .withQuantity(quantity)
            .withPrice(price)
            .withNetValue(netValue)
            .withGrossValue(grossValue)
            .withVatRate(vatRate)
            .build();
    }
}
