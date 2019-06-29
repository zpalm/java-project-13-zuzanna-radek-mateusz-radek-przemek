package pl.coderstrust.model.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

public class InvoiceEntryValidator extends Validator {

    public static List<String> validate(InvoiceEntry invoiceEntry) {
        if (invoiceEntry == null) {
            return Collections.singletonList("Invoice entry cannot be null");
        }
        List<String> result = new ArrayList<>();

        addResultOfValidation(result, validateDescription(invoiceEntry.getDescription()));

        String resultOfValidationQuantity = validateQuantity(invoiceEntry.getQuantity());
        addResultOfValidation(result, resultOfValidationQuantity);

        String resultOfValidationPrice = validatePrice(invoiceEntry.getPrice());
        addResultOfValidation(result, resultOfValidationPrice);

        String resultOfValidationNetValue = validateNetValue(invoiceEntry.getNetValue());
        addResultOfValidation(result, resultOfValidationNetValue);

        String resultOfValidationGrossValue = validateGrossValue(invoiceEntry.getGrossValue());
        addResultOfValidation(result, resultOfValidationGrossValue);

        String resultOfValidationVatRate = validateVatRate(invoiceEntry.getVatRate());
        addResultOfValidation(result, resultOfValidationVatRate);

        if (resultOfValidationQuantity == null && resultOfValidationPrice == null && resultOfValidationNetValue == null) {
            addResultOfValidation(result, validateRelationBetweenQuantityPriceAndNetValue(invoiceEntry.getQuantity(), invoiceEntry.getPrice(), invoiceEntry.getNetValue()));
        }

        if (resultOfValidationNetValue == null && resultOfValidationGrossValue == null && resultOfValidationVatRate == null) {
            addResultOfValidation(result, validateRelationBetweenNetValueGrossValueAndVatRate(invoiceEntry.getNetValue(), invoiceEntry.getGrossValue(), invoiceEntry.getVatRate()));
        }

        return result;
    }

    private static String validateDescription(String description) {
        if (description == null) {
            return "Description cannot be null";
        }

        if (description.trim().isEmpty()) {
            return "Description must contain at least 1 character various from whitespace";
        }

        return null;
    }

    private static String validateQuantity(Long quantity) {
        if (quantity == null) {
            return "Quantity cannot be null";
        }

        if (!(quantity > 0)) {
            return "Quantity must be greater than zero";
        }

        return null;
    }

    private static String validatePrice(BigDecimal price) {
        if (price == null) {
            return "Price cannot be null";
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return "Price cannot be lower than or equal to zero";
        }

        return null;
    }

    private static String validateNetValue(BigDecimal netValue) {
        if (netValue == null) {
            return "Net value cannot be null";
        }

        if (netValue.compareTo(BigDecimal.ZERO) <= 0) {
            return "Net value cannot be lower than or equal to zero";
        }

        return null;
    }

    private static String validateGrossValue(BigDecimal grossValue) {
        if (grossValue == null) {
            return "Gross value cannot be null";
        }

        if (grossValue.compareTo(BigDecimal.ZERO) <= 0) {
            return "Gross value cannot be lower than or equal to zero";
        }

        return null;
    }

    private static String validateVatRate(Vat vatRate) {
        if (vatRate == null) {
            return "Vat rate cannot be null";
        }
        return null;
    }

    private static String validateRelationBetweenQuantityPriceAndNetValue(Long quantity, BigDecimal price, BigDecimal netValue) {
        BigDecimal productOfQuantityAndPrice = price.multiply(BigDecimal.valueOf(quantity));
        if (!(productOfQuantityAndPrice.compareTo(netValue) == 0)) {
            return "Quantity must be a quotient of net value and price";
        }

        return null;
    }

    private static String validateRelationBetweenNetValueGrossValueAndVatRate(BigDecimal netValue, BigDecimal grossValue, Vat vatRate) {
        BigDecimal parsedVatRate = BigDecimal.valueOf(vatRate.getValue()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal vatValue = netValue.multiply(parsedVatRate).setScale(2, RoundingMode.HALF_EVEN);

        if (!(grossValue.compareTo(netValue.add(vatValue)) == 0)) {
            return "Gross value does not match net value and vat rate";
        }

        return null;
    }
}
