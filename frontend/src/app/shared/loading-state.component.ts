import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { AppIconComponent } from './app-icon.component';

@Component({
  selector: 'app-loading-state',
  standalone: true,
  imports: [AppIconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<div class="state loading" role="status" aria-live="polite" data-testid="loading-state"><span class="loader-logo" aria-hidden="true"><span class="loader-ring"></span><span class="brand-mark"><app-icon name="grid" /></span></span><span class="loader-message">{{ message() }}</span></div>`,
  styles: [`:host{display:block}.state{display:flex;flex-direction:column;align-items:center;justify-content:center;gap:12px;min-height:116px;padding:20px 12px;color:var(--muted,#778498);font-size:13px}.loader-logo{position:relative;display:grid;place-items:center;width:44px;height:44px}.loader-ring{position:absolute;inset:0;border:2px solid #dce7f6;border-top-color:var(--primary,#2368ce);border-radius:50%;animation:spin .9s linear infinite}.brand-mark{display:grid;place-items:center;width:30px;height:30px;border-radius:8px;background:var(--primary,#2368ce);color:#fff}.brand-mark app-icon{width:17px;height:17px}.loader-message{line-height:1.4}@keyframes spin{to{transform:rotate(360deg)}}@media(prefers-reduced-motion:reduce){.loader-ring{animation:none}}`]
})
export class LoadingStateComponent {
  message = input('Loading…');
}
