import { Component } from '@angular/core';
import { AppLayoutComponent } from './app-layout/app-layout.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true,
  imports: [
    AppLayoutComponent,
  ]
})
export class AppComponent{
  title = 'frontend';
}