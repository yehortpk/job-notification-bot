export interface StepStatus {
    status: 'pending' | 'done' | 'error';
    label?: string;
}

export interface ParserProgress {
    parserID: number
    parserTitle: string
    steps: number[]
}

export interface ParsingProgress {
    parsers: ParserProgress[]
    finished: boolean
    total: number
    new: number
    outdated: number
}