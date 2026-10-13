package com.acme.salary.seed;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class SeedDataGeneratorTest {
 private final SeedDataGenerator generator=new SeedDataGenerator();
 @Test void createsExactlyTenThousandEmployeesAndFiftyGroupsOfTwoHundred(){
  var first=generator.generate();assertEquals(10_000,first.size());
  var groups=first.stream().collect(java.util.stream.Collectors.groupingBy(e->e.jobTitle()+"|"+e.countryCode()+"|"+e.currency(),java.util.stream.Collectors.counting()));
  assertEquals(50,groups.size());assertTrue(groups.values().stream().allMatch(size->size==200));
  assertTrue(first.stream().allMatch(e->e.basic().signum()>=0&&e.allowances().signum()>=0&&e.deductions().signum()>=0));
  assertTrue(first.stream().allMatch(e->e.basic().add(e.allowances()).subtract(e.deductions()).signum()>=0));
 }
 @Test void repeatedGenerationUsesSameIdentifiersAndSalaryValues(){
  var a=generator.generate();var b=generator.generate();assertEquals(a.get(0).id(),b.get(0).id());assertEquals(a.get(9_999).salaryVersionId(),b.get(9_999).salaryVersionId());assertEquals(a.get(9_999).basic().add(a.get(9_999).allowances()),b.get(9_999).basic().add(b.get(9_999).allowances()));
 }
}
