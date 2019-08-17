package pl.coderstrust.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class PdfResponseHelperTest {

    @Test
    void shouldReturnFalseWhenHttpHeadersAreEmpty() {
        HttpHeaders headers = new HttpHeaders();
        assertFalse(PdfResponseHelper.isPdfResponse(headers));
    }

    @ParameterizedTest
    @MethodSource("setOfMediaTypesWithPdfNotPlacedInTheFirstPosition")
    void shouldReturnFalseWhenPdfIsNotTheFirstAcceptedResponseFormat(List<MediaType> mediaTypes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(mediaTypes);
        assertFalse(PdfResponseHelper.isPdfResponse(headers));
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
        assertTrue(PdfResponseHelper.isPdfResponse(headers));
    }

    private static Stream<Arguments> setOfMediaTypesWithPdfAtFirstPosition() {
        return Stream.of(
            Arguments.of(Arrays.asList(MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON)),
            Arguments.of(Arrays.asList(MediaType.APPLICATION_PDF))
        );
    }

}