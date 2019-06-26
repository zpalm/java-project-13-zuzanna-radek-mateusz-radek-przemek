package pl.coderstrust.generators;

import java.util.concurrent.ThreadLocalRandom;

public class VatRateGenerator {

    public static <T extends Enum<?>> T getRandomVatRate(Class<T> vat) {
        int x = ThreadLocalRandom.current().nextInt(vat.getEnumConstants().length);
        return vat.getEnumConstants()[x];
    }
}
