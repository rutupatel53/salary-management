import { Injectable, inject } from '@angular/core';
import { ApiBaseService } from '../../core/api-base.service';

export interface ImportRow { rowNumber: number; employeeNumber: string; valid: boolean; errors: string[]; }
export interface ImportPreview { totalRows: number; validRows: number; invalidRows: number; fileErrors: string[]; rows: ImportRow[]; }
export interface ApiResponse<T> { data: T; message: string | null; }

@Injectable({ providedIn: 'root' })
export class ImportExportApiService {
  private readonly api = inject(ApiBaseService);
  template() { return this.api.downloadFile('/imports/template'); }
  preview(file: File) { return this.api.postFile<ApiResponse<ImportPreview>>('/imports/preview', file); }
  confirm(file: File) { return this.api.postFile<ApiResponse<{ createdRows: number; message: string }>>('/imports/confirm', file); }
  export(filters: { q?: string; country?: string; department?: string; jobTitle?: string }) { return this.api.download('/employees/export', filters); }
}
