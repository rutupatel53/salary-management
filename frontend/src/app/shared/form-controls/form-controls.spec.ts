import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { TextFieldComponent } from './text-field.component';
import { DropdownComponent } from './dropdown.component';
import { DropdownOptionsPipe } from './dropdown-options.pipe';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, TextFieldComponent, DropdownComponent],
  template: `
    <app-text-field label="Name" testId="name" [formControl]="name" errorMessage="Name is required."></app-text-field>
    <app-text-field label="Amount" testId="amount" type="number" [formControl]="amount"></app-text-field>
    <app-dropdown label="Country" testId="country" [formControl]="country" [options]="options"></app-dropdown>`
})
class FormHost {
  name = new FormControl('', { nonNullable: true, validators: [Validators.required] });
  amount = new FormControl<number | null>(40);
  country = new FormControl('IN', { nonNullable: true });
  options = [{ value: 'IN', label: 'India' }, { value: 'US', label: 'United States' }];
}

describe('Shared form controls', () => {
  beforeEach(async () => {
    TestBed.configureTestingModule({ imports: [FormHost] });
    await TestBed.compileComponents();
  });
  afterEach(() => TestBed.resetTestingModule());

  it('updates the reactive form and marks a text input touched on blur', async () => {
    const fixture = TestBed.createComponent(FormHost);
    fixture.detectChanges();
    await fixture.whenStable();
    const input = fixture.nativeElement.querySelector('[data-testid="name"]') as HTMLInputElement;
    input.value = 'Ari';
    input.dispatchEvent(new Event('input'));
    input.dispatchEvent(new Event('blur'));
    expect(fixture.componentInstance.name.value).toBe('Ari');
    expect(fixture.componentInstance.name.touched).toBe(true);
  });

  it('keeps numeric values numeric and clears a blank amount to null', async () => {
    const fixture = TestBed.createComponent(FormHost);
    fixture.detectChanges();
    await fixture.whenStable();
    const input = fixture.nativeElement.querySelector('[data-testid="amount"]') as HTMLInputElement;
    input.value = '12.50';
    input.dispatchEvent(new Event('input'));
    expect(fixture.componentInstance.amount.value).toBe(12.5);
    input.value = '';
    input.dispatchEvent(new Event('input'));
    expect(fixture.componentInstance.amount.value).toBeNull();
  });

  it('shows touched validation errors and reflects model values and disabled states', async () => {
    const fixture = TestBed.createComponent(FormHost);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.componentInstance.name.markAsTouched();
    fixture.componentInstance.country.setValue('US');
    fixture.componentInstance.country.disable();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain('Name is required.');
    const dropdown = fixture.nativeElement.querySelector('[data-testid="country"]');
    expect(dropdown.textContent).toContain('United States');
    expect(dropdown.getAttribute('aria-disabled')).toBe('true');
    expect(fixture.nativeElement.querySelector('[data-testid="name"]').getAttribute('aria-describedby')).toBe('name-error');
  });

  it('maps reference options with stable values and currency labels', () => {
    expect(new DropdownOptionsPipe().transform([{ code: 'IN', name: 'India', currency: 'INR' }], 'code', 'name', 'currency'))
      .toEqual([{ value: 'IN', label: 'India · INR' }]);
  });
});
