import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { EmployeeApiService } from './employee-api.service';
import { Employee, EmployeeRequest, ReferenceCountry, ReferenceDepartment } from './employee.models';
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
 imports: [CommonModule, RouterLink, FormsModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatTableModule, EmptyStateComponent, FilterBarComponent, ServerPagedTableComponent, LoadingStateComponent, ErrorStateComponent],
 templateUrl: './employees.component.html', styleUrl: './employees.component.css', changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmployeesComponent implements OnInit {
 private readonly api=inject(EmployeeApiService); private readonly fb=inject(FormBuilder); private readonly notice=inject(NotificationService); private readonly confirmDialog=inject(ConfirmDialogService);
 private readonly cdr=inject(ChangeDetectorRef);
 readonly columns=['employeeNumber','name','workEmail','hireDate','country','department','jobTitle','actions'];
 readonly form=this.fb.group({employeeNumber:['',[Validators.required,Validators.pattern(/^[A-Za-z0-9-]{2,24}$/)]],firstName:['',[Validators.required,Validators.maxLength(80)]],lastName:['',[Validators.required,Validators.maxLength(80)]],workEmail:['',[Validators.required,Validators.email,Validators.maxLength(254)]],hireDate:['',[Validators.required]],countryCode:['',[Validators.required]],departmentId:['',[Validators.required]],jobTitle:['',[Validators.required,Validators.maxLength(100)]]});
 rows:Employee[]=[]; countries:ReferenceCountry[]=[]; departments:ReferenceDepartment[]=[]; total=0; page=0; size=25; loading=false; error=''; editing:Employee|null=null; formOpen=false;
 filters={q:'',country:'',department:'',jobTitle:''};
 ngOnInit():void{this.api.countries().subscribe(x=>{this.countries=x.data;this.cdr.markForCheck();});this.api.departments().subscribe(x=>{this.departments=x.data;this.cdr.markForCheck();});this.load();}
 load():void{this.loading=true;this.error='';this.cdr.markForCheck();this.api.list({...this.filters,page:this.page,size:this.size}).pipe(finalize(()=>{this.loading=false;this.cdr.markForCheck();})).subscribe({next:r=>{this.rows=r.data.items;this.total=r.data.totalItems;this.cdr.markForCheck();},error:()=>{this.error='Could not load employees. Check the API connection and try again.';this.cdr.markForCheck();}});}
 applyFilters():void{this.page=0;this.load();} pageChange(e:PageEvent):void{this.page=e.pageIndex;this.size=e.pageSize;this.load();}
 clearFilters():void{this.filters={q:'',country:'',department:'',jobTitle:''};this.applyFilters();}
 openCreate():void{this.editing=null;this.form.reset({employeeNumber:'',firstName:'',lastName:'',workEmail:'',hireDate:'',countryCode:'',departmentId:'',jobTitle:''});this.form.controls.employeeNumber.enable();this.formOpen=true;}
 openEdit(row:Employee):void{this.editing=row;this.form.reset({employeeNumber:row.employeeNumber,firstName:row.firstName,lastName:row.lastName,workEmail:row.workEmail,hireDate:row.hireDate,countryCode:row.countryCode,departmentId:row.departmentId,jobTitle:row.jobTitle});this.form.controls.employeeNumber.disable();this.formOpen=true;}
 closeForm():void{this.formOpen=false;this.editing=null;}
 save():void{if(this.form.invalid){this.form.markAllAsTouched();return;}const value=this.form.getRawValue() as EmployeeRequest;const action=this.editing?this.api.update(this.editing.id,value):this.api.create(value);action.subscribe({next:r=>{this.notice.show(r.message??'Employee saved.');this.closeForm();this.load();this.cdr.markForCheck();},error:e=>{this.error=e?.error?.message??'Could not save employee.';this.cdr.markForCheck();}});}
 async archive(row:Employee):Promise<void>{const confirmed=await firstValueFrom(this.confirmDialog.confirm({title:'Archive employee',message:`Archive ${row.firstName} ${row.lastName}? This removes the employee from active lists and insights.`,confirmLabel:'Archive employee'}));if(!confirmed)return;this.api.archive(row.id).subscribe({next:()=>{this.notice.show('Employee archived.');this.load();},error:()=>{this.error='Could not archive employee.';this.cdr.markForCheck();}});}
}
