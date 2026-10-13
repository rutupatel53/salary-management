import { Routes } from '@angular/router';
import { EmployeesComponent } from './features/employees/employees.component';
export const appRoutes: Routes = [
 { path: '', pathMatch: 'full', redirectTo: 'employees' },
 { path: 'employees', component: EmployeesComponent },
 { path: '**', redirectTo: 'employees' }
];
