import { Component } from '@angular/core';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TruncatePipe } from './pipe/truncate.pipe';
import { RouterOutlet } from '@angular/router';
import { AppLayoutComponent } from './app-layout/app-layout.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true,
  imports: [
    TruncatePipe,
    AppLayoutComponent,
    RouterOutlet,
    DashboardComponent,
  ]
})
export class AppComponent{
  title = 'frontend';
}