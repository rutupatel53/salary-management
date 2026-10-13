package com.acme.salary.enums;
public enum InsightDimension{
 COUNTRY("country_ref.name"),DEPARTMENT("department_ref.name"),JOB_TITLE("employee.job_title");
 private final String expression;
 InsightDimension(String expression){this.expression=expression;}
 public String expression(){return expression;}
 public static InsightDimension parse(String value){
  if(value==null)throw new IllegalArgumentException("groupBy must be country, department, or jobTitle.");
  return switch(value){case "country"->COUNTRY;case "department"->DEPARTMENT;case "jobTitle"->JOB_TITLE;default->throw new IllegalArgumentException("groupBy must be country, department, or jobTitle.");};
 }
}
