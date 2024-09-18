import { Component } from '@angular/core';
import { TruncatePipe } from '../truncate.pipe';
import { NgFor } from '@angular/common';
import { VacancyService } from '../vacancy.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [TruncatePipe, NgFor],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  vacancies: any
  constructor(vacancyService: VacancyService) {
    this.vacancies = vacancyService.getVacancies();
  }
}
