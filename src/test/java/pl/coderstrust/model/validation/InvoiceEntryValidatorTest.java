package pl.coderstrust.model.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

class InvoiceEntryValidatorTest {

    @Test
    void shouldValidateInvoiceEntry() {
        List<String> resultOfValidation = InvoiceEntryValidator.validate(null);
        assertEquals(Arrays.asList("Invoice entry cannot be null"), resultOfValidation);
    }

    @ParameterizedTest
    @MethodSource("setOfDescriptionsAndValidationResults")
    void shouldValidateDescription(String description, List<String> expected) {
        InvoiceEntry invoiceEntryWithDescriptionVariable = InvoiceEntry.builder()
            .withDescription(description)
            .withQuantity(4L)
            .withPrice(BigDecimal.valueOf(25L))
            .withNetValue(BigDecimal.valueOf(100L))
            .withGrossValue(BigDecimal.valueOf(123L))
            .withVatRate(Vat.VAT_23)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithDescriptionVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfDescriptionsAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Description cannot be null")),
            Arguments.of("       ", Arrays.asList("Description must contain at least 1 character various from whitespace")),
            Arguments.of("Pencils and scissors", Arrays.asList()),
            Arguments.of("FKZ/1573/N/Department", Arrays.asList()),
            Arguments.of("24812^#@#!", Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfQuantitiesAndValidationResults")
    void shouldValidateQuantity(Long quantity, List<String> expected) {
        InvoiceEntry invoiceEntryWithQuantityVariable = InvoiceEntry.builder()
            .withDescription("t-shirt")
            .withQuantity(quantity)
            .withPrice(BigDecimal.valueOf(25L))
            .withNetValue(BigDecimal.valueOf(100L))
            .withGrossValue(BigDecimal.valueOf(123L))
            .withVatRate(Vat.VAT_23)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithQuantityVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfQuantitiesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Quantity cannot be null")),
            Arguments.of(-85L, Arrays.asList("Quantity must be greater than zero")),
            Arguments.of(-5L, Arrays.asList("Quantity must be greater than zero")),
            Arguments.of(0L, Arrays.asList("Quantity must be greater than zero")),
            Arguments.of(-219L, Arrays.asList("Quantity must be greater than zero"))
        );
    }

    @ParameterizedTest
    @MethodSource("setOfPricesAndValidationResults")
    void shouldValidatePrice(BigDecimal price, List<String> expected) {
        InvoiceEntry invoiceEntryWithPriceVariable = InvoiceEntry.builder()
            .withDescription("t-shirt")
            .withQuantity(4L)
            .withPrice(price)
            .withNetValue(BigDecimal.valueOf(100L))
            .withGrossValue(BigDecimal.valueOf(123L))
            .withVatRate(Vat.VAT_23)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithPriceVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfPricesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Price cannot be null")),
            Arguments.of(BigDecimal.valueOf(-12.50), Arrays.asList("Price cannot be lower than or equal to zero")),
            Arguments.of(BigDecimal.valueOf(0L), Arrays.asList("Price cannot be lower than or equal to zero")),
            Arguments.of(BigDecimal.valueOf(-0.01), Arrays.asList("Price cannot be lower than or equal to zero"))
        );
    }

