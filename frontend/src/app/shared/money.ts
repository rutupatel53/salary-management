const MONEY_PATTERN = /^(?:0|[1-9]\d{0,11})(?:\.\d{1,2})?$/;
export function moneyInputValidator(value: unknown): boolean {
  return typeof value === 'string' && MONEY_PATTERN.test(value.trim());
}
export function toMinorUnits(value: string): bigint {
  if (!moneyInputValidator(value)) throw new Error('Enter a non-negative amount with up to two decimal places.');
  const [whole, fraction = ''] = value.trim().split('.');
  return BigInt(whole) * 100n + BigInt((fraction + '00').slice(0, 2));
}
export function fromMinorUnits(value: bigint): string {
  const negative = value < 0n;
  const absolute = negative ? -value : value;
  const whole = absolute / 100n;
  const cents = (absolute % 100n).toString().padStart(2, '0');
  return `${negative ? '-' : ''}${whole}.${cents}`;
}
