import { ChangeDetectionStrategy, Component, HostListener, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppIconComponent } from './shared/app-icon.component';
import { filter } from 'rxjs';

@Component({
 selector: 'app-root', standalone: true,
 imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, MatButtonModule, MatSidenavModule, AppIconComponent],
 templateUrl: './app.component.html', styleUrl: './app.component.css',
 changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
 readonly menuOpen = signal(false);
 readonly links = [
   { path: '/employees', label: 'Employees', icon: 'people', testId: 'nav-employees' },
   { path: '/salary-assignments', label: 'Salary assignments', icon: 'file', testId: 'nav-salary-assignments' },
   { path: '/salary-templates', label: 'Salary templates', icon: 'grid', testId: 'nav-salary-templates' },
   { path: '/insights', label: 'Pay insights', icon: 'insights', testId: 'nav-insights' },
   { path: '/import-export', label: 'Import & export', icon: 'file', testId: 'nav-import-export' },
   { path: '/activity', label: 'Activity log', icon: 'activity', testId: 'nav-activity' }
 ];
 toggleNavigation(): void { this.menuOpen.update(open => !open); }
 @HostListener('document:keydown.escape') closeNavigationOnEscape(): void { this.menuOpen.set(false); }
 constructor() {
   inject(Router).events.pipe(filter(event => event instanceof NavigationEnd), takeUntilDestroyed()).subscribe(() => this.menuOpen.set(false));
 }
}
