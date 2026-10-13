import { CommonModule } from '@angular/common';
import { Component,OnInit,inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { EmployeeApiService } from '../employees/employee-api.service';
import { ReferenceCountry,ReferenceDepartment } from '../employees/employee.models';
import { InsightsApiService } from './insights-api.service';
import { InsightDimension,PayInsights,PaySummaryRow,SalaryOutlier } from './insights.models';
import { EmptyStateComponent } from '../../shared/empty-state.component';

@Component({selector:'app-insights',standalone:true,imports:[CommonModule,FormsModule,MatButtonModule,MatFormFieldModule,MatSelectModule,MatInputModule,EmptyStateComponent],templateUrl:'./insights.component.html',styleUrl:'./insights.component.css'})
export class InsightsComponent implements OnInit{
 private readonly api=inject(InsightsApiService);private readonly references=inject(EmployeeApiService);
 readonly dimensions: {id:InsightDimension;label:string}[]=[{id:'country',label:'Country'},{id:'department',label:'Department'},{id:'jobTitle',label:'Job title'}];
 groupBy:InsightDimension='country';country='';department='';jobTitle='';countries:ReferenceCountry[]=[];departments:ReferenceDepartment[]=[];
 result:PayInsights|null=null;outliers:SalaryOutlier[]=[];loading=false;error='';
 ngOnInit():void{this.references.countries().subscribe(x=>this.countries=x);this.references.departments().subscribe(x=>this.departments=x);this.load();}
 load():void{this.loading=true;this.error='';const filters={country:this.country,department:this.department,jobTitle:this.jobTitle};
  this.api.pay({groupBy:this.groupBy,...filters}).pipe(finalize(()=>this.loading=false)).subscribe({next:r=>this.result=r.data,error:()=>this.error='Could not load pay insights.'});
  this.api.outliers(filters).subscribe({next:r=>this.outliers=r.data,error:()=>this.error='Could not load pay outliers.'});
 }
 clear():void{this.country='';this.department='';this.jobTitle='';this.groupBy='country';this.load();}
 testId(row: PaySummaryRow):string{return `insight-row-${row.currencyCode}-${row.groupName.toLowerCase().replace(/[^a-z0-9]+/g,'-').replace(/^-|-$/g,'')}`;}
}
