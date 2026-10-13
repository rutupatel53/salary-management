import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-error-state',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<div class="state error" role="alert" data-testid="error-state"><strong>Something went wrong.</strong><span>{{ message() }}</span></div>`,
  styles: [`.state{display:grid;gap:4px;margin:12px 0;padding:14px 16px;border:1px solid #f1c7c4;border-radius:10px;background:#fff5f4;color:#9b2822}`]
})
export class ErrorStateComponent {
  message = input('Please try again.');
}
