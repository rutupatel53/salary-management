import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { finalize, firstValueFrom } from 'rxjs';
import { DialogShellComponent } from '../../shared/dialog-shell.component';
import { ConfirmDialogService } from '../../shared/confirm-dialog.service';
import { EmployeeApiService } from './employee-api.service';
import { Employee, ReferenceCountry, ReferenceDepartment } from './employee.models';

export interface EmployeeFormDialogData {
  employee: Employee | null;
  countries: ReferenceCountry[];
  departments: ReferenceDepartment[];
}

@Component({
  selector: 'app-employee-form-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, DialogShellComponent],
  templateUrl: './employee-form-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [`
    .fields { display: grid; grid-template-columns: 1fr 1fr; gap: 8px 18px; }
    .section-title { grid-column: 1 / -1; margin: 0 0 14px; font-size: 14px; color: #344157; }
    .error { border-radius: 8px; padding: 12px; background: #fff0ee; color: #a72e25; margin-bottom: 16px; }
    @media(max-width: 600px) { .fields { grid-template-columns: 1fr; } }
  `]
})
export class EmployeeFormDialogComponent {
  readonly data = inject<EmployeeFormDialogData>(MAT_DIALOG_DATA);
  private readonly dialog = inject(MatDialogRef<EmployeeFormDialogComponent>);
  private readonly api = inject(EmployeeApiService);
  private readonly confirm = inject(ConfirmDialogService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  readonly form = this.fb.nonNullable.group({
    employeeNumber: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9-]{2,24}$/)]],
    firstName: ['', [Validators.required, Validators.maxLength(80)]],
    lastName: ['', [Validators.required, Validators.maxLength(80)]],
    workEmail: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    hireDate: ['', Validators.required],
    countryCode: ['', Validators.required],
    departmentId: ['', Validators.required],
    jobTitle: ['', [Validators.required, Validators.maxLength(100)]]
  });
  saving = false;
  error = '';
  confirming = false;

  constructor() {
    if (this.data.employee) {
      this.form.patchValue(this.data.employee);
      this.form.controls.employeeNumber.disable();
    }
    this.dialog.backdropClick().pipe(takeUntilDestroyed()).subscribe(() => void this.cancel());
    this.dialog.keydownEvents().pipe(takeUntilDestroyed()).subscribe(event => {
      if (event.key === 'Escape') { event.preventDefault(); void this.cancel(); }
    });
  }

  async cancel(): Promise<void> {
    if (this.saving || this.confirming) return;
    if (this.form.dirty) {
      this.confirming = true;
      const discard = await firstValueFrom(this.confirm.confirm({
        title: 'Discard changes?', message: 'Your unsaved employee changes will be lost.', confirmLabel: 'Discard changes'
      }));
      this.confirming = false;
      this.cdr.markForCheck();
      if (!discard) return;
    }
    this.dialog.close();
  }

  save(): void {
    if (this.saving) return;
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    this.error = '';
    const request = this.form.getRawValue();
    const action = this.data.employee ? this.api.update(this.data.employee.id, request) : this.api.create(request);
    action.pipe(takeUntilDestroyed(this.destroyRef), finalize(() => {
      this.saving = false;
      this.cdr.markForCheck();
    })).subscribe({
      next: response => this.dialog.close(response.message || 'Employee saved.'),
      error: response => {
        this.error = response.error?.message || 'Could not save employee. Try again.';
        const errors: Record<string, string> = response.error?.errors || {};
        for (const [field, message] of Object.entries(errors)) {
          this.form.get(field)?.setErrors({ server: message });
          this.form.get(field)?.markAsTouched();
        }
        this.cdr.markForCheck();
      }
    });
  }
}
