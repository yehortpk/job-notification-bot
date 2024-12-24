package com.github.yehortpk.subscriberbot.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VacanciesPageDTO {
    private List<VacancyCompanyDTO> vacancies;
    private int currentPage;
    private int pageSize;
    private int totalVacancies;
    private int totalPages;
}
