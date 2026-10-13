package com.acme.salary.seed;

import com.acme.salary.util.UuidV5;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class SeedDataGenerator {
 public static final int EMPLOYEE_COUNT=10_000;
 private static final UUID NAMESPACE=UUID.fromString("b0548a2a-61f6-5ce7-99c6-5e76cb2f8fd5");
 private static final UUID[] DEPARTMENTS={UUID.fromString("00000000-0000-5000-8000-000000000001"),UUID.fromString("00000000-0000-5000-8000-000000000002"),UUID.fromString("00000000-0000-5000-8000-000000000003"),UUID.fromString("00000000-0000-5000-8000-000000000004"),UUID.fromString("00000000-0000-5000-8000-000000000005"),UUID.fromString("00000000-0000-5000-8000-000000000006")};
 private static final String[] COUNTRIES={"US","IN","GB","DE","CA"};
 private static final String[] CURRENCIES={"USD","INR","GBP","EUR","CAD"};
 private static final String[] TITLES={"Software Engineer","Senior Engineer","Engineering Manager","Product Manager","Data Analyst","Finance Analyst","HR Partner","Sales Manager","Operations Lead","Designer"};
 private static final String[] FIRST={"Avery","Casey","Jordan","Morgan","Riya","Sam","Taylor","Uma","Dev","Nina","Alex","Kai","Leah","Noah","Asha","Milan"};
 private static final String[] LAST={"Shah","Patel","Morgan","Kim","Singh","Brown","Garcia","Taylor","Chen","Reed","Das","Miller","Roy","Nguyen","Khan","Wilson"};

 public List<SeedEmployee> generate(){
  List<SeedEmployee> rows=new ArrayList<>(EMPLOYEE_COUNT);
  for(int c=0;c<COUNTRIES.length;c++) for(int t=0;t<TITLES.length;t++) for(int i=0;i<200;i++){
   String number="ACME-"+COUNTRIES[c]+"-J"+String.format("%02d",t+1)+"-"+String.format("%03d",i);
   UUID employeeId=UuidV5.fromName(NAMESPACE,"employee:"+number);
   BigDecimal total=new BigDecimal(3000L+(i%20)*100L);
   if(i==199) total=new BigDecimal("25000.00"); // one clear IQR outlier in every job/country/currency group
   BigDecimal basic=total.multiply(new BigDecimal("0.85")).setScale(2);
   BigDecimal allowances=total.subtract(basic).setScale(2);
   BigDecimal deductions=total.multiply(new BigDecimal("0.10")).setScale(2);
   UUID departmentId=DEPARTMENTS[t%6];
   rows.add(new SeedEmployee(employeeId,number,FIRST[(c*3+t+i)%FIRST.length],LAST[(c+t*2+i/2)%LAST.length],
    number.toLowerCase().replace('-','.')+"@example.test",LocalDate.of(2010,1,1).plusDays((c*10L+t*200L+i)*2L),
    COUNTRIES[c],departmentId,TITLES[t],CURRENCIES[c],basic,allowances,deductions,
    UuidV5.fromName(NAMESPACE,"salary:"+number+":2025-01-01"),LocalDate.of(2025,1,1)));
  }
  return List.copyOf(rows);
 }
}
