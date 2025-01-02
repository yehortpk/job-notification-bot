import { Injectable } from '@angular/core';
import { VacanciesListDTO } from '../type/vacancy.type';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VacancyService {
  constructor(private http: HttpClient) { }
  
  getVacancies(pageId: number, pageSize: number, sortBy: string, sortDir: string, queryParam: string): Observable<VacanciesListDTO> {
    const ROUTER_URL = 'http://localhost:8081/vacancy';
    return this.http.get<VacanciesListDTO>(ROUTER_URL, {params: {"page": pageId, "pageSize": pageSize, "sortBy": sortBy, "sortDir": sortDir, "query": queryParam}});
  }

  getVacanciesByFilter(filterId: number, pageId: number, pageSize: number, sortBy: string, sortDir: string): Observable<VacanciesListDTO> {
    const FILTER_URL = `http://localhost:8081/filter/${filterId}/vacancies`;
    return this.http.get<VacanciesListDTO>(FILTER_URL, {params: {"page": pageId, "pageSize": pageSize, "sortBy": sortBy, "sortDir": sortDir}});
  }
}