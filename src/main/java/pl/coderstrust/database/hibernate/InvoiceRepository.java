package pl.coderstrust.database.hibernate;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.coderstrust.database.sql.model.Invoice;


public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @Query(value = "SELECT i FROM Invoice WHERE i.issuedDate>=:startDate AND i.issuedDate<=:endDate")
    Collection<Invoice> findAllByIssuedDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
