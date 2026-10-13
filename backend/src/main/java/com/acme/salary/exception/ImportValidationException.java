package com.acme.salary.exception;
import com.acme.salary.dto.ImportPreviewResponse;
public class ImportValidationException extends RuntimeException{
 private final ImportPreviewResponse preview;
 public ImportValidationException(ImportPreviewResponse preview){super("The workbook has row errors. No employees were imported.");this.preview=preview;}
 public ImportPreviewResponse getPreview(){return preview;}
}
