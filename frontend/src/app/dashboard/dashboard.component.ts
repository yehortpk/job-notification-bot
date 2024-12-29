import { VacancyService } from '../service/vacancy.service';
import { VacanciesListDTO, Vacancy } from "../type/vacancy.type"

import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { CommonModule } from '@angular/common';

import { MatSortModule, Sort } from '@angular/material/sort';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatSortModule, MatPaginatorModule]
})
export class DashboardComponent implements OnInit {
  vacancies: Vacancy[] = []
  currentPage!: number
  pageSize!: number
  totalVacancies: number = 0
  totalPages: number = 0

  displayedColumns: string[] = ['title', 'minSalary', 'maxSalary', 'company', 'parsedAt'];
  dataSource: MatTableDataSource<Vacancy> = new MatTableDataSource();

  constructor(
    
    private vacancyService: VacancyService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.requestVacancies(params);
    });
  }

  requestVacancies(params: ParamMap) {
    this.currentPage = parseInt(params.get('page') || '1');
    this.pageSize = parseInt(params.get('pageSize') || '10');
    let sortBy = params.get('sortBy') || '';
    let sortDir = params.get('sortDir') || '';
    let filterParam = params.get('filter')

    if (filterParam) {
      this.requestVacanciesByFilter(Number.parseInt(filterParam), this.currentPage, this.pageSize, sortBy, sortDir);
    } else {
      this.requestVacanciesOnPage(this.currentPage, this.pageSize, sortBy, sortDir);
    }
  }

  requestVacanciesByFilter(filterId: number, pageId: number, pageSize: number, sortBy: string, sortDir: string) {
    this.vacancyService.getVacanciesByFilter(filterId, pageId, pageSize, sortBy, sortDir).subscribe({
      next: (response) => {
        this.updatePage(response)
      },
      error: (error) => {
        console.error('An error occurred:', error);
      }
    });
  }

  requestVacanciesOnPage(pageId: number, pageSize: number, sortBy: string, sortDir: string) {
    this.vacancyService.getVacancies(pageId, pageSize, sortBy, sortDir).subscribe({
      next: (response) => {
        this.updatePage(response)
      },
      error: (error) => {
        console.error('An error occurred:', error);
      }
    });
  }

  updatePage(response: VacanciesListDTO) {
    response.vacancies.forEach((vacancy, _, __) => {
      if(!vacancy.company.imageUrl) {
        const dummyPictures = [
          '/img/logo/dummyA.png',
          '/img/logo/dummyB.png', 
          '/img/logo/dummyC.png', 
          '/img/logo/dummyD.png', 
          '/img/logo/dummyE.png', 
          '/img/logo/dummyF.png', 
          '/img/logo/dummyG.png'
        ];
        vacancy.company.imageUrl = dummyPictures[vacancy.company.company_id % dummyPictures.length];
      }

      if (vacancy.minSalary == 0) {
        vacancy.minSalary = "Not specified"
      }

      if (vacancy.maxSalary == 0) {
        vacancy.maxSalary = "Not specified"
      }

    });
    this.vacancies = response.vacancies;

    this.dataSource = new MatTableDataSource(this.vacancies)

    this.totalPages = response.totalPages;
    this.totalVacancies = response.totalVacancies;
    this.pageSize = Math.round(this.totalVacancies/this.totalPages);
  }

  onPageChange(event: PageEvent) {
    this.router.navigate(["/dashboard"], {
      relativeTo: this.route,
      queryParams: { page: event.pageIndex, pageSize: event.pageSize},
      queryParamsHandling: 'merge'
    });
  }

  onSortChange(event: Sort) {
    this.router.navigate(["/dashboard"], {
      relativeTo: this.route,
      queryParams: { 
        page: 0, 
        sortBy: event.active,
        sortDir: event.direction.toUpperCase()
      },
      queryParamsHandling: 'merge'
    });
}
}
