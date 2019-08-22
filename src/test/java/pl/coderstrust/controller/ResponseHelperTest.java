package pl.coderstrust.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoicePdfService;
import pl.coderstrust.service.ServiceOperationException;

class ResponseHelperTest {

    @Test
    void shouldCreatePdfResponse() throws ServiceOperationException {
        InvoicePdfService invoicePdfService = new InvoicePdfService();
        byte[] bytes = invoicePdfService.createPdf(InvoiceGenerator.getRandomInvoice());

        ResponseEntity<?> response = ResponseHelper.createPdfOkResponse(bytes);
        assertEquals(bytes, response.getBody());
    }

    @Test
    void shouldCreateJsonResponseWithOKStatus() {
        Invoice randomInvoice = InvoiceGenerator.getRandomInvoice();
        ResponseEntity<?> response = ResponseHelper.createJsonOkResponse(randomInvoice);
        assertEquals(randomInvoice + "|" + MediaType.APPLICATION_JSON + "|" + HttpStatus.OK
            , response.getBody() + "|" + response.getHeaders().getContentType() + "|" + response.getStatusCode());
    }

    @Test
    void shouldThrowExceptionWhenNullIsPassedAsArgument(){
        Invoice randomInvoice = null;
        ResponseEntity<?> response = ResponseHelper.createJsonOkResponse(randomInvoice);
        assertEquals(randomInvoice + "|" + MediaType.APPLICATION_JSON + "|" + HttpStatus.OK
            , response.getBody() + "|" + response.getHeaders().getContentType() + "|" + response.getStatusCode());
    }

    @Test
    void shouldThrowExceptionWhenUriHasIncorrectFormat(){
        Invoice randomInvoice = InvoiceGenerator.getRandomInvoice();
        String location = "!#FR@E$W$F@";
        ResponseEntity<?> response = ResponseHelper.createJsonCreatedResponse(randomInvoice, location);
        MediaType responseContentType = response.getHeaders().getContentType();
        URI responseLocation = response.getHeaders().getLocation();
        assertEquals(randomInvoice + "|" + MediaType.APPLICATION_JSON + "|" + HttpStatus.CREATED + "|" + location,
            response.getBody() + "|" + responseContentType + "|" + response.getStatusCode() + "|" + responseLocation);
    }

    @Test
    void shouldCreateJsonResponseWithCreatedStatus() {
        Invoice randomInvoice = InvoiceGenerator.getRandomInvoice();
        String location = "invoices/1";
        ResponseEntity<?> response = ResponseHelper.createJsonCreatedResponse(randomInvoice, location);
        MediaType responseContentType = response.getHeaders().getContentType();
        URI responseLocation = response.getHeaders().getLocation();
        assertEquals(randomInvoice + "|" + MediaType.APPLICATION_JSON + "|" + HttpStatus.CREATED + "|" + location,
            response.getBody() + "|" + responseContentType + "|" + response.getStatusCode() + "|" + responseLocation);
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

    private byte[] convertObjectToByteArray(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(bos);
            outputStream.writeObject(object);
            outputStream.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("An Error occurred while converting object to byte array");
        } finally {
            bos.close();
        }
    }
}
