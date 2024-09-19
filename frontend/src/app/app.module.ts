import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { AppLayoutComponent } from './app-layout/app-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TruncatePipe } from './truncate.pipe';

@NgModule({
  declarations: [
    AppComponent,
    AppLayoutComponent,
    DashboardComponent,
  ],
  imports: [
    BrowserModule,
    TruncatePipe,
    RouterModule.forRoot([
      {
        path: '',
        children: [
          { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
          { path: 'dashboard', component: DashboardComponent },
        ]
      }
    ])
  ],
  providers: [],
  bootstrap: [AppLayoutComponent]
})
export class AppModule { }