import { ChangeDetectionStrategy, Component, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-filter-bar',
  standalone: true,
  imports: [MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<section class="filters" role="search" data-testid="filter-bar"><ng-content></ng-content><button mat-button type="button" (click)="clear.emit()" data-testid="clear-filters">Clear filters</button></section>`,
  styles: [`.filters{display:flex;gap:12px;align-items:center;flex-wrap:wrap;margin:8px 0 14px}.filters mat-form-field{min-width:200px}@media(max-width:720px){.filters mat-form-field{flex:1 1 100%;min-width:0}}`]
})
export class FilterBarComponent {
  clear = output<void>();
}
