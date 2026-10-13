import { Component, input } from '@angular/core';
@Component({ selector: 'app-empty-state', standalone: true, template: `<div class="empty-state" data-testid="empty-state"><strong>{{ title() }}</strong><p>{{ message() }}</p></div>` })
export class EmptyStateComponent { title = input('Nothing here yet'); message = input('Try changing your filters.'); }
