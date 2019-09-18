package pl.coderstrust.database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "sql")
public class SqlDatabase implements Database{

    private final JdbcTemplate template;

    @Autowired
    public SqlDatabase(JdbcTemplate template){
        this.template = template;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        return null;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {

    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        String sqlQuery = getAllInvoicesAndEntriesSQLQuery();
        List<Invoice> results = template.query(sqlQuery, (rs, numRow) ->
            Invoice.builder()
                   .withId(rs.getLong("id"))
                   .build());
        return new ArrayList<>();
    }

    public static String getAllInvoicesAndEntriesSQLQuery() {
        StringBuilder select = new StringBuilder();
        select.append("SELECT * ")
              .append("FROM invoice AS i ")
              .append("JOIN invoice_entries AS ies ")
              .append("ON i.id= ies.invoice_id ")
              .append("JOIN invoice_entry AS ie ")
              .append("ON ies.entries_id= ie.id");
        return select.toString();
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {

    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        return false;
    }

    @Override
    public long count() throws DatabaseOperationException {
        return 0;
    }

    @Override
    public Collection<Invoice> getByIssueDate(LocalDate startDate, LocalDate endDate) throws DatabaseOperationException {
        return null;
    }
}
