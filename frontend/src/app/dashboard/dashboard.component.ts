import { Component } from '@angular/core';
import { VacancyService } from '../vacancy.service';
import {Vacancy} from "../../app/types/vacancy.type"

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  vacancies: Vacancy[]
  constructor(vacancyService: VacancyService) {
    this.vacancies = vacancyService.getVacancies();
  }
}
