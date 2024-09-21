import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { VacanciesListDTO, Vacancy } from '../type/vacancy.type';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class VacancyService {
  constructor(private http: HttpClient) { }

  getVacancies(): VacanciesListDTO {
    return {
      vacancies: [
        {
          title: "Lead Data Engineer (Data Modeling)",
          salary: "Not specified",
          url: "/vacancy/1",
          company: {
            id: 1,
            title: "Company A",
            imageUrl: "/img/dummyA.jpg"
          },
          filter: {
            title: "Java, -senior -lead -qa -automation -datascience -bigdata -android -kotlin -principal -consult -trainee -intern -devops -architect",
            url: "/filter/1"
          } 
        },
        {
          title: "Senior Data Engineer (Data Modeling)",
          salary: "Not specified",
          url: "/vacancy/2",
          company: {
            id: 1,
            title: "Company A",
            imageUrl: "/img/dummyA.jpg"
          },
          filter: {
            title: "Java, -senior -lead -qa -automation -datascience -bigdata -android -kotlin -principal -consult -trainee -intern -devops -architect",
            url: "/filter/2"
          } 
        },
        {
          title: "Middle Data Engineer (Data Modeling)",
          salary: "Not specified",
          url: "/vacancy/3",
          company: {
            id: 2,
            title: "Company B",
            imageUrl: "/img/dummyB.jpg"
          },
          filter: {
            title: "software (developer|engineer) -senior -lead -qa -automation -datascience -bigdata -android -kotlin -principal -consult -trainee -intern -devops -architect",
            url: "/filter/3"
          }
        },
        {
          title: "Lead Data Engineer (Data Modeling)",
          salary: "Not specified",
          url: "/vacancy/4",
          company: {
            id: 2,
            title: "Company B",
            imageUrl: "/img/dummyB.jpg"
          },
          filter: {
            title: "",
            url: "/filter/4"
          }
        },
        {
          title: "Junior Java Engineer (Data Modeling)",
          salary: "850$",
          url: "/vacancy/5",
          company: {
            id: 3,
            title: "Company C",
            imageUrl: "/img/dummyC.jpg"
          },
          filter: {
            title: "Java, -senior -lead -qa -automation -datascience -bigdata -android -kotlin -principal -consult -trainee -intern -devops -architect",
            url: "/filter/5"
          } 
        }
      ],
      currentPage: 5,
      pageSize: 10,
      totalVacancies: 100,
      totalPages: 10
    }
  }

  getVacanciesRemote(): Observable<VacanciesListDTO> {
    const routerServiceURL: string|undefined = process.env['ROUTER_SERVICE_URL']
    if (!routerServiceURL) {
      throw new Error('Missing required environment variable: ROUTER_SERVICE_URL');
    }
    return this.http.get<VacanciesListDTO>(routerServiceURL);
  }
}
