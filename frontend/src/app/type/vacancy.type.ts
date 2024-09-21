export type Vacancy = {
    title: string
    salary: string
    url: string
    company: {
        id: number
        title: string
        imageUrl: string
    },
    filter: {
        title: string
        url: string
    } 
}

export type VacanciesListDTO = {
    vacancies: Vacancy[]
    currentPage: number
    pageSize: number
    totalVacancies: number
    totalPages: number
}