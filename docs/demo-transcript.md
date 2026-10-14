# Salary Management: two-minute local demo

Recorded from the running local app with synthetic data. Narration uses an offline synthetic voice. Public hosting is a separate deployment step.

## 1. People at scale

This is a local demonstration of ACME Salary Management, using fictional employee data. The directory supports ten thousand generated employees, with search, filters, and server-side pagination. The menu connects the main HR workspaces.

## 2. Preview spreadsheet errors

HR starts with an Excel workbook. Preview checks each row before saving anything. Here, deductions exceed gross pay. The error identifies the row, and confirmation is disabled until the workbook is corrected.

## 3. Correct and import

After correcting the workbook, HR uploads it again. The row is now ready. Confirmation validates the file again and creates the employee and first salary together, in one transaction. Existing employees are never overwritten.

## 4. Create a reusable salary template

Salary templates define reusable rules for a country and currency. This active India template uses forty percent of CTC for basic pay, with the remaining CTC as an allowance. Template changes preserve existing salary history.

## 5. Assign CTC, preview, confirm

HR can search an employee directly in the assignment workspace. Choose the compatible template, enter annual CTC, and set an effective date. Preview shows monthly and annual amounts. Here, annual CTC is twelve lakh rupees, and monthly gross is one lakh. Confirmation saves a new salary snapshot.

## 6. Answer a pay question

Insights compare average, median, minimum, maximum, and range. India results remain in rupees. Outliers compare employees in the same job title, country, and currency, with at least five salaries. An outlier is a review signal.

## 7. Export the filtered view

HR filters the export to the imported employee and downloads an Excel workbook. The export uses the same database filters as the directory and includes current salary values, without combining currencies.

## 8. Review the activity trail

The activity screen records imports, salary changes, and exports. This local preview is ready for public deployment verification.