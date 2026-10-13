import { test, expect } from '@playwright/test';
import { EmployeeDetailPage } from '../pages/employee-detail.page';

const apiURL=process.env.API_URL??'http://127.0.0.1:8080/api';
test.beforeEach(async({request})=>{
 const reset=await request.post(`${apiURL}/test/reset`);expect(reset.ok()).toBeTruthy();expect((await reset.json()).data.employees).toBe(6);
});

test('@functional salary change stores exact gross and net in history',async({page,request})=>{
 const list=await request.get(`${apiURL}/employees`,{params:{q:'PW-001',page:0,size:10}});expect(list.ok()).toBeTruthy();
 const employee=(await list.json()).data.items[0];expect(employee.employeeNumber).toBe('PW-001');
 await page.goto(`/employees/${employee.id}`);
 const detail=new EmployeeDetailPage(page);
 await detail.changeSalary('10000.00','1000.00','500.00','2027-01-01');
 await expect(page.getByTestId('salary-preview')).toContainText('Gross: INR 11,000.00');
 await expect(page.getByTestId('salary-history')).toContainText('10,500.00');
 const history=await request.get(`${apiURL}/employees/${employee.id}/salary-history`);const latest=(await history.json()).data[0];
 expect(latest.gross).toBe(11000);expect(latest.net).toBe(10500);expect(latest.basic).toBe(10000);
});
