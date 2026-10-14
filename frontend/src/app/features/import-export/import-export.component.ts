import { StatusBadgeComponent } from '../../shared/status-badge.component';
import { downloadFile } from '../../shared/download-file';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
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
import { FileUploadBoxComponent } from '../../shared/file-upload-box.component';
import { ErrorStateComponent } from '../../shared/error-state.component';

@Component({ selector: 'app-import-export', standalone: true, imports: [StatusBadgeComponent, CommonModule, FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, FileUploadBoxComponent, ErrorStateComponent], templateUrl: './import-export.component.html', styleUrl: './import-export.component.css', changeDetection: ChangeDetectionStrategy.OnPush })
export class ImportExportComponent {
  private readonly api = inject(ImportExportApiService);
  private readonly references = inject(EmployeeApiService);
  private readonly notice = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);
  file: File | null = null; preview: ImportPreview | null = null; error = ''; busy = false;
  countries: ReferenceCountry[] = []; departments: ReferenceDepartment[] = [];
  filters = { q: '', country: '', department: '', jobTitle: '' };
  constructor() { this.references.countries().subscribe(response => { this.countries = response.data; this.cdr.markForCheck(); }); this.references.departments().subscribe(response => { this.departments = response.data; this.cdr.markForCheck(); }); }
  choose(files: FileList | null): void { this.file = files?.item(0) ?? null; this.preview = null; this.error = ''; }
  chooseFile(file: File | null): void { this.file = file; this.preview = null; this.error = ''; }
  downloadTemplate(): void { this.api.template().subscribe({ next: blob => downloadFile(blob, 'salary-management-template.xlsx'), error: () => { this.error = 'Could not download the template.'; this.cdr.markForCheck(); } }); }
  inspect(): void { if (!this.file) { this.error = 'Choose an .xlsx file first.'; this.cdr.markForCheck(); return; } this.busy = true; this.error = ''; this.cdr.markForCheck(); this.api.preview(this.file).pipe(finalize(() => { this.busy = false; this.cdr.markForCheck(); })).subscribe({ next: r => { this.preview = r.data; this.cdr.markForCheck(); }, error: e => { this.error = e?.error?.message ?? 'Could not read this workbook.'; this.cdr.markForCheck(); } }); }
  confirm(): void { if (!this.file || !this.preview || this.preview.invalidRows || this.preview.fileErrors.length) return; this.busy = true; this.error = ''; this.cdr.markForCheck(); this.api.confirm(this.file).pipe(finalize(() => { this.busy = false; this.cdr.markForCheck(); })).subscribe({ next: r => { this.notice.show(r.data.message); this.preview = null; this.file = null; this.cdr.markForCheck(); }, error: e => { this.preview = e?.error?.data ?? this.preview; this.error = e?.error?.message ?? 'Import failed. No rows were created.'; this.cdr.markForCheck(); } }); }
  export(): void { this.busy = true; this.cdr.markForCheck(); this.api.export(this.filters).pipe(finalize(() => { this.busy = false; this.cdr.markForCheck(); })).subscribe({ next: blob => { downloadFile(blob, 'salary-management-employees.xlsx'); this.notice.show('Filtered employee export downloaded.'); }, error: () => { this.error = 'Could not export this view.'; this.cdr.markForCheck(); } }); }
}
