import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-shell',
  standalone: true,
  imports: [MatDialogModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header><span class="eyebrow">{{ eyebrow() }}</span><h2 mat-dialog-title>{{ title() }}</h2><p>{{ description() }}</p></header>
    <mat-dialog-content data-testid="dialog-content"><ng-content /></mat-dialog-content>
    <mat-dialog-actions align="end" data-testid="dialog-actions"><ng-content select="[dialogActions]" /></mat-dialog-actions>
  `,
  styles: [`
    :host { display: block; }
    header { padding: 24px 24px 12px; border-bottom: 1px solid var(--border,#e5e9f0); }
    .eyebrow { color: var(--primary,#2368ce); font-size: 11px; font-weight: 700; letter-spacing: .12em; }
    h2 { padding: 0; margin: 6px 0; font-size: 23px; }
    h2::before { display: none; }
    p { margin: 0 0 12px; color: var(--muted,#65738b); font-size: 13px; line-height: 1.55; }
    mat-dialog-content { padding: 24px; }
    mat-dialog-actions { padding: 16px 24px; border-top: 1px solid var(--border,#e5e9f0); gap: 8px; }
  `]
})
export class DialogShellComponent {
  readonly title = input.required<string>();
  readonly description = input('');
  readonly eyebrow = input('PEOPLE OPERATIONS');
}
