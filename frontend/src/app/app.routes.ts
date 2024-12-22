import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ParserProgressComponent } from './parser-progress/parser-progress.component';
import { FiltersComponent } from './filters/filters.component';

export const routes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'parser', component: ParserProgressComponent },
    { path: 'filters', component: FiltersComponent },
];
