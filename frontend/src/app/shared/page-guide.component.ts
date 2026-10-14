import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-page-guide',
  standalone: true,
  template: `<aside class="page-guide" data-testid="page-guide" aria-label="How to use this page"><span class="guide-mark" aria-hidden="true">i</span><div><strong>How to use this page</strong><ng-content /></div></aside>`,
  styles: [`:host{display:block}.page-guide{display:flex;align-items:flex-start;gap:12px;margin:0 0 20px;padding:14px 16px;border:1px solid var(--border,#e4e7ec);border-radius:10px;background:var(--surface-soft,#f7f9fc);color:var(--ink,#253148);font-size:13px;line-height:1.55}.guide-mark{display:grid;place-items:center;flex:0 0 22px;width:22px;height:22px;border-radius:50%;background:#e8f0ff;color:#285ab6;font-weight:700}.page-guide strong{display:block;margin:0 0 4px}.page-guide ::ng-deep p{margin:0;color:var(--muted,#667085)}.page-guide ::ng-deep ol{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:8px 20px;margin:8px 0 0;padding-left:20px}.page-guide ::ng-deep li{min-width:0;padding-left:2px;padding-right:8px}@media(max-width:760px){.page-guide ::ng-deep ol{grid-template-columns:1fr;gap:5px}}`],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageGuideComponent {}
