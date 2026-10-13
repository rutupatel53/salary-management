import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { EmployeeApiService } from './employee-api.service';

describe('EmployeeApiService', () => {
 let api:EmployeeApiService;let http:HttpTestingController;
 beforeEach(()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting()]});api=TestBed.inject(EmployeeApiService);http=TestBed.inject(HttpTestingController);});
 afterEach(()=>http.verify());
 it('sends filters and bounded page fields to the server',()=>{
  api.list({q:'ria',country:'IN',department:'dept-1',page:2,size:25}).subscribe();
  const req=http.expectOne(r=>r.url==='/api/employees');
  expect(req.request.params.get('q')).toBe('ria');expect(req.request.params.get('country')).toBe('IN');expect(req.request.params.get('page')).toBe('2');expect(req.request.params.get('size')).toBe('25');
  req.flush({status:200,success:true,message:'Employees retrieved.',data:{items:[],page:2,size:25,totalItems:0,totalPages:0},errors:{}});
 });
 it('posts employee fields without transforming hire date',()=>{
  const body={employeeNumber:'EMP-1',firstName:'Riya',lastName:'Shah',workEmail:'riya@example.com',hireDate:'2021-06-02',countryCode:'IN',departmentId:'dept-1',jobTitle:'Engineer'};
  api.create(body).subscribe();const req=http.expectOne('/api/employees');expect(req.request.method).toBe('POST');expect(req.request.body.hireDate).toBe('2021-06-02');req.flush({status:201,success:true,data:{...body,id:'employee-1'},message:'Employee added.',errors:{}});
 });
});
