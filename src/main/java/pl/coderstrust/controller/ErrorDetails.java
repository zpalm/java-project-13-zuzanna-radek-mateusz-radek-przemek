package pl.coderstrust.controller;

import java.time.LocalDate;

public class ErrorDetails {
    private LocalDate timestamp;
    private String message;
    private String details;

    public ErrorDetails(String message, String details) {
        this.timestamp=LocalDate.now();
        this.message = message;
        this.details = details;
    }
}
