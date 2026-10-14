import { TextFieldComponent } from '../../shared/form-controls/text-field.component';
import { DropdownComponent } from '../../shared/form-controls/dropdown.component';
import { DropdownOptionsPipe } from '../../shared/form-controls/dropdown-options.pipe';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { firstValueFrom } from 'rxjs';
import { DialogShellComponent } from '../../shared/dialog-shell.component';
import { ConfirmDialogService } from '../../shared/confirm-dialog.service';
import { ReferenceCountry } from '../employees/employee.models';
import { SalaryTemplate, SalaryWorkspaceApiService, TemplateComponent } from './salary-workspace-api.service';

@Component({
 selector: 'app-template-editor', standalone: true,
 imports: [TextFieldComponent, DropdownComponent, DropdownOptionsPipe, CommonModule, FormsModule, MatButtonModule, DialogShellComponent],
 templateUrl: './template-editor.component.html', styleUrl: './salary-workspace.css', changeDetection: ChangeDetectionStrategy.OnPush
})
export class TemplateEditorComponent {
 readonly data = inject<{ template: SalaryTemplate | null; countries: ReferenceCountry[]; copy: boolean }>(MAT_DIALOG_DATA);
 private readonly api = inject(SalaryWorkspaceApiService);
 private readonly ref = inject(MatDialogRef<TemplateEditorComponent>);
 private readonly confirm = inject(ConfirmDialogService);
 private readonly cdr = inject(ChangeDetectorRef);
 readonly categories = ['BASIC', 'ALLOWANCE', 'EMPLOYER_CONTRIBUTION', 'DEDUCTION'];
 readonly calculations = ['FIXED', 'PERCENT_CTC', 'PERCENT_BASIC', 'BALANCE'];
 readonly categoryOptions = this.categories.map(value => ({ value, label: this.label(value) }));
 readonly calculationOptions = this.calculations.map(value => ({ value, label: this.label(value) }));
 model: Partial<SalaryTemplate>;
 error = '';
 saving = false;
 dirty = false;
 constructor() {
   const template = this.data.template;
   this.model = template ? { ...template, id: this.data.copy ? undefined : template.id, name: this.data.copy ? `${template.name} copy` : template.name, status: this.data.copy ? 'DRAFT' : template.status, components: template.components.map(row => ({...row})) } : {
     name: '', countryCode: 'IN', status: 'DRAFT', components: [
       { name: 'Basic pay', category: 'BASIC', calculation: 'PERCENT_CTC', value: 40 },
       { name: 'Special allowance', category: 'ALLOWANCE', calculation: 'BALANCE', value: 0 }
     ]
   };
 }
 label(value: string): string { return ({ FIXED: 'Fixed annual amount', PERCENT_CTC: '% of CTC', PERCENT_BASIC: '% of basic', BALANCE: 'Remaining CTC', BASIC: 'Basic pay', ALLOWANCE: 'Allowance', EMPLOYER_CONTRIBUTION: 'Employer contribution', DEDUCTION: 'Employee deduction' } as Record<string,string>)[value] || value; }
 add(): void { this.model.components!.push({ name: '', category: 'ALLOWANCE', calculation: 'FIXED', value: 0 }); this.dirty = true; }
 remove(index: number): void { this.model.components!.splice(index, 1); this.dirty = true; }
 calculationChanged(component: TemplateComponent): void { if (component.calculation === 'BALANCE') component.value = 0; this.dirty = true; }
 async cancel(): Promise<void> {
   if (this.saving) return;
   if (this.dirty && !await firstValueFrom(this.confirm.confirm({title:'Discard template changes?',message:'Unsaved template changes will be lost.',confirmLabel:'Discard changes'}))) return;
   this.ref.close();
 }
 save(): void {
   if (this.saving) return;
   this.error = '';
   if (!this.model.name?.trim()) { this.error = 'Enter a template name.'; return; }
   this.saving = true;
   this.api.save(this.model).subscribe({
     next: () => this.ref.close(true),
     error: response => { this.error = response.error?.message || 'Could not save template.'; this.saving = false; this.cdr.markForCheck(); }
   });
 }
}
