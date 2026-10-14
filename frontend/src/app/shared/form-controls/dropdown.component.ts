import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { FieldControl } from './field-control';

export interface DropdownOption { value: string; label: string; disabled?: boolean; }

@Component({
  selector: 'app-dropdown',
  standalone: true,
  imports: [CommonModule, MatSelectModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="form-control" [class.field-invalid]="error">
      <label [id]="id+'-label'" (click)="select.focus()">{{ label }} <span *ngIf="required" aria-hidden="true" class="required-mark">*</span></label>
      <mat-select #select [id]="id" class="dropdown-control" panelClass="app-dropdown-panel"
        [value]="value" [disabled]="disabled" [placeholder]="placeholder"
        [aria-labelledby]="id+'-label'" [aria-describedby]="descriptionId || ''"
        [attr.aria-invalid]="!!error" [required]="required" [attr.data-testid]="testId || id"
        (selectionChange)="update($event.value)" (closed)="touch()" (blur)="touch()">
        <mat-option *ngIf="emptyLabel" value="">{{ emptyLabel }}</mat-option>
        <mat-option *ngFor="let option of options" [value]="option.value" [disabled]="option.disabled">{{ option.label }}</mat-option>
      </mat-select>
      <span *ngIf="error" [id]="id+'-error'" class="field-error" role="alert">{{ error }}</span>
      <span *ngIf="hint && !error" [id]="id+'-hint'" class="field-hint">{{ hint }}</span>
    </div>`,
  styles: [':host{display:block;min-width:0}']
})
export class DropdownComponent extends FieldControl<string> {
  @Input() options: readonly DropdownOption[] = [];
  @Input() emptyLabel = '';
  @Input() placeholder = 'Choose an option';
}
