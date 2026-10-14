import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { firstValueFrom } from 'rxjs';
import { EmployeeApiService } from '../employees/employee-api.service';
import { Employee } from '../employees/employee.models';
import { SalaryApiService } from '../employees/salary-api.service';
import { SalaryVersion } from '../employees/salary.models';
import { StatusBadgeComponent } from '../../shared/status-badge.component';
import { ConfirmDialogService } from '../../shared/confirm-dialog.service';
import { SalaryWorkspaceApiService, SalaryTemplate, AssignmentRequest, AssignmentPreview } from './salary-workspace-api.service';

@Component({selector:'app-salary-assignments',standalone:true,imports:[CommonModule,FormsModule,RouterLink,MatButtonModule,MatFormFieldModule,MatInputModule,MatSelectModule,StatusBadgeComponent],templateUrl:'./salary-assignments.component.html',styleUrl:'./salary-workspace.css',changeDetection:ChangeDetectionStrategy.OnPush})
export class SalaryAssignmentsComponent {
 private readonly employees=inject(EmployeeApiService);
 private readonly salary=inject(SalaryApiService);
 private readonly api=inject(SalaryWorkspaceApiService);
 private readonly confirm=inject(ConfirmDialogService);
 private readonly cdr=inject(ChangeDetectorRef);
 q=''; matches:Employee[]=[]; searched=false; employee:Employee|null=null;
 templates:SalaryTemplate[]=[]; templateId=''; annualCtc=''; effectiveDate=''; previous:SalaryVersion|null=null;
 preview:AssignmentPreview|null=null; private previewRequest:AssignmentRequest|null=null;
 error=''; busy=false; success=false; searching=false;
 constructor(){const id=inject(ActivatedRoute).snapshot.queryParamMap.get('employee');if(id)this.employees.get(id).subscribe({next:r=>this.select(r.data),error:()=>{this.error='Employee could not be loaded. Search again.';this.cdr.markForCheck();}});}
 search():void {
   if(this.q.trim().length<2){this.error='Enter at least two characters to find an employee.';return;}
   this.searching=true;this.error='';
   this.employees.list({q:this.q.trim(),page:0,size:10}).subscribe({next:r=>{this.matches=r.data.items;this.searched=true;this.searching=false;this.cdr.markForCheck();},error:()=>{this.error='Search failed. Try again.';this.searching=false;this.cdr.markForCheck();}});
 }
 select(employee:Employee):void {
   this.employee=employee;this.matches=[];this.preview=null;this.previewRequest=null;this.success=false;this.error='';this.templateId='';this.previous=null;this.templates=[];this.annualCtc='';this.effectiveDate='';
   this.api.list({country:employee.countryCode,status:'ACTIVE',page:0,size:100}).subscribe({next:r=>{this.templates=r.data.items;this.cdr.markForCheck();},error:()=>{this.error='Could not load compatible templates.';this.cdr.markForCheck();}});
   this.salary.history(employee.id).subscribe({next:r=>{this.previous=r.data[0]||null;this.cdr.markForCheck();},error:()=>{this.error='Could not load previous salary. Preview will validate it again.';this.cdr.markForCheck();}});
 }
 invalidate():void{this.preview=null;this.previewRequest=null;this.success=false;}
 review():void {
   if(!this.employee||!this.templateId||!this.annualCtc||!this.effectiveDate){this.error='Choose a template, annual CTC and effective date.';return;}
   const template=this.templates.find(row=>row.id===this.templateId);
   if(!template)return;
   const request:AssignmentRequest={employeeId:this.employee.id,templateId:template.id,templateRevision:template.revision,annualCtc:this.annualCtc,effectiveDate:this.effectiveDate};
   this.busy=true;this.error='';this.preview=null;
   this.api.preview(request).subscribe({next:r=>{this.preview=r.data;this.previewRequest=request;this.busy=false;this.cdr.markForCheck();},error:r=>{this.error=r.error?.message||'Could not calculate this assignment.';this.busy=false;this.cdr.markForCheck();}});
 }
 async assign():Promise<void>{
   if(!this.previewRequest||!this.preview||this.busy)return;
   this.busy=true;
   const agreed=await firstValueFrom(this.confirm.confirm({title:'Confirm salary assignment',message:`Assign ${this.preview.template.name} to ${this.preview.employee.firstName} ${this.preview.employee.lastName} from ${this.effectiveDate}? A new salary snapshot will be created.`,confirmLabel:'Confirm assignment'}));
   if(!agreed){this.busy=false;this.cdr.markForCheck();return;}
   this.api.assign(this.previewRequest).subscribe({next:()=>{this.success=true;this.busy=false;this.cdr.markForCheck();},error:r=>{this.error=r.error?.message||'Assignment failed. No salary was changed.';this.busy=false;this.invalidate();this.cdr.markForCheck();}});
 }
 startAgain():void{this.employee=null;this.success=false;this.preview=null;this.previewRequest=null;this.q='';this.searched=false;this.error='';}
}
