import { Routes } from '@angular/router';
import { EmployeesComponent } from './features/employees/employees.component';
import { EmployeeDetailComponent } from './features/employees/employee-detail.component';
export const appRoutes: Routes = [
 { path: '', pathMatch: 'full', redirectTo: 'employees' },
 { path: 'employees', component: EmployeesComponent },
 { path: 'employees/:id', component: EmployeeDetailComponent },
 { path: '**', redirectTo: 'employees' }
];
