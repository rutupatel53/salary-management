import { describe, expect, it } from 'vitest';
import { CurrencyAmountPipe } from './currency-amount.pipe';

describe('CurrencyAmountPipe', () => {
  const pipe = new CurrencyAmountPipe();

  it('groups exact decimal text and keeps the ISO currency code visible', () => {
    expect(pipe.transform('100000001.5', 'INR')).toBe('INR 100,000,001.50');
  });

  it('does not convert the amount through a floating point calculation', () => {
    expect(pipe.transform('999999999999.99', 'USD')).toBe('USD 999,999,999,999.99');
  });

  it('uses an em dash for missing amounts', () => {
    expect(pipe.transform(null, 'CAD')).toBe('—');
  });
});