    @ParameterizedTest
    @MethodSource("setOfNetValuesAndValidationResults")
    void shouldValidateNetValue(BigDecimal netValue, List<String> expected) {
        InvoiceEntry invoiceEntryWithNetValueVariable = InvoiceEntry.builder()
            .withDescription("t-shirt")
            .withQuantity(4L)
            .withPrice(BigDecimal.valueOf(25L))
            .withNetValue(netValue)
            .withGrossValue(BigDecimal.valueOf(123L))
            .withVatRate(Vat.VAT_23)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithNetValueVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfNetValuesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Net value cannot be null")),
            Arguments.of(BigDecimal.valueOf(-20L), Arrays.asList("Net value cannot be lower than or equal to zero")),
            Arguments.of(BigDecimal.valueOf(0L), Arrays.asList("Net value cannot be lower than or equal to zero")),
            Arguments.of(BigDecimal.valueOf(-14.99), Arrays.asList("Net value cannot be lower than or equal to zero"))
        );
    }

    @ParameterizedTest
    @MethodSource("setOfGrossValuesAndValidationResults")
    void shouldValidateGrossValue(BigDecimal grossValue, List<String> expected) {
        InvoiceEntry invoiceEntryWithGrossValueVariable = InvoiceEntry.builder()
            .withDescription("t-shirt")
            .withQuantity(4L)
            .withPrice(BigDecimal.valueOf(25L))
            .withNetValue(BigDecimal.valueOf(100L))
            .withGrossValue(grossValue)
            .withVatRate(Vat.VAT_23)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithGrossValueVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfGrossValuesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Gross value cannot be null")),
            Arguments.of(BigDecimal.valueOf(-123L), Arrays.asList("Gross value cannot be lower than or equal to zero")),
            Arguments.of(BigDecimal.valueOf(0L), Arrays.asList("Gross value cannot be lower than or equal to zero")),
            Arguments.of(BigDecimal.valueOf(-2333.45), Arrays.asList("Gross value cannot be lower than or equal to zero"))
        );
    }

    @Test
    void shouldValidateVat() {
        InvoiceEntry invoiceEntryWithNullVatRate = InvoiceEntry.builder()
            .withDescription("t-shirt")
            .withQuantity(4L)
            .withPrice(BigDecimal.valueOf(25L))
            .withNetValue(BigDecimal.valueOf(100L))
            .withGrossValue(BigDecimal.valueOf(123L))
            .withVatRate(null)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithNullVatRate);

        assertEquals(Arrays.asList("Vat rate cannot be null"), resultOfValidation);
    }

    @ParameterizedTest
    @MethodSource("setOfRelationsBetweenQuantityPriceNetValueAndValidationResults")
    void shouldValidateRelationBetweenQuantityPriceAndNetValue(Long quantity, BigDecimal price, BigDecimal netValue, List<String> expected) {
        InvoiceEntry invoiceEntryWithRelationVariable = InvoiceEntry.builder()
            .withDescription("t-shirt")
            .withQuantity(quantity)
            .withPrice(price)
            .withNetValue(netValue)
            .withGrossValue(BigDecimal.valueOf(198.09))
            .withVatRate(Vat.VAT_23)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithRelationVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfRelationsBetweenQuantityPriceNetValueAndValidationResults() {
        return Stream.of(
            Arguments.of(5L, BigDecimal.valueOf(32.21), BigDecimal.valueOf(161.05), Arrays.asList()),
            Arguments.of(4L, BigDecimal.valueOf(32.21), BigDecimal.valueOf(161.05), Arrays.asList("Quantity must be a quotient of net value and price")),
            Arguments.of(5L, BigDecimal.valueOf(31.70), BigDecimal.valueOf(161.05), Arrays.asList("Quantity must be a quotient of net value and price")),
            Arguments.of(5L, BigDecimal.valueOf(32.21), BigDecimal.valueOf(161.65), Arrays.asList("Quantity must be a quotient of net value and price", "Gross value does not match net value and vat rate"))
        );
    }

    @ParameterizedTest
    @MethodSource("setOfRelationsBetweenNetValueGrossValueVatRateAndValidationResults")
    void shouldValidateRelationBetweenNetValueGrossValueAndVatRate(BigDecimal netValue, BigDecimal grossValue, Vat vatRate, List<String> expected) {
        InvoiceEntry invoiceEntryWithRelationVariable = InvoiceEntry.builder()
            .withDescription("t-shirt")
            .withQuantity(5L)
            .withPrice(BigDecimal.valueOf(32.21))
            .withNetValue(netValue)
            .withGrossValue(grossValue)
            .withVatRate(vatRate)
            .build();

        List<String> resultOfValidation = InvoiceEntryValidator.validate(invoiceEntryWithRelationVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfRelationsBetweenNetValueGrossValueVatRateAndValidationResults() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(161.05), BigDecimal.valueOf(198.09), Vat.VAT_23, Arrays.asList()),
            Arguments.of(BigDecimal.valueOf(161.65), BigDecimal.valueOf(198.09), Vat.VAT_23, Arrays.asList("Quantity must be a quotient of net value and price", "Gross value does not match net value and vat rate")),
            Arguments.of(BigDecimal.valueOf(161.05), BigDecimal.valueOf(198.08), Vat.VAT_23, Arrays.asList("Gross value does not match net value and vat rate")),
            Arguments.of(BigDecimal.valueOf(161.05), BigDecimal.valueOf(198.09), Vat.VAT_5, Arrays.asList("Gross value does not match net value and vat rate"))
        );
    }
}
