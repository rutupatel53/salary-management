import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet, NavigationEnd } from '@angular/router';
import { BreakpointObserver } from '@angular/cdk/layout';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { filter } from 'rxjs';
import { AppIconComponent } from './shared/app-icon.component';
import { StatusBadgeComponent } from './shared/status-badge.component';

@Component({
 selector: 'app-root', standalone: true,
 imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, MatSidenavModule, MatButtonModule, AppIconComponent, StatusBadgeComponent],
 templateUrl: './app.component.html', styleUrl: './app.component.css',
 host: { '(document:keydown.escape)': 'closeMobileNavigation()' },
 changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
 readonly mobile = signal(false);
 readonly menuOpen = signal(true);
 readonly pageName = signal('Employees');
 readonly links = [
   { path: '/employees', label: 'Employees', icon: 'people', testId: 'nav-employees' },
   { path: '/salary-assignments', label: 'Salary assignments', icon: 'file', testId: 'nav-salary-assignments' },
   { path: '/salary-templates', label: 'Salary templates', icon: 'grid', testId: 'nav-salary-templates' },
   { path: '/insights', label: 'Pay insights', icon: 'insights', testId: 'nav-insights' },
   { path: '/import-export', label: 'Import & export', icon: 'file', testId: 'nav-import-export' },
   { path: '/activity', label: 'Activity log', icon: 'activity', testId: 'nav-activity' }
 ];
 closeMobileNavigation(): void { if (this.mobile()) this.menuOpen.set(false); }
 constructor() {
   inject(BreakpointObserver).observe('(max-width: 960px)').pipe(takeUntilDestroyed()).subscribe(state => {
     this.mobile.set(state.matches);
     this.menuOpen.set(!state.matches);
   });
   inject(Router).events.pipe(filter(event => event instanceof NavigationEnd), takeUntilDestroyed()).subscribe(event => {
     const link = this.links.find(item => event.urlAfterRedirects.startsWith(item.path));
     this.pageName.set(link?.label || 'Workspace');
     if (this.mobile()) this.menuOpen.set(false);
   });
 }
}
