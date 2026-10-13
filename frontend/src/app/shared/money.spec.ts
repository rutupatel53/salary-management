import { describe, expect, it } from 'vitest';
import { fromMinorUnits, moneyInputValidator, toMinorUnits } from './money';
describe('exact money helpers',()=>{
 it('calculates decimal components in integer cents',()=>expect(fromMinorUnits(toMinorUnits('100000000.25')+toMinorUnits('0.75'))).toBe('100000001.00'));
 it('rejects negative values and more than two decimal places',()=>{expect(moneyInputValidator('-1.00')).toBe(false);expect(moneyInputValidator('1.001')).toBe(false);});
});
