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
   edit: 'M12 20h9 M16.5 3.5a2.12 2.12 0 0 1 3 3L8 18l-4 1 1-4Z',
   copy: 'M8 8V4a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2h-4 M4 8h10a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V10a2 2 0 0 1 2-2Z',
   assign: 'M15 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M16 11h6 M19 8l3 3-3 3',
   search: 'M11 19a8 8 0 1 1 5.657-2.343A8 8 0 0 1 11 19Z M16.65 16.65 21 21',
   chevron: 'M9 5l7 7-7 7'
 };
}
