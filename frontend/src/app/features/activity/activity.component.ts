import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import type { PageEvent } from '@angular/material/paginator';
import { ActivityApiService, Activity } from './activity-api.service';
import { EmptyStateComponent } from '../../shared/empty-state.component';
import { ServerPagedTableComponent } from '../../shared/server-paged-table.component';
import { LoadingStateComponent } from '../../shared/loading-state.component';
import { ErrorStateComponent } from '../../shared/error-state.component';

@Component({ selector: 'app-activity', standalone: true, imports: [CommonModule, MatButtonModule, EmptyStateComponent, ServerPagedTableComponent, LoadingStateComponent, ErrorStateComponent], templateUrl: './activity.component.html', styleUrl: './activity.component.css', changeDetection: ChangeDetectionStrategy.OnPush })
export class ActivityComponent implements OnInit {
  private readonly api = inject(ActivityApiService);
  private readonly cdr = inject(ChangeDetectorRef);
  rows: Activity[] = []; total = 0; page = 0; size = 25; loading = false; error = '';
  ngOnInit(): void { this.load(); }
  load(): void { this.loading = true; this.error = ''; this.cdr.markForCheck(); this.api.list(this.page, this.size).pipe(finalize(() => { this.loading = false; this.cdr.markForCheck(); })).subscribe({ next: result => { this.rows = result.data.items; this.total = result.data.totalItems; this.cdr.markForCheck(); }, error: () => { this.error = 'Could not load activity.'; this.cdr.markForCheck(); } }); }
  pageChange(event: PageEvent): void { this.page = event.pageIndex; this.size = event.pageSize; this.load(); }
  detail(activity: Activity): string { return Object.entries(activity.details ?? {}).map(([key, value]) => `${key}: ${String(value)}`).join(' · ') || 'No extra details'; }
  testId(row: Activity): string { return `activity-row-${row.id}`; }
}
