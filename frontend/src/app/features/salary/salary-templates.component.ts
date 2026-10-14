import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PageEvent } from '@angular/material/paginator';
import { EmployeeApiService } from '../employees/employee-api.service';
import { ReferenceCountry } from '../employees/employee.models';
import { StatusBadgeComponent } from '../../shared/status-badge.component';
import { ServerPagedTableComponent } from '../../shared/server-paged-table.component';
import { SalaryTemplate, SalaryWorkspaceApiService } from './salary-workspace-api.service';
import { TemplateEditorComponent } from './template-editor.component';
import { AppIconComponent } from '../../shared/app-icon.component';
import { PageGuideComponent } from '../../shared/page-guide.component';

@Component({selector:'app-salary-templates',standalone:true,imports:[CommonModule,FormsModule,RouterLink,MatButtonModule,MatTooltipModule,StatusBadgeComponent,ServerPagedTableComponent,AppIconComponent,PageGuideComponent],templateUrl:'./salary-templates.component.html',styleUrl:'./salary-workspace.css',changeDetection:ChangeDetectionStrategy.OnPush})
export class SalaryTemplatesComponent {
 private readonly api=inject(SalaryWorkspaceApiService);
 private readonly references=inject(EmployeeApiService);
 private readonly dialog=inject(MatDialog);
 private readonly cdr=inject(ChangeDetectorRef);
 countries:ReferenceCountry[]=[];
 rows:SalaryTemplate[]=[];
 q=''; status=''; page=0; total=0; error=''; loading=false;
 constructor(){this.references.countries().subscribe(r=>{this.countries=r.data;this.cdr.markForCheck();});this.load();}
 load():void {this.loading=true;this.error='';this.api.list({q:this.q,status:this.status,page:this.page,size:25}).subscribe({next:r=>{this.rows=r.data.items;this.total=r.data.totalItems;this.loading=false;this.cdr.markForCheck();},error:()=>{this.error='Could not load templates. Try again.';this.loading=false;this.cdr.markForCheck();}});}
 search():void {this.page=0;this.load();}
 pageChange(event:PageEvent):void {this.page=event.pageIndex;this.load();}
 edit(template:SalaryTemplate|null=null,copy=false):void {this.dialog.open(TemplateEditorComponent,{width:'1120px',maxWidth:'calc(100vw - 24px)',disableClose:true,data:{template,countries:this.countries,copy}}).afterClosed().subscribe(saved=>{if(saved)this.load();});}
}
