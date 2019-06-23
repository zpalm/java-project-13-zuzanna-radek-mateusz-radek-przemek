package pl.coderstrust.database;

import java.util.List;
import pl.coderstrust.model.Invoice;

public interface Database {

    void save(Invoice invoice) throws DatabaseOperationException;

    void delete(Invoice invoice) throws DatabaseOperationException;

    Invoice getById(Long id) throws DatabaseOperationException;

    Invoice getByNumber(String number) throws DatabaseOperationException;

    List<Invoice> getAll() throws DatabaseOperationException;

    void deleteAll() throws DatabaseOperationException;

    boolean exists(Invoice invoice) throws DatabaseOperationException;

    int count() throws DatabaseOperationException;
}
