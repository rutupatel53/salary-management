package com.acme.salary.service;
import com.acme.salary.dto.ImportConfirmResponse;
import com.acme.salary.dto.ImportPreviewResponse;
import org.springframework.web.multipart.MultipartFile;
public interface ExcelImportService{
 byte[] template();
 ImportPreviewResponse preview(MultipartFile file);
 ImportConfirmResponse confirm(MultipartFile file);
}
