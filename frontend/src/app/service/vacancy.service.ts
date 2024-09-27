import { Injectable } from '@angular/core';
import { VacanciesListDTO } from '../type/vacancy.type';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VacancyService {
  constructor(private http: HttpClient) { }
  getVacancies(pageId: number): Observable<VacanciesListDTO> {
    const ROUTER_URL = 'http://localhost:8081/vacancy';
    return this.http.get<VacanciesListDTO>(ROUTER_URL, {params: {"page": pageId}});
  }
}