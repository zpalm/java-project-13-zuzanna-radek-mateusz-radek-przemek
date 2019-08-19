package pl.coderstrust.database;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.apache.tomcat.jni.Local;
import pl.coderstrust.model.Invoice;

public interface Database {

    Invoice save(Invoice invoice) throws DatabaseOperationException;

    void delete(Long id) throws DatabaseOperationException;

    Optional<Invoice> getById(Long id) throws DatabaseOperationException;

    Optional<Invoice> getByNumber(String number) throws DatabaseOperationException;

    Collection<Invoice> getAll() throws DatabaseOperationException;

    void deleteAll() throws DatabaseOperationException;

    boolean exists(Long id) throws DatabaseOperationException;

    long count() throws DatabaseOperationException;

    Collection<Invoice> getByIssueDate(LocalDate startDate, LocalDate endDate) throws DatabaseOperationException;
}
