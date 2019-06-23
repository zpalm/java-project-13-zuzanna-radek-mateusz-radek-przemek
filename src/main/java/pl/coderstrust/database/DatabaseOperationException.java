package pl.coderstrust.database;

public class DatabaseOperationException extends Exception {

    public DatabaseOperationException() {
        super();
    }

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(Throwable cause) {
        super(cause);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
