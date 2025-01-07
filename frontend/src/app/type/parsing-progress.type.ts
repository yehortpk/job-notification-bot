export interface PageProgress {
    id: number,
    status: number,
    parsedVacanciesCnt: number
}

export interface ParserProgress {
    id: number
    title: string
    pages: PageProgress[]
}

export interface ParsingProgress {
    parsers: ParserProgress[]
    finished: boolean
    total: number
    new: number
}