import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController,provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach,beforeEach,describe,expect,it } from 'vitest';
import { InsightsApiService } from './insights-api.service';
describe('InsightsApiService',()=>{
 let api:InsightsApiService;let http:HttpTestingController;
 beforeEach(()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting()]});api=TestBed.inject(InsightsApiService);http=TestBed.inject(HttpTestingController);});
 afterEach(()=>{http.verify();TestBed.resetTestingModule();});
 it('asks the server to group pay without dropping the currency filters',()=>{
  api.pay({groupBy:'jobTitle',country:'IN',department:'dept-1',jobTitle:''}).subscribe();const req=http.expectOne(r=>r.url==='/api/insights/pay');
  expect(req.request.params.get('groupBy')).toBe('jobTitle');expect(req.request.params.get('country')).toBe('IN');expect(req.request.params.get('department')).toBe('dept-1');req.flush({data:{groupBy:'jobTitle',groups:[]},message:null});
 });
});
