package pl.coderstrust.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pl.coderstrust.generators.CompanyGenerator;
import pl.coderstrust.generators.InvoiceEntryGenerator;
import pl.coderstrust.generators.InvoiceGenerator;

class ResponseHelperTest {

    @Test
    void shouldCreatePdfResponse() {
        byte[] bytes = new byte[100];
        Arrays.fill(bytes, Byte.valueOf("1"));
        ResponseEntity<?> expected = createExpectedResponse(bytes, MediaType.APPLICATION_PDF, HttpStatus.OK);
        ResponseEntity<?> response = ResponseHelper.createPdfOkResponse(bytes);
        assertEquals(expected, response);
    }

    @Test
    void createPdfResponseMethodShouldThrowExceptionWhenNullIsPassed() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ResponseHelper.createPdfOkResponse(null));
        assertEquals("Response body cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("jsonOkResponseArguments")
    void shouldCreateJsonResponseWithOkStatus(Object body) {
        ResponseEntity<?> expected = createExpectedResponse(body, MediaType.APPLICATION_JSON, HttpStatus.OK);
        ResponseEntity<?> response = ResponseHelper.createJsonOkResponse(body);
        assertEquals(expected, response);
    }

    private static Stream<Arguments> jsonOkResponseArguments() {
        return Stream.of(
            Arguments.of(InvoiceGenerator.getRandomInvoice()),
            Arguments.of(InvoiceGenerator.getRandomInvoiceWithSpecificId(45L)),
            Arguments.of(InvoiceEntryGenerator.getRandomEntry()),
            Arguments.of(CompanyGenerator.getRandomCompany()),
            Arguments.of("Any example String")
        );
    }

    @Test
    void createJsonOkResponseMethodShouldThrowExceptionWhenNullIsPassed() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ResponseHelper.createJsonOkResponse(null));
        assertEquals("Response body cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("jsonCreatedResponseArguments")
    void shouldCreateJsonResponseWithCreatedStatus(Object body, String location) {
        ResponseEntity<?> expected = createExpectedResponse(body, MediaType.APPLICATION_JSON, HttpStatus.CREATED, location);
        ResponseEntity<?> response = ResponseHelper.createJsonCreatedResponse(body, location);
        assertEquals(expected, response);
    }

    private static Stream<Arguments> jsonCreatedResponseArguments() {
        return Stream.of(
            Arguments.of(InvoiceGenerator.getRandomInvoiceWithSpecificId(3564324L), "invoices/1"),
            Arguments.of(InvoiceGenerator.getRandomInvoice(), "invoices/356"),
            Arguments.of("Any not null object", "anyStringWithoutWhitespaces"),
            Arguments.of(CompanyGenerator.getRandomCompany(), "company/2/invoice/12/entry/10")
        );
    }

    @Test
    void createJsonCreatedResponseMethodShouldThrowExceptionWhenNullIsPassedAsBody() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ResponseHelper.createJsonCreatedResponse(null, "invoices/1"));
        assertEquals("Response body cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidLocationArguments")
    void createJsonCreatedResponseMethodShouldThrowExceptionWhenLocationContainsWhitespaces(String location) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ResponseHelper.createJsonCreatedResponse(InvoiceGenerator.getRandomInvoice(), location));
        assertEquals("Passed location cannot contain whitespaces", exception.getMessage());
    }

    private static Stream<Arguments> invalidLocationArguments() {
        return Stream.of(
            Arguments.of("    "),
            Arguments.of(" invoices/1"),
            Arguments.of("invoices/1 "),
            Arguments.of(" in voi ce s/ 1 "),
            Arguments.of("invoices/ 1")
        );
    }

    @ParameterizedTest
    @MethodSource("jsonValidationFailedArguments")
    void shouldCreateJsonFailedValidationResponseWithBadRequestStatus(List<String> violations) {
        ResponseEntity<?> expected = createExpectedResponse(violations, MediaType.APPLICATION_JSON, HttpStatus.BAD_REQUEST);
        ResponseEntity<?> response = ResponseHelper.createJsonFailedValidationResponse(violations);
        assertEquals(expected, response);
    }

    private static Stream<Arguments> jsonValidationFailedArguments() {
        return Stream.of(
            Arguments.of(
                List.of("Number must contain at least 1 digit", "Issued date must be earlier than due date"),
                List.of("Tax id does not match correct tax id pattern", "Account number does not match correct account number pattern"),
                List.of("Email does not match correct email pattern")
            )
        );
    }

    @Test
    void createJsonFailedValidationResponseMethodShouldThrowExceptionWhenNullIsPassed() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ResponseHelper.createJsonFailedValidationResponse(null));
        assertEquals("Violations cannot be null", exception.getMessage());
    }

    @Test
    void shouldReturnFalseWhenHttpHeadersAreEmpty() {
        HttpHeaders headers = new HttpHeaders();
        assertFalse(ResponseHelper.isPdfResponse(headers));
    }

    @ParameterizedTest
    @MethodSource("mediaTypesWithPdfNotPlacedInTheFirstPosition")
    void shouldReturnFalseWhenPdfIsNotTheFirstAcceptedResponseFormat(List<MediaType> mediaTypes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(mediaTypes);
        assertFalse(ResponseHelper.isPdfResponse(headers));
    }

    private static Stream<Arguments> mediaTypesWithPdfNotPlacedInTheFirstPosition() {
        return Stream.of(
            Arguments.of(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_PDF, MediaType.APPLICATION_XML)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_PDF)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_ATOM_XML)),
            Arguments.of(Arrays.asList(MediaType.valueOf("*/*"), MediaType.APPLICATION_PDF)),
            Arguments.of(Arrays.asList(MediaType.valueOf("*/*")))
        );
    }

    @ParameterizedTest
    @MethodSource("mediaTypesWithPdfAtFirstPosition")
    void shouldReturnTrueWhenPdfIsTheFirstAcceptedResponseFormat(List<MediaType> mediaTypes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(mediaTypes);
        assertTrue(ResponseHelper.isPdfResponse(headers));
    }

    private static Stream<Arguments> mediaTypesWithPdfAtFirstPosition() {
        return Stream.of(
            Arguments.of(Arrays.asList(MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_PDF))
        );
    }

    private ResponseEntity<?> createExpectedResponse(Object body, MediaType mediaType, HttpStatus status) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(mediaType);
        return new ResponseEntity<>(body, responseHeaders, status);
    }

    private ResponseEntity<?> createExpectedResponse(Object body, MediaType mediaType, HttpStatus status, String location) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(mediaType);
        responseHeaders.setLocation(URI.create(location));
        return new ResponseEntity<>(body, responseHeaders, status);
    }
}
