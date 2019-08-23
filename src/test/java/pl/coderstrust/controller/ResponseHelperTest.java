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
    void shouldCreatePdfResponseThrowExceptionWhenNullIsPassed() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ResponseHelper.createPdfOkResponse(null);
        });
        assertEquals("Passed byte array is null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("setOfObjectsToCreateOkResponse")
    void shouldCreateJsonResponseWithOkStatus(Object body) {
        ResponseEntity<?> expected = createExpectedResponse(body, MediaType.APPLICATION_JSON, HttpStatus.OK);
        ResponseEntity<?> response = ResponseHelper.createJsonOkResponse(body);
        assertEquals(expected, response);
    }

    private static Stream<Arguments> setOfObjectsToCreateOkResponse() {
        return Stream.of(
            Arguments.of(InvoiceGenerator.getRandomInvoice()),
            Arguments.of(InvoiceGenerator.getRandomInvoiceWithSpecificId(45L)),
            Arguments.of(InvoiceEntryGenerator.getRandomEntry()),
            Arguments.of(CompanyGenerator.getRandomCompany()),
            Arguments.of("Any example String")
        );
    }

    @Test
    void shouldCreateJsonOkResponseThrowExceptionWhenNullIsPassedAsArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ResponseHelper.createJsonOkResponse(null);
        });
        assertEquals("Response body cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("setOfObjectsAndLocationsToCreateJsonCreatedResponse")
    void shouldCreateJsonResponseWithCreatedStatus(Object body, String location) {
        ResponseEntity<?> expected = createExpectedResponseWithLocation(body, MediaType.APPLICATION_JSON, HttpStatus.CREATED, location);
        ResponseEntity<?> response = ResponseHelper.createJsonCreatedResponse(body, location);
        assertEquals(expected, response);
    }

    private static Stream<Arguments> setOfObjectsAndLocationsToCreateJsonCreatedResponse() {
        return Stream.of(
            Arguments.of(InvoiceGenerator.getRandomInvoiceWithSpecificId(3564324L), "invoices/1"),
            Arguments.of(InvoiceGenerator.getRandomInvoice(), "invoices/356"),
            Arguments.of("Any not null object", "anyStringWithoutWhitespaces"),
            Arguments.of(CompanyGenerator.getRandomCompany(), "company/2/invoice/12/entry/10")
        );
    }

    @Test
    void shouldCreateJsonCreatedResponseThrowExceptionWhenNullIsPassedAsArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ResponseHelper.createJsonCreatedResponse(null, "invoices/1");
        });
        assertEquals("Response body cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("setOfIncorrectLocations")
    void shouldCreateJsonCreatedResponseThrowExceptionWhenLocationContainsWhitespaces(String location) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ResponseHelper.createJsonCreatedResponse(InvoiceGenerator.getRandomInvoice(), location);
        });
        assertEquals("Passed location cannot contain whitespaces", exception.getMessage());
    }

    private static Stream<Arguments> setOfIncorrectLocations() {
        return Stream.of(
            Arguments.of("    "),
            Arguments.of(" invoices/1"),
            Arguments.of("invoices/1 "),
            Arguments.of(" in voi ce s/ 1 "),
            Arguments.of("invoices/ 1")
        );
    }

    @Test
    void shouldReturnFalseWhenHttpHeadersAreEmpty() {
        HttpHeaders headers = new HttpHeaders();
        assertFalse(ResponseHelper.isPdfResponse(headers));
    }

    @ParameterizedTest
    @MethodSource("setOfMediaTypesWithPdfNotPlacedInTheFirstPosition")
    void shouldReturnFalseWhenPdfIsNotTheFirstAcceptedResponseFormat(List<MediaType> mediaTypes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(mediaTypes);
        assertFalse(ResponseHelper.isPdfResponse(headers));
    }

    private static Stream<Arguments> setOfMediaTypesWithPdfNotPlacedInTheFirstPosition() {
        return Stream.of(
            Arguments.of(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_PDF, MediaType.APPLICATION_XML)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_PDF)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_ATOM_XML)),
            Arguments.of(Arrays.asList(MediaType.valueOf("*/*"), MediaType.APPLICATION_PDF)),
            Arguments.of(Arrays.asList(MediaType.valueOf("*/*")))
        );
    }

    @ParameterizedTest
    @MethodSource("setOfMediaTypesWithPdfAtFirstPosition")
    void shouldReturnTrueWhenPdfIsTheFirstAcceptedResponseFormat(List<MediaType> mediaTypes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(mediaTypes);
        assertTrue(ResponseHelper.isPdfResponse(headers));
    }

    private static Stream<Arguments> setOfMediaTypesWithPdfAtFirstPosition() {
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

    private ResponseEntity<?> createExpectedResponseWithLocation(Object body, MediaType mediaType, HttpStatus status, String location) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(mediaType);
        responseHeaders.setLocation(URI.create(location));
        return new ResponseEntity<>(body, responseHeaders, status);
    }
}
