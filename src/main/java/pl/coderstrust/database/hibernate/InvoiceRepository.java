package pl.coderstrust.database.hibernate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderstrust.database.sql.model.Invoice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @Query(value = "SELECT i FROM Invoice WHERE i.issuedDate>=:startDate AND i.issuedDate<=:endDate")
    List<Invoice> findAllByIssuedDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
