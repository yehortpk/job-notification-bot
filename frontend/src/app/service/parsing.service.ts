import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ParsingProgress } from '../type/parsing-progress.type';
import { environment } from '../../environment';

@Injectable({
    providedIn: 'root'
  })
  export class ParsingService {
    private baseUrl = environment.router_service_url + "/parser";
  
    constructor(private http: HttpClient) {}
  
    startParsing(): void {
      this.http.post(`${this.baseUrl}/start`, {}).subscribe(
        {
            error: console.error
        }
      );
    }
  
    getProgress(): Observable<ParsingProgress> {
      return this.http.get<ParsingProgress>(`${this.baseUrl}/progress`);
    }
  }