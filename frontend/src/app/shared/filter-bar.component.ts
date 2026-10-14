import { ChangeDetectionStrategy, Component, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-filter-bar',
  standalone: true,
  imports: [MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<section class="filters" role="search" data-testid="filter-bar"><ng-content></ng-content><button mat-button type="button" (click)="clear.emit()" data-testid="clear-filters">Clear filters</button></section>`,
  styles: [`.filters{display:flex;gap:14px;align-items:flex-end;flex-wrap:wrap;margin:8px 0 20px}`]
})
export class FilterBarComponent {
  clear = output<void>();
}
