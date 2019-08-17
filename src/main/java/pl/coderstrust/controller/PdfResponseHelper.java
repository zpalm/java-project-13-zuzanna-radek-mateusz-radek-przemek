package pl.coderstrust.controller;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class PdfResponseHelper {

    public static ResponseEntity<?> createPdfResponse(byte[] array) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(array, responseHeaders, HttpStatus.OK);
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
