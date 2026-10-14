import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppIconComponent } from '../app-icon.component';
import { FieldControl } from './field-control';

@Component({
  selector: 'app-text-field',
  standalone: true,
  imports: [CommonModule, AppIconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="form-control" [class.field-invalid]="error">
      <label [for]="id">{{ label }} <span *ngIf="required" aria-hidden="true" class="required-mark">*</span></label>
      <div [class.input-with-icon]="icon">
        <app-icon *ngIf="icon" [name]="icon" />
        <input [id]="id" [type]="type" [value]="value ?? ''" [disabled]="disabled"
          [placeholder]="placeholder" [attr.inputmode]="inputMode || null"
          [attr.min]="min" [attr.step]="step" [attr.maxlength]="maxLength"
          [attr.aria-required]="required" [attr.aria-invalid]="!!error"
          [attr.aria-describedby]="descriptionId" [attr.data-testid]="testId || id"
          (input)="inputChanged($event)" (blur)="touch()">
      </div>
      <span *ngIf="error" [id]="id+'-error'" class="field-error" role="alert">{{ error }}</span>
      <span *ngIf="hint && !error" [id]="id+'-hint'" class="field-hint">{{ hint }}</span>
    </div>`,
  styles: [':host{display:block;min-width:0}']
})
export class TextFieldComponent extends FieldControl<string | number> {
  @Input() type = 'text';
  @Input() placeholder = '';
  @Input() icon = '';
  @Input() inputMode = '';
  @Input() min: number | null = null;
  @Input() step: string | null = null;
  @Input() maxLength: number | null = null;

  inputChanged(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.update(this.type === 'number' ? (input.value === '' ? null : input.valueAsNumber) : input.value);
  }
}
