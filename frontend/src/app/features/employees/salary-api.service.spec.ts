import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { SalaryApiService } from './salary-api.service';
describe('SalaryApiService',()=>{
 let api:SalaryApiService;let http:HttpTestingController;
 beforeEach(()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting()]});api=TestBed.inject(SalaryApiService);http=TestBed.inject(HttpTestingController);});
 afterEach(()=>http.verify());
 it('posts each salary version as an append request',()=>{
  api.append('employee-1',{basic:1000.12,allowances:100,deductions:25,currencyCode:'INR',effectiveDate:'2025-02-01'}).subscribe();
  const request=http.expectOne('/api/employees/employee-1/salary-history');expect(request.request.method).toBe('POST');expect(request.request.body).toEqual({basic:1000.12,allowances:100,deductions:25,currencyCode:'INR',effectiveDate:'2025-02-01'});request.flush({data:{},message:'Salary version added.'});
 });
});
