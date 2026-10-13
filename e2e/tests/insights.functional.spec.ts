import { test, expect } from '@playwright/test';
import { InsightsPage } from '../pages/insights.page';
const apiURL=process.env.API_URL??'http://127.0.0.1:8080/api';
test.beforeEach(async({request})=>{const r=await request.post(`${apiURL}/test/reset`);expect(r.ok()).toBeTruthy();});

test('@functional insight average refreshes after a salary change',async({page,request})=>{
 const list=await request.get(`${apiURL}/employees`,{params:{q:'PW-001',page:0,size:10}});const employee=(await list.json()).items[0];
 const params={groupBy:'country',country:'IN'};
 const baseline=(await (await request.get(`${apiURL}/insights/pay`,{params})).json()).data.groups[0].average;
 const changed=await request.post(`${apiURL}/employees/${employee.id}/salary-history`,{data:{basic:'50000.00',allowances:'5000.00',deductions:'1000.00',currencyCode:'INR',effectiveDate:'2026-01-01'}});expect(changed.ok()).toBeTruthy();
 const after=(await (await request.get(`${apiURL}/insights/pay`,{params})).json()).data.groups[0].average;expect(after).not.toBe(baseline);
 const insights=new InsightsPage(page);await insights.open();await insights.filterCountry('IN');
 await expect(page.getByTestId('insight-row-INR-india')).toContainText(after.toFixed(2));
});
