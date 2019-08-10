package pl.coderstrust.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@RestControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleUnthrownException(Exception e, WebRequest request) {
        if (e instanceof ResponseStatusException) {
            return new ResponseEntity<>(createExceptionBody(((ResponseStatusException) e).getStatus(), e, ((ResponseStatusException) e).getReason(), request.getDescription(false)), ((ResponseStatusException) e).getStatus());
        }
        log.error("Handling {} due to {}", e.getClass().getSimpleName(), e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.CONFLICT;
        log.info("Status set to {}", status);
        //return handleExceptionInternal(e, createExceptionBody(status, e, "An unexpected error occured", request.getDescription(false)), headers, status, request);
        return new ResponseEntity<>(createExceptionBody(status, e, "An unexpected error occured", request.getDescription(false)), status);
    }

 /*   protected ResponseEntity<Object> handleExceptionInternal(Exception e, @Nullable Map<String, Object> body, HttpHeaders headers, HttpStatus status,
                                                             WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, e, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
*/
    private Map<String, Object> createExceptionBody(HttpStatus status, Exception e, String message, String path) {
        Map<String, Object> exceptionBody = new LinkedHashMap<>();
        exceptionBody.put("timestamp", LocalDateTime.now());
        exceptionBody.put("status", status.value());
        exceptionBody.put("error", status.getReasonPhrase());
        exceptionBody.put("message", message);
        exceptionBody.put("path", path);
        return exceptionBody;
    }
}
