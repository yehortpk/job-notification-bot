package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.vacancy.VacanciesPageDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.services.VacancyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;
    private final ModelMapper modelMapper;

    @DeleteMapping("/{id}")
    public void removeVacancy(@PathVariable Long id) {
        vacancyService.removeVacancy(id);
    }

    @GetMapping
    @CrossOrigin("http://127.0.0.1:4200")
    public VacanciesPageDTO getVacanciesByPage(@RequestParam(value = "page") int pageId,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize){

        Page<Vacancy> vacanciesOnPage = vacancyService.getVacanciesByPage(pageId, pageSize);
        return modelMapper.getTypeMap(Page.class, VacanciesPageDTO.class).map(vacanciesOnPage);
    }
}
