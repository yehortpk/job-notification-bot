import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Filter } from '../type/filter.type';

@Injectable({
  providedIn: 'root'
})
export class FilterService {
  constructor(private http: HttpClient) { }
  
  getAllFilters(): Observable<Filter[]> {
    const ROUTER_URL = 'http://localhost:8081/filter';
    return this.http.get<Filter[]>(ROUTER_URL);
  }
}