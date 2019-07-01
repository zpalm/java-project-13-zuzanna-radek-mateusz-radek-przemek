package pl.coderstrust.model.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.InvoiceEntry.Builder;
import pl.coderstrust.model.Vat;

class InvoiceValidatorTest {

    private Company correctSeller;
    private Company correctBuyer;
    private List<InvoiceEntry> correctEntries;

    @BeforeEach
    void setup() {

        correctSeller = new Company.Builder().withName("xyz")
            .withAddress("ul. Krakowska 47a, 02-876 Warszawa")
            .withAccountNumber("82 1020 5226 0000 6102 0417 7895").withPhoneNumber("+48505609023")
            .withTaxId("935-78-43-209").withEmail("xyz@gmail.com").build();

        correctBuyer = new Company.Builder().withName("abc")
            .withAddress("ul. Warszawska 55b, 07-367 Kraków")
            .withAccountNumber("93 1020 1068 1230 7223 1226 0763").withPhoneNumber("6831330")
            .withTaxId("2300423149").withEmail("abc@gmail.com").build();

        correctEntries = Arrays.asList(
            new InvoiceEntry.Builder().withDescription("pencils").withQuantity(10L)
                .withPrice(
                    BigDecimal.valueOf(10L)).withNetValue(BigDecimal.valueOf(100L))
                .withVatRate(Vat.VAT_23).withGrossValue(BigDecimal.valueOf(123L)).build(),
            new InvoiceEntry.Builder().withDescription("sharpeners").withQuantity(5L)
                .withPrice(
                    BigDecimal.valueOf(6L)).withNetValue(BigDecimal.valueOf(30L))
                .withVatRate(Vat.VAT_5).withGrossValue(BigDecimal.valueOf(31.50)).build()
        );
    }

    @Test
    void shouldValidateInvoice() {
        Invoice nullINvoice = null;
        List<String> resultOfValidation = InvoiceValidator.validate(nullINvoice);
        assertEquals(Arrays.asList("Invoice cannot be null"), resultOfValidation);
    }

