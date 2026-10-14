import { downloadFile } from '../../shared/download-file';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EmployeeFormDialogComponent } from './employee-form-dialog.component';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule, Sort } from '@angular/material/sort';
import { PageEvent } from '@angular/material/paginator';
import { EmployeeApiService } from './employee-api.service';
import { Employee, ReferenceCountry, ReferenceDepartment } from './employee.models';
import { EmptyStateComponent } from '../../shared/empty-state.component';
import { NotificationService } from '../../core/notification.service';
import { ConfirmDialogService } from '../../shared/confirm-dialog.service';
import { FilterBarComponent } from '../../shared/filter-bar.component';
import { ServerPagedTableComponent } from '../../shared/server-paged-table.component';
import { LoadingStateComponent } from '../../shared/loading-state.component';
import { ErrorStateComponent } from '../../shared/error-state.component';
import { firstValueFrom } from 'rxjs';

@Component({
 selector: 'app-employees', standalone: true,
 imports: [CommonModule, RouterLink, FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatTableModule, MatSortModule, EmptyStateComponent, FilterBarComponent, ServerPagedTableComponent, LoadingStateComponent, ErrorStateComponent],
 templateUrl: './employees.component.html', styleUrl: './employees.component.css', changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmployeesComponent implements OnInit {
 private readonly api=inject(EmployeeApiService); private readonly dialog=inject(MatDialog); readonly notice=inject(NotificationService); private readonly confirmDialog=inject(ConfirmDialogService);
 private readonly cdr=inject(ChangeDetectorRef);
 readonly columns=['employeeNumber','name','workEmail','hireDate','country','department','jobTitle','actions'];
 rows:Employee[]=[]; countries:ReferenceCountry[]=[]; departments:ReferenceDepartment[]=[]; total=0; page=0; size=25; loading=false; error='';
 sortBy = 'employeeNumber';
 direction: 'asc' | 'desc' = 'asc';
 sortChange(sort: Sort): void { this.sortBy = sort.active; this.direction = sort.direction === 'desc' ? 'desc' : 'asc'; this.page = 0; this.load(); }
 filters={q:'',country:'',department:'',jobTitle:''};
 ngOnInit():void{this.api.countries().subscribe(x=>{this.countries=x.data;this.cdr.markForCheck();});this.api.departments().subscribe(x=>{this.departments=x.data;this.cdr.markForCheck();});this.load();}
 load():void{this.loading=true;this.error='';this.cdr.markForCheck();this.api.list({...this.filters,page:this.page,size:this.size,sortBy:this.sortBy,direction:this.direction}).pipe(finalize(()=>{this.loading=false;this.cdr.markForCheck();})).subscribe({next:r=>{this.rows=r.data.items;this.total=r.data.totalItems;this.cdr.markForCheck();},error:()=>{this.error='Could not load employees. Check the API connection and try again.';this.cdr.markForCheck();}});}
 applyFilters():void{this.page=0;this.load();} pageChange(e:PageEvent):void{this.page=e.pageIndex;this.size=e.pageSize;this.load();}
 clearFilters():void{this.filters={q:'',country:'',department:'',jobTitle:''};this.applyFilters();}
 exporting = false;
 exportView(): void {
   this.exporting = true;
   this.api.export({ ...this.filters, sortBy: this.sortBy, direction: this.direction })
     .pipe(finalize(() => { this.exporting = false; this.cdr.markForCheck(); }))
     .subscribe({
       next: blob => { downloadFile(blob, 'salary-management-employees.xlsx'); this.notice.show('Filtered export downloaded.'); },
       error: () => { this.error = 'Could not export employees. Please try again.'; this.cdr.markForCheck(); }
     });
 }
 openCreate(): void { this.openEditor(null); }
 openEdit(employee: Employee): void { this.openEditor(employee); }
 private openEditor(employee: Employee | null): void {
   this.dialog.open(EmployeeFormDialogComponent, {
     width: '760px', maxWidth: 'calc(100vw - 32px)', disableClose: true,
     ariaLabel: employee ? 'Edit employee' : 'Add employee',
     data: { employee, countries: this.countries, departments: this.departments }
   }).afterClosed().subscribe((message: string | undefined) => {
     if (message) { this.notice.show(message); this.load(); }
   });
 }
 async archive(row:Employee):Promise<void>{const confirmed=await firstValueFrom(this.confirmDialog.confirm({title:'Archive employee',message:`Archive ${row.firstName} ${row.lastName}? This removes the employee from active lists and insights.`,confirmLabel:'Archive employee'}));if(!confirmed)return;this.api.archive(row.id).subscribe({next:()=>{this.notice.show('Employee archived.');this.load();},error:()=>{this.error='Could not archive employee.';this.cdr.markForCheck();}});}
}
