import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { EmployeeApiService } from './employee-api.service';
import { SalaryApiService } from './salary-api.service';
import { Employee } from './employee.models';
import { SalaryVersion } from './salary.models';
import { ErrorStateComponent } from '../../shared/error-state.component';

@Component({selector:'app-employee-detail',standalone:true,imports:[CommonModule,RouterLink,MatButtonModule,ErrorStateComponent],templateUrl:'./employee-detail.component.html',styleUrl:'./employee-detail.component.css',changeDetection:ChangeDetectionStrategy.OnPush})
export class EmployeeDetailComponent implements OnInit{
 private readonly route=inject(ActivatedRoute);private readonly api=inject(EmployeeApiService);private readonly salaryApi=inject(SalaryApiService);
 private readonly cdr=inject(ChangeDetectorRef);
 employee:Employee|null=null;history:SalaryVersion[]=[];error='';saving=false;
 ngOnInit():void{const id=this.route.snapshot.paramMap.get('id');if(!id){this.error='Employee not found.';return;}this.load(id);}
 load(id:string):void{this.api.get(id).subscribe({next:r=>{this.employee=r.data;this.cdr.markForCheck();},error:()=>{this.error='Could not load employee details.';this.cdr.markForCheck();}});this.salaryApi.history(id).subscribe({next:r=>{this.history=r.data;this.cdr.markForCheck();},error:()=>{this.error='Could not load salary history.';this.cdr.markForCheck();}});}
}
