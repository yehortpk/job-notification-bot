package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.vacancy.VacanciesPageDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.services.VacancyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;
    private final ModelMapper modelMapper;

    @PostMapping
    public void addVacancies(@RequestBody List<VacancyDTO> vacancies) {
        vacancyService.addVacancies(vacancies);
    }

    @DeleteMapping("/{id}")
    public void removeVacancy(@PathVariable Long id) {
        vacancyService.removeVacancy(id);
    }

    @DeleteMapping
    public void removeVacancies(@RequestBody List<String> urls) {
        vacancyService.removeVacanciesByUrlsIn(urls);
    }

    @GetMapping
    @CrossOrigin("http://127.0.0.1:4200")
    public VacanciesPageDTO getVacanciesByPage(@RequestParam(value = "page") int pageId,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize){

        Page<Vacancy> vacanciesOnPage = vacancyService.getVacanciesByPage(pageId, pageSize);
        return modelMapper.getTypeMap(Page.class, VacanciesPageDTO.class).map(vacanciesOnPage);
    }

    @GetMapping(params = "byCompany=true")
    public Map<Long, Set<String>> getPersistedVacanciesIdsByCompanyId() {
        return vacancyService.getAllVacancies().stream().collect(Collectors.groupingBy(
                (vacancy -> (long) vacancy.getCompany().getCompanyId()),
                Collectors.mapping(Vacancy::getLink, Collectors.toSet())
        ));
    }
}
