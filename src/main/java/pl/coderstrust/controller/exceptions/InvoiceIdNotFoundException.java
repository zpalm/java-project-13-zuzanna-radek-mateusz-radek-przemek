package pl.coderstrust.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Attempt to get invoice by id that does not exist in database.")
public class InvoiceIdNotFoundException extends Exception {
    public InvoiceIdNotFoundException() {
    }

    public InvoiceIdNotFoundException(String message) {
        super(message);
    }

    public InvoiceIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvoiceIdNotFoundException(Throwable cause) {
        super(cause);
    }
}
