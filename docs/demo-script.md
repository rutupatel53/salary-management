# Assessment demo script

Target length: 3 minutes. Record the deployed public app after CI and seed verification. Use only the fictional assessment dataset.

## Walkthrough

1. **0:00–0:15 | Home and scale.** Open the public app at the employee directory. Point out the five countries and 10,000 synthetic employees. Show the page count and server-side paging.
2. **0:15–0:40 | Find and edit.** Search for an employee number, filter to a country and department, open the profile, and save one harmless profile change. Mention the hire date and archive behavior.
3. **0:40–1:10 | Salary history.** Open Salary structure. Enter a new effective date and basic, allowances, and deductions. Show gross and net preview, save, and show the appended history row. State the formulas and that PostgreSQL stores generated gross/net values.
4. **1:10–1:35 | Pay question.** Open Pay insights. Switch between country, department, and job title. Point out the currency column and average/median/min/max/range. Show an outlier and explain the job-title + country + currency grouping, IQR rule, and five-salary minimum.
5. **1:35–2:15 | Spreadsheet workflow.** Download the template. Upload the invalid fixture and show the row-level deduction error and disabled confirmation. Replace it with the valid fixture, preview it, confirm, and search for the imported employee.
6. **2:15–2:35 | Filtered export.** Apply a country or employee filter in Import and export and download the workbook. Mention that export reuses the directory's server-side filters and includes local currency.
7. **2:35–2:50 | Activity.** Open Activity and show the salary/import/export events in newest-first order.
8. **2:50–3:00 | Engineering choices.** Close with the small schema, append-only salary versions, deterministic 10,000-person seed, tests, and GitHub auto-deploy separated from test-only CI.

## Recording checks

- Load and warm the Render API before recording; its free service may take time to wake.
- Check the deployed employee count and run the public sanity workflow first.
- Use synthetic records only. Do not show account settings, environment variables, secrets, or real email addresses.
- Keep the mouse and browser zoom readable. Stop recording only after the downloaded workbook is visible or saved to a neutral demo folder.
