package pl.coderstrust.database.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderstrust.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
