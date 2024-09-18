import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VacancyService {
  getVacancies = () => {
    return [
      {
        title: "Lead Data Engineer (Data Modeling)",
        salary: "Not specified",
        url: "/vacancy/1",
        company: {
          id: 1,
          title: "Company A",
          image_url: "/img/dummyA.jpg"
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
          image_url: "/img/dummyA.jpg"
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
          image_url: "/img/dummyB.jpg"
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
          image_url: "/img/dummyB.jpg"
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
          image_url: "/img/dummyC.jpg"
        },
        filter: {
          title: "Java, -senior -lead -qa -automation -datascience -bigdata -android -kotlin -principal -consult -trainee -intern -devops -architect",
          url: "/filter/5"
        } 
      }
    ];
  }
}
