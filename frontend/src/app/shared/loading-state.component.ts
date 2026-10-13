import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-loading-state',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<div class="state loading" role="status" aria-live="polite" data-testid="loading-state"><span class="spinner" aria-hidden="true"></span>{{ message() }}</div>`,
  styles: [`.state{display:flex;align-items:center;gap:10px;padding:16px 2px;color:#5b6477}.spinner{width:16px;height:16px;border:2px solid #d5d9e5;border-top-color:#5448b8;border-radius:50%;animation:spin .8s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}@media(prefers-reduced-motion:reduce){.spinner{animation:none}}`]
})
export class LoadingStateComponent {
  message = input('Loading…');
}
