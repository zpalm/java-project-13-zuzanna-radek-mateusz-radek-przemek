package pl.coderstrust.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pl.coderstrust.model.Invoice;

public class ResponseHelper {

    static ResponseEntity<?> createPdfResponse(byte[] array) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(array, responseHeaders, HttpStatus.OK);
    }

    static ResponseEntity<?> createJsonOkResponse(Object responseBody) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
    }

    static ResponseEntity<?> createJsonCreatedResponse(Invoice addedInvoice) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        responseHeaders.setLocation(URI.create(String.format("/invoices/%d", addedInvoice.getId())));
        return new ResponseEntity<>(addedInvoice, responseHeaders, HttpStatus.CREATED);
    }

    public static boolean isPdfResponse(HttpHeaders httpHeaders) {
        Optional<MediaType> type = httpHeaders.getAccept().stream()
            .filter(x -> x.isCompatibleWith(MediaType.APPLICATION_PDF) || x.isCompatibleWith(MediaType.APPLICATION_JSON))
            .findFirst();
        if (type.isEmpty()) {
            return false;
        }
        return !type.get().isWildcardType() && type.get().isCompatibleWith(MediaType.APPLICATION_PDF);
    }
}
