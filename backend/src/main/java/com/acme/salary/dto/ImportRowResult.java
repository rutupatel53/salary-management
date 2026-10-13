package com.acme.salary.dto;
import java.util.List;
public record ImportRowResult(int rowNumber,String employeeNumber,boolean valid,List<String> errors){}
