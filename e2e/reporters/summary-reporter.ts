import type { FullConfig, FullResult, Reporter, Suite, TestCase, TestResult } from '@playwright/test/reporter';
import { mkdirSync, writeFileSync } from 'node:fs';
import path from 'node:path';

export default class SummaryReporter implements Reporter {
  private readonly results = new Map<string, { title: string; status: string; reason: string; duration: number }>();
  private startedAt = 0;
  onBegin(_config: FullConfig, suite: Suite): void { this.startedAt = Date.now(); this.expected = suite.allTests().length; }
  private expected = 0;
  onTestEnd(test: TestCase, result: TestResult): void {
    this.results.set(test.id, { title: test.titlePath().join(' › '), status: result.status, reason: result.error?.message?.split('\n')[0] ?? '', duration: result.duration });
  }
  async onEnd(_result: FullResult): Promise<void> {
    const tests = [...this.results.values()];
    const passed = tests.filter(t => t.status === 'passed').length;
    const skipped = tests.filter(t => t.status === 'skipped').length;
    const failed = tests.filter(t => ['failed', 'timedOut', 'interrupted'].includes(t.status));
    const duration = ((Date.now() - this.startedAt) / 1000).toFixed(1);
    console.log(`\nPlaywright summary: total ${this.expected}, passed ${passed}, failed ${failed.length}, skipped ${skipped}, duration ${duration}s`);
    for (const test of failed) console.log(`FAILED ${test.title}: ${test.reason || test.status}`);
    const htmlRows = tests.map(t => `<tr><td>${escapeHtml(t.status)}</td><td>${escapeHtml(t.title)}</td><td>${escapeHtml(t.reason)}</td><td>${(t.duration / 1000).toFixed(2)}s</td></tr>`).join('\n');
    const output = path.resolve('reports/detailed-report.html'); mkdirSync(path.dirname(output), { recursive: true });
    writeFileSync(output, `<!doctype html><html><head><meta charset="utf-8"><title>Salary Management E2E Report</title><style>body{font:14px Arial,sans-serif;margin:2rem;color:#202938}table{width:100%;border-collapse:collapse}td,th{padding:.65rem;border-bottom:1px solid #ddd;text-align:left}th{background:#f4f6f8}</style></head><body><h1>Salary Management E2E Report</h1><p>Total ${this.expected} · Passed ${passed} · Failed ${failed.length} · Skipped ${skipped} · Duration ${duration}s</p><table><thead><tr><th>Status</th><th>Test</th><th>Failure reason</th><th>Duration</th></tr></thead><tbody>${htmlRows}</tbody></table></body></html>`);
    console.log(`Detailed HTML report: ${output}`);
  }
}

function escapeHtml(value: string): string { return value.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#39;'); }
