package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.ImportConfirmResponse;
import com.acme.salary.dto.ImportPreviewResponse;
import com.acme.salary.service.ExcelImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/imports")
public class ImportController {
    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private final ExcelImportService service;

    public ImportController(ExcelImportService service) { this.service = service; }

    @GetMapping("/template")
    public ResponseEntity<byte[]> template() {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(XLSX))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=salary-management-template.xlsx")
                .body(service.template());
    }

    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportPreviewResponse> preview(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(HttpStatus.OK.value(), service.preview(file),
                "Review row errors before confirming the import.");
    }

    @PostMapping(value = "/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImportConfirmResponse>> confirm(@RequestPart("file") MultipartFile file) {
        ImportConfirmResponse result = service.confirm(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), result, "Import completed."));
    }
}
