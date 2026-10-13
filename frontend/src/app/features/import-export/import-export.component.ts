import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ImportExportApiService, ImportPreview } from './import-export-api.service';
import { NotificationService } from '../../core/notification.service';
import { EmployeeApiService } from '../employees/employee-api.service';
import { ReferenceCountry, ReferenceDepartment } from '../employees/employee.models';
import { MatSelectModule } from '@angular/material/select';

@Component({ selector: 'app-import-export', standalone: true, imports: [CommonModule, FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule], templateUrl: './import-export.component.html', styleUrl: './import-export.component.css' })
export class ImportExportComponent {
  private readonly api = inject(ImportExportApiService);
  private readonly references = inject(EmployeeApiService);
  private readonly notice = inject(NotificationService);
  file: File | null = null; preview: ImportPreview | null = null; error = ''; busy = false;
  countries: ReferenceCountry[] = []; departments: ReferenceDepartment[] = [];
  filters = { q: '', country: '', department: '', jobTitle: '' };
  constructor() { this.references.countries().subscribe(rows => this.countries = rows); this.references.departments().subscribe(rows => this.departments = rows); }
  choose(files: FileList | null): void { this.file = files?.item(0) ?? null; this.preview = null; this.error = ''; }
  downloadTemplate(): void { this.api.template().subscribe({ next: blob => this.save(blob, 'salary-management-template.xlsx'), error: () => this.error = 'Could not download the template.' }); }
  inspect(): void { if (!this.file) { this.error = 'Choose an .xlsx file first.'; return; } this.busy = true; this.error = ''; this.api.preview(this.file).pipe(finalize(() => this.busy = false)).subscribe({ next: r => this.preview = r.data, error: e => this.error = e?.error?.message ?? 'Could not read this workbook.' }); }
  confirm(): void { if (!this.file || !this.preview || this.preview.invalidRows || this.preview.fileErrors.length) return; this.busy = true; this.error = ''; this.api.confirm(this.file).pipe(finalize(() => this.busy = false)).subscribe({ next: r => { this.notice.show(r.data.message); this.preview = null; this.file = null; }, error: e => { this.preview = e?.error?.data ?? this.preview; this.error = e?.error?.message ?? 'Import failed. No rows were created.'; } }); }
  export(): void { this.busy = true; this.api.export(this.filters).pipe(finalize(() => this.busy = false)).subscribe({ next: blob => { this.save(blob, 'salary-management-employees.xlsx'); this.notice.show('Filtered employee export downloaded.'); }, error: () => this.error = 'Could not export this view.' }); }
  private save(blob: Blob, name: string): void { const url = URL.createObjectURL(blob); const link = document.createElement('a'); link.href = url; link.download = name; link.click(); URL.revokeObjectURL(url); }
}
