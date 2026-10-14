import { TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EMPTY, Subject } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { EmployeeFormDialogComponent } from './employee-form-dialog.component';
import { EmployeeApiService } from './employee-api.service';
import { ConfirmDialogService } from '../../shared/confirm-dialog.service';

describe('EmployeeFormDialogComponent', () => {
  const api = { create: vi.fn(), update: vi.fn() };
  const dialog = { close: vi.fn(), backdropClick: () => EMPTY, keydownEvents: () => EMPTY };
  beforeEach(async () => {
    vi.clearAllMocks();
    TestBed.configureTestingModule({
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { employee: null, countries: [], departments: [] } },
        { provide: MatDialogRef, useValue: dialog },
        { provide: EmployeeApiService, useValue: api },
        { provide: ConfirmDialogService, useValue: { confirm: vi.fn() } }
      ]
    });
    TestBed.overrideComponent(EmployeeFormDialogComponent, { set: { template: '', styles: [] } });
    await TestBed.compileComponents();
  });
  afterEach(() => TestBed.resetTestingModule());

  it('does not send an invalid form', () => {
    const component = TestBed.createComponent(EmployeeFormDialogComponent).componentInstance;
    component.save();
    expect(api.create).not.toHaveBeenCalled();
    expect(component.form.controls.employeeNumber.touched).toBe(true);
  });

  it('prevents duplicate requests while saving', () => {
    const component = TestBed.createComponent(EmployeeFormDialogComponent).componentInstance;
    component.form.setValue({ employeeNumber: 'EMP-001', firstName: 'Ari', lastName: 'Example', workEmail: 'ari@example.test', hireDate: '2024-01-01', countryCode: 'IN', departmentId: 'department-1', jobTitle: 'Engineer' });
    api.create.mockReturnValue(new Subject());
    component.save();
    component.save();
    expect(api.create).toHaveBeenCalledTimes(1);
    expect(component.saving).toBe(true);
  });
});
