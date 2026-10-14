import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-server-paged-table',
  standalone: true,
  imports: [MatPaginatorModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<div class="table-wrap" data-testid="server-paged-table"><div class="table-scroll" tabindex="0" aria-label="Scrollable data table"><ng-content></ng-content></div><mat-paginator [length]="length()" [pageIndex]="pageIndex()" [pageSize]="pageSize()" [pageSizeOptions]="pageSizeOptions()" [selectConfig]="{panelClass:'app-dropdown-panel'}" (page)="pageChange.emit($event)" data-testid="server-pagination"></mat-paginator></div>`,
  styles: [`.table-wrap{overflow:hidden;background:#fff;border:1px solid #e3e7ee;border-radius:12px} .table-scroll{overflow:auto;max-width:100%} :host{display:block;min-width:0} ::ng-deep .mat-mdc-paginator{border-top:1px solid #edf0f5}`]
})
export class ServerPagedTableComponent {
  length = input(0);
  pageIndex = input(0);
  pageSize = input(25);
  pageSizeOptions = input<number[]>([10, 25, 50]);
  pageChange = output<PageEvent>();
}
