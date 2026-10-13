import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { fromMinorUnits, moneyInputValidator, toMinorUnits } from '../../shared/money';
import { EmployeeApiService } from './employee-api.service';
import { SalaryApiService } from './salary-api.service';
import { Employee } from './employee.models';
import { SalaryVersion } from './salary.models';
import { NotificationService } from '../../core/notification.service';

@Component({selector:'app-employee-detail',standalone:true,imports:[CommonModule,RouterLink,FormsModule,ReactiveFormsModule,MatButtonModule,MatFormFieldModule,MatInputModule],templateUrl:'./employee-detail.component.html',styleUrl:'./employee-detail.component.css'})
export class EmployeeDetailComponent implements OnInit{
 private readonly route=inject(ActivatedRoute);private readonly api=inject(EmployeeApiService);private readonly salaryApi=inject(SalaryApiService);private readonly fb=inject(FormBuilder);private readonly notice=inject(NotificationService);
 employee:Employee|null=null;history:SalaryVersion[]=[];error='';saving=false;
 readonly form=this.fb.group({basic:['',[Validators.required,(c: {value:unknown})=>moneyInputValidator(c.value)?null:{money:true}]],allowances:['0.00',[Validators.required,(c: {value:unknown})=>moneyInputValidator(c.value)?null:{money:true}]],deductions:['0.00',[Validators.required,(c: {value:unknown})=>moneyInputValidator(c.value)?null:{money:true}]],effectiveDate:['',[Validators.required]]});
 ngOnInit():void{const id=this.route.snapshot.paramMap.get('id');if(!id){this.error='Employee not found.';return;}this.load(id);}
 load(id:string):void{this.api.get(id).subscribe({next:r=>this.employee=r.data,error:()=>this.error='Could not load employee details.'});this.salaryApi.history(id).subscribe({next:r=>this.history=r.data,error:()=>this.error='Could not load salary history.'});}
 get grossPreview():string{try{return fromMinorUnits(toMinorUnits(this.form.controls.basic.value||'0')+toMinorUnits(this.form.controls.allowances.value||'0'));}catch{return '0.00';}}
 get netPreview():string{try{return fromMinorUnits(toMinorUnits(this.grossPreview)-toMinorUnits(this.form.controls.deductions.value||'0'));}catch{return '0.00';}}
 get netNegative():boolean{return this.netPreview.startsWith('-');}
 saveSalary():void{if(!this.employee||this.form.invalid)return;if(this.netNegative){this.error='Deductions cannot exceed gross pay.';return;}const raw=this.form.getRawValue();this.saving=true;this.salaryApi.append(this.employee.id,{basic:raw.basic!,allowances:raw.allowances!,deductions:raw.deductions!,currencyCode:this.employee.currencyCode,effectiveDate:raw.effectiveDate!}).subscribe({next:()=>{this.notice.show('Salary version saved.');this.form.reset({basic:'',allowances:'0.00',deductions:'0.00',effectiveDate:''});this.load(this.employee!.id);this.saving=false;},error:e=>{this.error=e?.error?.message??'Could not save salary version.';this.saving=false;}});}
}
