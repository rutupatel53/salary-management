# Assessment demo flow

A narrated two-minute video is ready at `output/demo/Salary Management Demo.mp4`. It records the local application with fictional data, chapter captions, and an optional English subtitle track. The full spoken text is in [demo-transcript.md](demo-transcript.md).

## Recorded walkthrough

| Time | What the viewer sees |
| --- | --- |
| 0:00-0:14 | Directory of 10,000 generated employees; search and hamburger navigation |
| 0:14-0:30 | Invalid Excel preview with row-level error and disabled confirmation |
| 0:30-0:46 | Corrected workbook preview and atomic import |
| 0:46-1:02 | Reusable active India salary template with basic pay and balancing allowance |
| 1:02-1:26 | Direct employee search, annual CTC, effective date, monthly/annual preview, confirmation |
| 1:26-1:40 | India gross-pay summary and explanation of comparable-role outliers |
| 1:40-1:52 | Filtered Excel export for the imported employee |
| 1:52-2:00 | Activity trail for import, template, salary, and export actions |

## Sharing the prepared video

Upload the MP4 to a reviewer-accessible sharing location or attach it if the email attachment limit allows. It is about 4.4 MB. Open the shared link without your account session to check access. Describe it as a local application demonstration; do not claim the recording proves public hosting.

## Optional hosted recording

After deployment, repeat this flow on the public app if time allows. First verify API health, the generated seed count, proxy routing, and import/export. Use a fresh fictional employee identity so create-only import does not conflict with an existing record. Choose a salary effective date after the employee's latest salary and no later than today if the demo should change current insights.

Warm the API before recording. Keep passwords, environment variables, account settings, and real employee data out of the recording. Keep currencies separate and label annual versus monthly amounts clearly.
