import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'currencyAmount', standalone: true, pure: true })
export class CurrencyAmountPipe implements PipeTransform {
  transform(amount: number | string | null | undefined, currency: string): string {
    if (amount === null || amount === undefined || !currency) return '—';
    const value = String(amount).trim();
    const match = /^(-?)(\d+)(?:\.(\d{1,2}))?$/.exec(value);
    if (!match) return `${currency} ${value}`;
    const whole = match[2].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    const fraction = (match[3] ?? '').padEnd(2, '0');
    return `${currency} ${match[1]}${whole}.${fraction}`;
  }
}
