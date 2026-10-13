import { readFile, mkdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const xmlPath = path.join(root, 'reports', 'junit', 'results.xml');
const outputPath = path.join(root, 'reports', 'detailed-report.html');
let xml;
try { xml = await readFile(xmlPath, 'utf8'); }
catch { console.error('No JUnit XML found. Run a Playwright suite first.'); process.exit(1); }
const tests = [...xml.matchAll(/<testcase\b([^>]*?)(?:\/>|>([\s\S]*?)<\/testcase>)/g)].map(match => {
  const attrs = match[1]; const body = match[2] ?? '';
  const get = name => decode((attrs.match(new RegExp(`\\b${name}="([^"]*)"`)) ?? [])[1] ?? '');
  const failure = body.match(/<(failure|error)\b([^>]*)>([\s\S]*?)<\/\1>/);
  const skipped = /<skipped\b/.test(body);
  const failureMessage = failure ? decode(failure[3].replace(/<[^>]+>/g, ' ').trim()) || decode((failure[2].match(/message="([^"]*)"/) ?? [])[1] ?? '') : '';
  return { name: `${get('classname')} › ${get('name')}`, time: get('time'), status: failure ? 'failed' : skipped ? 'skipped' : 'passed', reason: failureMessage };
});
const counts = { total: tests.length, passed: tests.filter(t => t.status === 'passed').length, failed: tests.filter(t => t.status === 'failed').length, skipped: tests.filter(t => t.status === 'skipped').length, duration: tests.reduce((sum, t) => sum + (Number(t.time) || 0), 0).toFixed(1) };
const rows = tests.map(t => `<tr><td>${escapeHtml(t.status)}</td><td>${escapeHtml(t.name)}</td><td>${escapeHtml(t.reason)}</td><td>${escapeHtml(t.time)}s</td></tr>`).join('\n');
await mkdir(path.dirname(outputPath), { recursive: true });
await writeFile(outputPath, `<!doctype html><html><head><meta charset="utf-8"><title>Salary Management E2E Report</title><style>body{font:14px Arial,sans-serif;margin:2rem;color:#202938}table{width:100%;border-collapse:collapse}td,th{padding:.65rem;border-bottom:1px solid #ddd;text-align:left}th{background:#f4f6f8}</style></head><body><h1>Salary Management E2E Report</h1><p>Total ${counts.total} · Passed ${counts.passed} · Failed ${counts.failed} · Skipped ${counts.skipped} · Duration ${counts.duration}s</p><table><thead><tr><th>Status</th><th>Test</th><th>Failure reason</th><th>Duration</th></tr></thead><tbody>${rows}</tbody></table></body></html>`);
console.log(`JUnit summary: total ${counts.total}, passed ${counts.passed}, failed ${counts.failed}, skipped ${counts.skipped}, duration ${counts.duration}s`);
for (const t of tests.filter(t => t.status === 'failed')) console.log(`FAILED ${t.name}: ${t.reason.split('\n')[0]}`);
console.log(`Detailed HTML report saved to ${outputPath}`);

function decode(value) { return value.replaceAll('&quot;', '"').replaceAll('&apos;', "'").replaceAll('&lt;', '<').replaceAll('&gt;', '>').replaceAll('&amp;', '&'); }
function escapeHtml(value) { return value.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#39;'); }
