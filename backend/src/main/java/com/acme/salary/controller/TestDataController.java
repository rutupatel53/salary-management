package com.acme.salary.controller;
import com.acme.salary.seed.TestDataResetService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/test")
@ConditionalOnProperty(name="app.test-tools-enabled",havingValue="true")
public class TestDataController{
 private final TestDataResetService reset;
 public TestDataController(TestDataResetService reset){this.reset=reset;}
 @PostMapping("/reset") public ResetResponse reset(){return new ResetResponse(reset.reset());}
 public record ResetResponse(int employees){}
}
