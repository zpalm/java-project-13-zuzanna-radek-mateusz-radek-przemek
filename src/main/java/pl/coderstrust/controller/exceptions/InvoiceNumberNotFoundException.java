package pl.coderstrust.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Attempt to get invoice by number that does not exist in database.")
public class InvoiceNumberNotFoundException extends Exception {
    public InvoiceNumberNotFoundException() {
    }

    public InvoiceNumberNotFoundException(String message) {
        super(message);
    }

    public InvoiceNumberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvoiceNumberNotFoundException(Throwable cause) {
        super(cause);
    }
}
