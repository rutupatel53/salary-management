package com.acme.salary.specification;
import com.acme.salary.model.Employee;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;
import java.util.UUID;

public final class EmployeeSpecifications {
 private EmployeeSpecifications() { }
 public static Specification<Employee> active(){return (root,q,cb)->cb.isNull(root.get("archivedAt"));}
 public static Specification<Employee> filters(String query,String country,UUID department,String jobTitle){
  return (root,q,cb)->{
   var predicates=cb.conjunction();
   if(query!=null&&!query.isBlank()){
    String pattern="%"+query.trim().toLowerCase()+"%";
    predicates=cb.and(predicates,cb.or(cb.like(cb.lower(root.get("employeeNumber")),pattern),cb.like(cb.lower(root.get("firstName")),pattern),cb.like(cb.lower(root.get("lastName")),pattern),cb.like(cb.lower(root.get("workEmail")),pattern),cb.like(cb.lower(root.get("jobTitle")),pattern)));
   }
   if(country!=null&&!country.isBlank())predicates=cb.and(predicates,cb.equal(root.join("country",JoinType.INNER).get("code"),country));
   if(department!=null)predicates=cb.and(predicates,cb.equal(root.join("department",JoinType.INNER).get("id"),department));
   if(jobTitle!=null&&!jobTitle.isBlank())predicates=cb.and(predicates,cb.equal(cb.lower(root.get("jobTitle")),jobTitle.toLowerCase()));
   return predicates;
  };
 }
}
