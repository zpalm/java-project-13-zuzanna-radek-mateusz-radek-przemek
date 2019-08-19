package pl.coderstrust.database.hibernate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderstrust.database.sql.model.Invoice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    default List<Invoice> findAllByIssuedDate(LocalDate startDate, LocalDate endDate){
        return this.findAll(new Specification<Invoice>(){
            @Override
            public Predicate toPredicate(Root<Invoice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(startDate!=null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"),startDate));
                }
                if (endDate!=null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"),endDate));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

}
