import { test, expect } from '@playwright/test';
const apiURL = process.env.API_URL ?? 'http://127.0.0.1:8080/api';
test.beforeEach(async ({ request }) => { expect((await request.post(`${apiURL}/test/reset`)).ok()).toBeTruthy(); });

test('@functional HR creates a template and assigns salary directly by employee search', async ({ page, request }) => {
  await page.goto('/salary-templates');
  await page.getByTestId('create-template').click();
  await page.getByTestId('template-name').fill('PW-Standard');
  await page.getByTestId('template-status').click();
  await page.getByRole('option', { name: 'Active', exact: true }).click();
  await page.getByTestId('save-template').click();
  await expect(page.getByRole('dialog')).toHaveCount(0);
  await expect(page.getByTestId('template-table')).toContainText('PW-Standard');
  await expect(page.getByTestId('page-guide')).toContainText('Create a country and currency-specific template once');
  const editTemplate = page.getByRole('button', { name: 'View or edit PW-Standard' });
  await expect(editTemplate).toBeVisible();
  await editTemplate.hover();
  await expect(page.locator('.mat-mdc-tooltip-surface')).toHaveText('View or edit template');
  await expect(page.getByRole('button', { name: 'Copy PW-Standard as a new draft' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Assign PW-Standard to an employee' })).toBeVisible();
  await page.goto('/salary-assignments');
  await page.getByTestId('assignment-employee-search').fill('PW-001');
  await page.getByTestId('search-assignment-employee').click();
  await page.getByTestId('assign-select-PW-001').click();
  await page.getByTestId('assignment-template').click();
  await page.getByRole('option', { name: 'PW-Standard', exact: true }).click();
  await page.getByTestId('assignment-ctc').fill('1200000.00');
  await page.getByTestId('assignment-date').fill('2027-01-01');
  await page.getByTestId('preview-assignment').click();
  await expect(page.getByTestId('assignment-review')).toContainText('100,000.00');
  await page.getByTestId('confirm-assignment').click();
  await page.getByTestId('confirm-action').click();
  await expect(page.getByTestId('assignment-success')).toContainText('Salary assigned');
  const employees = await request.get(`${apiURL}/employees?q=PW-001`);
  const employee = (await employees.json()).data.items[0];
  const history = (await (await request.get(`${apiURL}/employees/${employee.id}/salary-history`)).json()).data;
  expect(history).toHaveLength(2);
  expect(history[0]).toMatchObject({basic:40000,allowances:60000,gross:100000,net:100000});
});

test('@functional assignment rejects a stale template and incompatible country', async ({ request }) => {
  const response = await request.post(`${apiURL}/salary-templates`, {data:{name:'PW-Revision',countryCode:'IN',status:'ACTIVE',components:[{name:'Basic',category:'BASIC',calculation:'PERCENT_CTC',value:40},{name:'Allowance',category:'ALLOWANCE',calculation:'BALANCE',value:0}]}});
  expect(response.status()).toBe(201);
  const template = (await response.json()).data;
  const employee = (await (await request.get(`${apiURL}/employees?q=PW-001`)).json()).data.items[0];
  const requestBody = {employeeId:employee.id,templateId:template.id,templateRevision:template.revision,annualCtc:'1200000.00',effectiveDate:'2027-01-01'};
  expect((await request.post(`${apiURL}/salary-assignments/preview`,{data:requestBody})).ok()).toBeTruthy();
  expect((await request.put(`${apiURL}/salary-templates/${template.id}`,{data:{...template,name:'PW-Revision updated'}})).ok()).toBeTruthy();
  expect((await request.post(`${apiURL}/salary-assignments`,{data:requestBody})).status()).toBe(409);
  const countryTemplate = (await (await request.get(`${apiURL}/salary-templates?country=US&status=ACTIVE`)).json()).data.items[0];
  expect((await request.post(`${apiURL}/salary-assignments/preview`,{data:{...requestBody,templateId:countryTemplate.id,templateRevision:countryTemplate.revision}})).status()).toBe(409);
});


test('@data template edits preserve previously assigned component snapshots', async ({ request }) => {
  const templateRequest = {name:'PW-Snapshot',countryCode:'IN',status:'ACTIVE',components:[{name:'Basic',category:'BASIC',calculation:'PERCENT_CTC',value:40},{name:'Allowance',category:'ALLOWANCE',calculation:'BALANCE',value:0}]};
  const template = (await (await request.post(`${apiURL}/salary-templates`,{data:templateRequest})).json()).data;
  const employee = (await (await request.get(`${apiURL}/employees?q=PW-001`)).json()).data.items[0];
  const assignment = await request.post(`${apiURL}/salary-assignments`,{data:{employeeId:employee.id,templateId:template.id,templateRevision:template.revision,annualCtc:'1200000.00',effectiveDate:'2027-01-01'}});
  expect(assignment.status()).toBe(201);
  const saved = (await assignment.json()).data;
  expect(saved.assignmentBreakdown.monthly.basic).toBe(40000);
  expect((await request.put(`${apiURL}/salary-templates/${template.id}`,{data:{...template,components:[{...template.components[0],value:50},template.components[1]]}})).ok()).toBeTruthy();
  const history = (await (await request.get(`${apiURL}/employees/${employee.id}/salary-history`)).json()).data;
  expect(history[0].assignmentBreakdown).toEqual(saved.assignmentBreakdown);
  expect(history[0].annualCtc).toBe(1200000);
  expect(history[0].templateRevision).toBe(template.revision);
});
