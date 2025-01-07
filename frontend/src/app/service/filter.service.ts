import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Filter } from '../type/filter.type';
import { environment } from '../../environment';

@Injectable({
  providedIn: 'root'
})
export class FilterService {
  constructor(private http: HttpClient) { }
  
  getAllFilters(): Observable<Filter[]> {
    const ROUTER_URL = `${environment.router_service_url}/filter`;
    return this.http.get<Filter[]>(ROUTER_URL);
  }
}