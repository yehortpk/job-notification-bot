import { Component } from '@angular/core';
import { VacancyService } from '../service/vacancy.service';
import {Vacancy } from "../type/vacancy.type"
import { PaginationService } from '../utils/pagination.service';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TruncatePipe } from '../pipe/truncate.pipe';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  standalone: true,
  imports: [CommonModule, TruncatePipe]
})
export class DashboardComponent {
  vacancies: Vacancy[]
  currentPage: number = 1
  pageSize: number
  totalVacancies: number
  totalPages: number
  pages: string[] = []

  constructor(vacancyService: VacancyService, private paginationService: PaginationService, private router: Router, private route: ActivatedRoute) {
    const vacanciesDto = vacancyService.getVacancies();
    this.vacancies = vacanciesDto.vacancies;
    this.pageSize = vacanciesDto.pageSize;
    this.totalVacancies = vacanciesDto.totalVacancies;
    this.totalPages = vacanciesDto.totalPages;

    this.route.queryParamMap.subscribe(params => {
      this.currentPage = parseInt(params.get('page') || '1', 10);
      this.pages = this.paginationService.generatePagination(this.currentPage, this.totalPages)
    });
  }

  onPageChange(page: number|string) {
    if (typeof page == "string") {
      page = Number.parseInt(page)
    }

    this.router.navigate(["/dashboard"], {
      relativeTo: this.route,
      queryParams: { page: page },
      queryParamsHandling: 'merge'
    });
  }
}
