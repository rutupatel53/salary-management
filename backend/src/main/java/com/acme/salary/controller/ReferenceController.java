package com.acme.salary.controller;
import com.acme.salary.repository.CountryRepository;
import com.acme.salary.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api")
public class ReferenceController {
 private final CountryRepository countries;private final DepartmentRepository departments;
 public ReferenceController(CountryRepository c,DepartmentRepository d){countries=c;departments=d;}
 @GetMapping("/countries") public List<CountryView> countries(){return countries.findAll().stream().sorted(java.util.Comparator.comparing(x->x.getName())).map(c->new CountryView(c.getCode(),c.getName(),c.getCurrencyCode())).toList();}
 @GetMapping("/departments") public List<DepartmentView> departments(){return departments.findByActiveTrueOrderByName().stream().map(d->new DepartmentView(d.getId(),d.getName())).toList();}
 public record CountryView(String countryCode,String name,String currencyCode){}
 public record DepartmentView(UUID id,String name){}
}
