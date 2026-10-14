import { Pipe, PipeTransform } from '@angular/core';
import { DropdownOption } from './dropdown.component';

/** Keep reference-list mapping in one pure pipe rather than in feature components. */
@Pipe({ name: 'dropdownOptions', standalone: true })
export class DropdownOptionsPipe implements PipeTransform {
  transform<T>(rows: readonly T[], valueKey: keyof T, labelKey: keyof T, suffixKey?: keyof T): DropdownOption[] {
    return rows.map(row => ({
      value: String(row[valueKey]),
      label: suffixKey ? `${row[labelKey]} · ${row[suffixKey]}` : String(row[labelKey])
    }));
  }
}
