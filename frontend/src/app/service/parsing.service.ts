import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { ParsingProgress } from '../type/progress-bar.type';

@Injectable({
    providedIn: 'root'
  })
  export class ParsingService {
    private baseUrl = 'http://localhost:8082';
  
    constructor(private http: HttpClient) {}
  
    startParsing(): void {
      this.http.post(`${this.baseUrl}/parser/start`, {}).subscribe(
        {
            error: console.error
        }
      );
    }
  
    getProgress(): Observable<ParsingProgress> {
      return this.http.get<ParsingProgress>(`${this.baseUrl}/progress`);
    }
  }