    @ParameterizedTest
    @MethodSource("setOfNumbersAndValidationResults")
    void shouldValidateNumber(String number, List<String> expected) {
        Invoice invoiceWithNumberVariable = Invoice.builder()
            .withId(1L)
            .withNumber(number)
            .withIssuedDate(LocalDate.of(2019, 4, 30))
            .withDueDate(LocalDate.of(2019, 5, 31))
            .withSeller(correctSeller)
            .withBuyer(correctBuyer)
            .withEntries(correctEntries)
            .build();

        List<String> resultOfValidation = InvoiceValidator.validate(invoiceWithNumberVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfNumbersAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Number cannot be null")),
            Arguments.of("FEQWD/HWF/wfzcgf", Arrays.asList("Number must contain at least 1 digit")),
            Arguments.of("^$@%@$^**", Arrays.asList("Number must contain at least 1 digit")),
            Arguments.of("Invoice", Arrays.asList("Number must contain at least 1 digit")),
            Arguments.of("Invoice#8", Arrays.asList()),
            Arguments.of("32/FA/^&$Y^#$", Arrays.asList()),
            Arguments.of("ABC7342-%^", Arrays.asList()),
            Arguments.of("1", Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfIssuedDatesAndValidationResults")
    void shouldValidateNumber(LocalDate issuedDate, List<String> expected) {
        Invoice invoiceWithIssuedDateVariable = Invoice.builder()
            .withId(1L)
            .withNumber("9/FA/2019/5/DEP/INV")
            .withIssuedDate(issuedDate)
            .withDueDate(LocalDate.of(2019, 5, 31))
            .withSeller(correctSeller)
            .withBuyer(correctBuyer)
            .withEntries(correctEntries)
            .build();

        List<String> resultOfValidation = InvoiceValidator.validate(invoiceWithIssuedDateVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfIssuedDatesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Issued date cannot be null")),
            Arguments.of(LocalDate.of(2019, 4, 30), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 2, 28), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 5, 30), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 5, 15), Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfDueDatesAndValidationResults")
    void shouldValidateDueDate(LocalDate dueDate, List<String> expected) {
        Invoice invoiceWithDueDateVariable = Invoice.builder()
            .withId(1L)
            .withNumber("9/FA/2019/5/DEP/INV")
            .withIssuedDate(LocalDate.of(2019, 1, 1))
            .withDueDate(dueDate)
            .withSeller(correctSeller)
            .withBuyer(correctBuyer)
            .withEntries(correctEntries)
            .build();

        List<String> resultOfValidation = InvoiceValidator.validate(invoiceWithDueDateVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfDueDatesAndValidationResults() {
        return Stream.of(
            Arguments.of(null, Arrays.asList("Due date cannot be null")),
            Arguments.of(LocalDate.of(2019, 4, 30), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 2, 28), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 5, 30), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 5, 15), Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfRelationsBetweenIssuedDateDueDateAndValidationResults")
    void shouldValidateRelationOfIssuedDateAndDueDate(LocalDate issuedDate, LocalDate dueDate, List<String> expected) {
        Invoice invoiceWithDatesVariable = Invoice.builder()
            .withId(1L)
            .withNumber("9/FA/2019/5/DEP/INV")
            .withIssuedDate(issuedDate)
            .withDueDate(dueDate)
            .withSeller(correctSeller)
            .withBuyer(correctBuyer)
            .withEntries(correctEntries)
            .build();

        List<String> resultOfValidation = InvoiceValidator.validate(invoiceWithDatesVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfRelationsBetweenIssuedDateDueDateAndValidationResults() {
        return Stream.of(
            Arguments.of(LocalDate.of(2019, 6, 30), LocalDate.of(2019, 5, 31), Arrays.asList("Issued date must be earlier than due date")),
            Arguments.of(LocalDate.of(2019, 2, 28), LocalDate.of(2019, 1, 31), Arrays.asList("Issued date must be earlier than due date")),
            Arguments.of(LocalDate.of(2019, 7, 31), LocalDate.of(2019, 6, 30), Arrays.asList("Issued date must be earlier than due date")),
            Arguments.of(LocalDate.of(2019, 6, 30), LocalDate.of(2019, 7, 31), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 2, 28), LocalDate.of(2019, 3, 31), Arrays.asList()),
            Arguments.of(LocalDate.of(2019, 7, 31), LocalDate.of(2019, 8, 31), Arrays.asList())
        );
    }

    @ParameterizedTest
    @MethodSource("setOfSellersAndValidationResults")
    void shouldValidateSeller(Company seller, List<String> expected) {
        Invoice invoiceWithSellerVariable = Invoice.builder()
            .withId(1L)
            .withNumber("9/FA/2019/5/DEP/INV")
            .withIssuedDate(LocalDate.of(2019, 5, 31))
            .withDueDate(LocalDate.of(2019, 6, 30))
            .withSeller(seller)
            .withBuyer(correctBuyer)
            .withEntries(correctEntries)
            .build();

        List<String> resultOfValidation = InvoiceValidator.validate(invoiceWithSellerVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfSellersAndValidationResults() {
        Company.Builder seller = new Company.Builder().withName("xyz")
            .withAddress("ul. Krakowska 32/1, 02-786 Warszawa")
            .withAccountNumber("28 1020 4476 0000 8802 0410 5219").withPhoneNumber("+48508467123")
            .withTaxId("2300423149").withEmail("etc@gmail.com");

        return Stream.of(
            Arguments.of(null, Arrays.asList("Company cannot be null")),
            Arguments.of(seller.build(), Arrays.asList()),
            Arguments.of(seller.withAccountNumber("28 1020 4476 0000 8802 0410 5319").build(), Arrays.asList("Incorrect account number - please verify")),
            Arguments.of(seller.withPhoneNumber(null).withTaxId("230042314").withAccountNumber("28 1020 4476 0000 8802 0410 5219").build(),
                Arrays.asList("Tax id does not match correct tax id pattern", "Phone number cannot be null"))
        );
    }

    @ParameterizedTest
    @MethodSource("setOfBuyersAndValidationResults")
    void shouldValidateBuyer(Company buyer, List<String> expected) {
        Invoice invoiceWithBuyerVariable = Invoice.builder()
            .withId(1L)
            .withNumber("9/FA/2019/5/DEP/INV")
            .withIssuedDate(LocalDate.of(2019, 5, 31))
            .withDueDate(LocalDate.of(2019, 6, 30))
            .withSeller(correctSeller)
            .withBuyer(buyer)
            .withEntries(correctEntries)
            .build();

        List<String> resultOfValidation = InvoiceValidator.validate(invoiceWithBuyerVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfBuyersAndValidationResults() {
        Company.Builder buyer = new Company.Builder().withName("abc")
            .withAddress("ul. Warszawska 32/1, 02-786 Wrocław")
            .withAccountNumber("47191010482944033582500001").withPhoneNumber("+256123456")
            .withTaxId("523-02-44324").withEmail("abc@gmail.com");

        return Stream.of(
            Arguments.of(null, Arrays.asList("Company cannot be null")),
            Arguments.of(buyer.build(), Arrays.asList()),
            Arguments.of(buyer.withName("     ").withEmail("abc@gmail,com").build(), Arrays.asList("Name must contain at least 1 character", "Email does not match correct email pattern")),
            Arguments.of(buyer.withName("     ").withEmail("abc@gmail.com").withAddress(null).build(), Arrays.asList("Name must contain at least 1 character", "Address cannot be null"))
        );
    }

    @ParameterizedTest
    @MethodSource("setOfInvoiceEntriesAndValidationResults")
    void shouldValidateEntries(List<InvoiceEntry> entries, List<String> expected) {
        Invoice invoiceWithEntriesVariable = Invoice.builder()
            .withId(1L)
            .withNumber("9/FA/2019/5/DEP/INV")
            .withIssuedDate(LocalDate.of(2019, 5, 31))
            .withDueDate(LocalDate.of(2019, 6, 30))
            .withSeller(correctSeller)
            .withBuyer(correctBuyer)
            .withEntries(entries)
            .build();

        List<String> resultOfValidation = InvoiceValidator.validate(invoiceWithEntriesVariable);

        assertEquals(expected, resultOfValidation);
    }

    private static Stream<Arguments> setOfInvoiceEntriesAndValidationResults() {
        Builder entryBuilder1 = new Builder().withDescription("pencils").withQuantity(10L)
            .withPrice(
                BigDecimal.valueOf(10L)).withNetValue(BigDecimal.valueOf(100L))
            .withVatRate(Vat.VAT_23).withGrossValue(BigDecimal.valueOf(123L));
        Builder entryBuilder2 = new Builder().withDescription("sharpeners").withQuantity(5L)
            .withPrice(BigDecimal.valueOf(6L)).withNetValue(BigDecimal.valueOf(30L))
            .withVatRate(Vat.VAT_5).withGrossValue(BigDecimal.valueOf(31.50));

        List<InvoiceEntry> entries1 = new ArrayList<InvoiceEntry>();
        entries1.add(entryBuilder1.withNetValue(BigDecimal.valueOf(95L)).build());
        entries1.add(entryBuilder2.withNetValue(BigDecimal.valueOf(28L)).build());

        List<InvoiceEntry> entries2 = new ArrayList<InvoiceEntry>();
        entries2.add(entryBuilder1.withNetValue(BigDecimal.valueOf(100L)).withPrice(null).build());
        entries2.add(entryBuilder2.withNetValue(BigDecimal.valueOf(30L)).withDescription("    ").build());

        List<InvoiceEntry> entries3 = new ArrayList<InvoiceEntry>();
        entries3.add(entryBuilder1.withNetValue(BigDecimal.valueOf(100L)).withPrice(BigDecimal.valueOf(10L)).withQuantity(-10L).build());
        entries3.add(entryBuilder2.withNetValue(BigDecimal.valueOf(30L)).withDescription("sharpeners").withVatRate(null).build());

        List<InvoiceEntry> entries4 = new ArrayList<InvoiceEntry>();
        entries4.add(null);

        return Stream.of(
            Arguments.of(entries1, Arrays.asList("Quantity must be a quotient of net value and price", "Gross value does not match net value and vat rate",
                "Quantity must be a quotient of net value and price", "Gross value does not match net value and vat rate")),
            Arguments.of(entries2, Arrays.asList("Price cannot be null", "Description must contain at least 1 character various from whitespace")),
            Arguments.of(entries3, Arrays.asList("Quantity must be greater than zero", "Vat rate cannot be null")),
            Arguments.of(entries4, Arrays.asList("Invoice entry cannot be null"))
        );
    }
}
