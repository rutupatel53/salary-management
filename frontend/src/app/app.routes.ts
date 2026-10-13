import { Routes } from '@angular/router';
export const appRoutes: Routes = [
 { path: '', pathMatch: 'full', redirectTo: 'employees' },
 { path: 'employees', loadComponent: () => import('./features/employees/employees.component').then(m => m.EmployeesComponent) },
 { path: 'employees/:id', loadComponent: () => import('./features/employees/employee-detail.component').then(m => m.EmployeeDetailComponent) },
 { path: 'insights', loadComponent: () => import('./features/insights/insights.component').then(m => m.InsightsComponent) },
 { path: 'import-export', loadComponent: () => import('./features/import-export/import-export.component').then(m => m.ImportExportComponent) },
 { path: 'activity', loadComponent: () => import('./features/activity/activity.component').then(m => m.ActivityComponent) },
 { path: '**', redirectTo: 'employees' }
];
