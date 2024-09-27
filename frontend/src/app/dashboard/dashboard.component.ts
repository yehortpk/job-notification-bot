import { Component, OnInit } from '@angular/core';
import { VacancyService } from '../service/vacancy.service';
import { Vacancy } from "../type/vacancy.type"
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
export class DashboardComponent implements OnInit{
  vacancies: Vacancy[] = []
  currentPage: number = 1
  pageSize: number = 10
  totalVacancies: number = 0
  totalPages: number = 0
  pages: string[] = []

  constructor(private vacancyService: VacancyService, private paginationService: PaginationService, private router: Router, private route: ActivatedRoute) {
    this.requestVacanciesOnPage(this.currentPage);
    this.route.queryParamMap.subscribe(params => {
      this.currentPage = parseInt(params.get('page') || '1', 10);
      this.requestVacanciesOnPage(this.currentPage);      
    });
  }

  ngOnInit() {
    
  }

  requestVacanciesOnPage(pageId: number) {
    this.vacancyService.getVacanciesHttp(pageId).subscribe({
      next: (response) => {
        console.log(response);
        response.vacancies.forEach((vacancy, _, __) => {
          const dummyPictures = ['/img/dummyA.jpg', '/img/dummyB.jpg', '/img/dummyC.jpg']
          vacancy.company.imageUrl = dummyPictures[vacancy.company.company_id % dummyPictures.length];

          if (vacancy.minSalary == 0) {
            vacancy.minSalary = "Not specified"
          }

          if (vacancy.maxSalary == 0) {
            vacancy.maxSalary = "Not specified"
          }

        });
        this.vacancies = response.vacancies;
        this.totalPages = response.totalPages;
        this.totalVacancies = response.totalVacancies
        this.pageSize = Math.round(this.totalVacancies/this.totalPages);
      },
      error: (error) => {
        console.error('An error occurred:', error);
      }
    });
    this.pages = this.paginationService.generatePagination(this.currentPage, this.totalPages)
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
