package com.acme.salary.dto;
import java.util.List;
public record ImportPreviewResponse(int totalRows,int validRows,int invalidRows,List<String> fileErrors,List<ImportRowResult> rows){}
