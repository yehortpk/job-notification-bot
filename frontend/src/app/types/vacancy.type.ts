export type Vacancy = {
    title: string
    salary: string
    url: string
    company: {
        id: number
        title: string
        image_url: string
    },
    filter: {
        title: string
        url: string
    } 
}