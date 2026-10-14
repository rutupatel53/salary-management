import { TextFieldComponent } from '../../shared/form-controls/text-field.component';
import { DropdownComponent } from '../../shared/form-controls/dropdown.component';
import { DropdownOptionsPipe } from '../../shared/form-controls/dropdown-options.pipe';
import { StatusBadgeComponent } from '../../shared/status-badge.component';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component,OnInit,inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { EmployeeApiService } from '../employees/employee-api.service';
import { ReferenceCountry,ReferenceDepartment } from '../employees/employee.models';
import { InsightsApiService } from './insights-api.service';
import { InsightDimension,PayInsights,PaySummaryRow,SalaryOutlier } from './insights.models';
import { EmptyStateComponent } from '../../shared/empty-state.component';
import { FilterBarComponent } from '../../shared/filter-bar.component';
import { LoadingStateComponent } from '../../shared/loading-state.component';
import { ErrorStateComponent } from '../../shared/error-state.component';
import { PageGuideComponent } from '../../shared/page-guide.component';

@Component({selector:'app-insights',standalone:true,imports:[TextFieldComponent, DropdownComponent, DropdownOptionsPipe, StatusBadgeComponent,CommonModule,FormsModule,MatButtonModule,EmptyStateComponent,FilterBarComponent,LoadingStateComponent,ErrorStateComponent,PageGuideComponent],templateUrl:'./insights.component.html',styleUrl:'./insights.component.css',changeDetection:ChangeDetectionStrategy.OnPush})
export class InsightsComponent implements OnInit{
 private readonly api=inject(InsightsApiService);private readonly references=inject(EmployeeApiService);private readonly cdr=inject(ChangeDetectorRef);
 readonly dimensions: {id:InsightDimension;label:string}[]=[{id:'country',label:'Country'},{id:'department',label:'Department'},{id:'jobTitle',label:'Job title'}];
 groupBy:InsightDimension='country';country='';department='';jobTitle='';countries:ReferenceCountry[]=[];departments:ReferenceDepartment[]=[];
 result:PayInsights|null=null;outliers:SalaryOutlier[]=[];loading=false;error='';
 ngOnInit():void{this.references.countries().subscribe(x=>{this.countries=x.data;this.cdr.markForCheck();});this.references.departments().subscribe(x=>{this.departments=x.data;this.cdr.markForCheck();});this.load();}
 load():void{this.loading=true;this.error='';const filters={country:this.country,department:this.department,jobTitle:this.jobTitle};
  this.cdr.markForCheck();this.api.pay({groupBy:this.groupBy,...filters}).pipe(finalize(()=>{this.loading=false;this.cdr.markForCheck();})).subscribe({next:r=>{this.result=r.data;this.cdr.markForCheck();},error:()=>{this.error='Could not load pay insights.';this.cdr.markForCheck();}});
  this.api.outliers(filters).subscribe({next:r=>{this.outliers=r.data;this.cdr.markForCheck();},error:()=>{this.error='Could not load pay outliers.';this.cdr.markForCheck();}});
 }
 clear():void{this.country='';this.department='';this.jobTitle='';this.groupBy='country';this.load();}
 testId(row: PaySummaryRow):string{return `insight-row-${row.currencyCode}-${row.groupName.toLowerCase().replace(/[^a-z0-9]+/g,'-').replace(/^-|-$/g,'')}`;}
}
