import { ChangeDetectorRef, DestroyRef, Directive, Input, OnInit, inject } from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

let nextFieldId = 0;

/** Shared Angular Forms behavior for both reusable field components. */
@Directive()
export abstract class FieldControl<T> implements ControlValueAccessor, OnInit {
  @Input() label = '';
  @Input() controlId = '';
  @Input() testId = '';
  @Input() hint = '';
  @Input() errorMessage = 'Enter a valid value.';
  @Input() required = false;
  @Input() disabled = false;
  value: T | null = null;
  private readonly fallbackId = `app-field-${nextFieldId++}`;
  private readonly changeDetector = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);
  protected readonly ngControl = inject(NgControl, { self: true, optional: true });
  private onChange: (value: T | null) => void = () => {};
  private onTouched: () => void = () => {};

  constructor() {
    if (this.ngControl) this.ngControl.valueAccessor = this;
  }

  get id(): string { return this.controlId || this.testId || this.fallbackId; }
  get error(): string {
    const control = this.ngControl?.control;
    if (!control?.invalid || !(control.touched || control.dirty)) return '';
    return control.getError('server') || this.errorMessage;
  }
  get descriptionId(): string | null {
    return this.error ? `${this.id}-error` : this.hint ? `${this.id}-hint` : null;
  }

  ngOnInit(): void {
    this.ngControl?.control?.events.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.changeDetector.markForCheck());
  }

  writeValue(value: T | null): void {
    this.value = value;
    this.changeDetector.markForCheck();
  }
  registerOnChange(fn: (value: T | null) => void): void { this.onChange = fn; }
  registerOnTouched(fn: () => void): void { this.onTouched = fn; }
  setDisabledState(disabled: boolean): void {
    this.disabled = disabled;
    this.changeDetector.markForCheck();
  }
  update(value: T | null): void { this.value = value; this.onChange(value); }
  touch(): void { this.onTouched(); }
}
