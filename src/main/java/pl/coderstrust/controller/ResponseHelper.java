package pl.coderstrust.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {

    public static ResponseEntity<?> createPdfOkResponse(byte[] array) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(array, responseHeaders, HttpStatus.OK);
    }

    public static ResponseEntity<?> createJsonOkResponse(Object body) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
    }

    public static ResponseEntity<?> createJsonCreatedResponse(Object body, String location) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        responseHeaders.setLocation(URI.create(location));
        return new ResponseEntity<>(body, responseHeaders, HttpStatus.CREATED);
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
