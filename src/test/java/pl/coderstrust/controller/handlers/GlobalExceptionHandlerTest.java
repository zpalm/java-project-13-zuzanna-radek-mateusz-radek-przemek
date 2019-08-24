package pl.coderstrust.controller.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler;
    WebRequest request;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        request = Mockito.mock(WebRequest.class);
    }

    @ParameterizedTest
    @MethodSource("statusExceptionsArguments")
    void shouldReturnJsonResponseWithCorrectStatusAndBodyWhenResponseStatusExceptionIsThrown(ResponseStatusException exception) {
        when(request.getHeaderValues("accept")).thenReturn(new String[]{"application/json"});
        ResponseEntity<Object> response = handler.handleUnexpectedException(exception, request);
        String stringBody = response.getBody().toString();
        assertEquals(exception.getReason(), extractMessageFromResponseBody(stringBody));
        assertEquals(exception.getStatus(), response.getStatusCode());
        verify(request).getHeaderValues("accept");
    }

    @ParameterizedTest
    @MethodSource("statusExceptionsArguments")
    void shouldReturnPdfResponseWithCorrectStatusWhenResponseStatusExceptionIsThrown(ResponseStatusException exception) {
        when(request.getHeaderValues("accept")).thenReturn(new String[]{"application/pdf"});
        ResponseEntity<Object> response = handler.handleUnexpectedException(exception, request);
        assertEquals(exception.getStatus(), response.getStatusCode());
        verify(request).getHeaderValues("accept");
    }

    private static Stream<Arguments> statusExceptionsArguments() {
        return Stream.of(
            Arguments.of(new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to get invoice by id that does not exist in database.")),
            Arguments.of(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to get invoice providing null number.")),
            Arguments.of(new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to get invoice by number that does not exist in database.")),
            Arguments.of(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to add null invoice.")),
            Arguments.of(new ResponseStatusException(HttpStatus.CONFLICT, "Attempt to add invoice already existing in database.")),
            Arguments.of(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to update invoice providing null invoice.")),
            Arguments.of(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to update invoice providing different invoice id.")),
            Arguments.of(new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to update not existing invoice.")),
            Arguments.of(new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt to delete not existing invoice."))
        );
    }

    @Test
    void shouldReturnJsonResponseWithCorrectStatusAndBodyWhenUnexpectedErrorOccur() {
        when(request.getHeaderValues("accept")).thenReturn(new String[]{"application/json"});
        ResponseEntity<Object> response = handler.handleUnexpectedException(new NullPointerException(), request);
        String stringBody = response.getBody().toString();
        assertEquals("An unexpected error occurred", extractMessageFromResponseBody(stringBody));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(request).getHeaderValues("accept");
    }

    @Test
    void shouldReturnPdfResponseWithCorrectStatusWhenUnexpectedErrorOccur() {
        when(request.getHeaderValues("accept")).thenReturn(new String[]{"application/pdf"});
        ResponseEntity<Object> response = handler.handleUnexpectedException(new NullPointerException(), request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(request).getHeaderValues("accept");
    }

    private String extractMessageFromResponseBody(String body) {
        int startIndex = body.indexOf("message=");
        int endIndex = body.indexOf(", path=");
        return body.substring(startIndex, endIndex).replaceFirst("message=", "");
    }
}
