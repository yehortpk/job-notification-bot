export type Vacancy = {
    title: string
    minSalary: number|string
    maxSalary: number|string
    url: string
    company: {
        company_id: number
        title: string
        imageUrl: string|null
        company_url: string
    }
}

export type VacanciesListDTO = {
    vacancies: Vacancy[]
    currentPage: number
    pageSize: number
    totalVacancies: number
    totalPages: number
}
