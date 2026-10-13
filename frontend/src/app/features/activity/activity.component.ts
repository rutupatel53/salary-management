import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ActivityApiService, Activity } from './activity-api.service';
import { EmptyStateComponent } from '../../shared/empty-state.component';

@Component({ selector: 'app-activity', standalone: true, imports: [CommonModule, MatButtonModule, MatPaginatorModule, EmptyStateComponent], templateUrl: './activity.component.html', styleUrl: './activity.component.css' })
export class ActivityComponent implements OnInit {
  private readonly api = inject(ActivityApiService);
  rows: Activity[] = []; total = 0; page = 0; size = 25; loading = false; error = '';
  ngOnInit(): void { this.load(); }
  load(): void { this.loading = true; this.error = ''; this.api.list(this.page, this.size).subscribe({ next: result => { this.rows = result.items; this.total = result.totalItems; }, error: () => this.error = 'Could not load activity.', complete: () => this.loading = false }); }
  pageChange(event: PageEvent): void { this.page = event.pageIndex; this.size = event.pageSize; this.load(); }
  detail(activity: Activity): string { return Object.entries(activity.details ?? {}).map(([key, value]) => `${key}: ${String(value)}`).join(' · ') || 'No extra details'; }
  testId(row: Activity): string { return `activity-row-${row.id}`; }
}
