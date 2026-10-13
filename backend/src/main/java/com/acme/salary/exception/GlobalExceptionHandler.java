package com.acme.salary.exception;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(NotFoundException.class) ResponseEntity<?> notFound(RuntimeException e){return ResponseEntity.status(404).body(Map.of("message",e.getMessage(),"errors",Map.of()));}
 @ExceptionHandler(ConflictException.class) ResponseEntity<?> conflict(RuntimeException e){return ResponseEntity.status(409).body(Map.of("message",e.getMessage(),"errors",Map.of()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> invalid(MethodArgumentNotValidException e){
  Map<String,String> errors=new TreeMap<>(); e.getBindingResult().getFieldErrors().forEach(x->errors.putIfAbsent(x.getField(),x.getDefaultMessage()));
  return ResponseEntity.badRequest().body(Map.of("message","Please correct the highlighted fields.","errors",errors));
 }
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> integrity(){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","A matching record already exists or the record is referenced.","errors",Map.of()));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<?> badRequest(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage(),"errors",Map.of()));}
}
