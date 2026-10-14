import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-status-badge', standalone: true, changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<span class="badge" [attr.data-tone]="tone()" data-testid="status-badge"><span class="dot" aria-hidden="true"></span><ng-content /></span>`,
  styles: [`
    .badge{display:inline-flex;align-items:center;gap:6px;padding:4px 9px;border-radius:6px;font-size:11px;font-weight:600;line-height:1.45;white-space:nowrap;background:#eef3fa;color:#476079;border:1px solid #e0e8f2}
    .dot{width:5px;height:5px;border-radius:50%;background:currentColor}
    [data-tone=success]{background:#edf8f2;color:#23754e;border-color:#cce9d9}
    [data-tone=warning]{background:#fff8e8;color:#9c650c;border-color:#f2dfb2}
    [data-tone=danger]{background:#fff0f0;color:#b33939;border-color:#f5d5d5}
    [data-tone=info]{background:#edf4ff;color:#2865b9;border-color:#d5e5fb}
  `]
})
export class StatusBadgeComponent {
  readonly tone = input<'neutral' | 'success' | 'warning' | 'danger' | 'info'>('neutral');
}
