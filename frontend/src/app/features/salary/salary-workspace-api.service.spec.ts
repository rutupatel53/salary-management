import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { SalaryWorkspaceApiService } from './salary-workspace-api.service';

describe('SalaryWorkspaceApiService', () => {
  let api: SalaryWorkspaceApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(SalaryWorkspaceApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    TestBed.resetTestingModule();
  });

  it('sends a versioned annual CTC preview request before assignment', () => {
    const assignment = {
      employeeId: 'employee-1', templateId: 'template-1', templateRevision: 3,
      annualCtc: '1200000.00', effectiveDate: '2027-01-01'
    };
    api.preview(assignment).subscribe();
    const request = http.expectOne('/api/salary-assignments/preview');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(assignment);
    request.flush({ status: 200, success: true, message: 'Review this salary breakdown before confirming.', data: {}, errors: {} });
  });

  it('creates the template with its country-scoped calculation components', () => {
    const template = {
      name: 'Engineering standard', countryCode: 'IN', status: 'ACTIVE',
      components: [
        { name: 'Basic', category: 'BASIC', calculation: 'PERCENT_CTC', value: 40 },
        { name: 'Balance', category: 'ALLOWANCE', calculation: 'BALANCE', value: 0 }
      ]
    };
    api.save(template).subscribe();
    const request = http.expectOne('/api/salary-templates');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(template);
    request.flush({ status: 201, success: true, message: 'Salary template created.', data: {}, errors: {} });
  });
});
