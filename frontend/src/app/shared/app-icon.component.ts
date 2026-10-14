import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
 selector: 'app-icon', standalone: true, changeDetection: ChangeDetectionStrategy.OnPush,
 template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path [attr.d]="paths[name()] || paths['grid']" /></svg>`,
 styles: [`:host{display:inline-flex;width:20px;height:20px;flex-shrink:0}svg{width:100%;height:100%}`]
})
export class AppIconComponent {
 readonly name = input('grid');
 readonly paths: Record<string, string> = {
   people: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2 M16 3a4 4 0 0 1 0 8 M22 21v-2a4 4 0 0 0-3-3.87 M13 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0',
   insights: 'M4 3v18h17 M8 16v-5 M13 16V7 M18 16V4',
   file: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6 M8 13h8 M8 17h5',
   activity: 'M12 8v4l3 2 M21 12a9 9 0 1 1-3-6.7 M21 3v5h-5',
   menu: 'M4 6h16 M4 12h16 M4 18h16',
   close: 'M6 6l12 12 M18 6L6 18',
   grid: 'M3 3h7v7H3z M14 3h7v7h-7z M3 14h7v7H3z M14 14h7v7h-7z',
   chevron: 'M9 5l7 7-7 7'
 };
}
