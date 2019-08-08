package pl.coderstrust.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Attempt to get invoice providing null number.")
public class NullInvoiceNumberException extends Exception {
    public NullInvoiceNumberException() {
    }

    public NullInvoiceNumberException(String message) {
        super(message);
    }

    public NullInvoiceNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullInvoiceNumberException(Throwable cause) {
        super(cause);
    }
}
