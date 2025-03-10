export interface PageLog {
    page_id: number,
    message: string
}

export interface PageProgress {
    id: number,
    status: number,
    parsedVacanciesCnt: number
    logs: PageLog[]
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
    outdated: number
}
