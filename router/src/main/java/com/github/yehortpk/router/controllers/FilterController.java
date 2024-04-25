package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.filter.FilterShortInfoDTO;
import com.github.yehortpk.router.models.vacancy.VacancyShortDTO;
import com.github.yehortpk.router.services.FilterService;
import com.github.yehortpk.router.services.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Controller responsible for handling filter related requests
 */
@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {
    private final ModelMapper modelMapper;
    private final SubscribeService subscribeService;
    private final FilterService filterService;

    @GetMapping("/{chat_id}/{company_id}")
    public List<FilterShortInfoDTO> getFilters(
            @PathVariable("chat_id") int chatId,
            @PathVariable("company_id") int companyId
    ) {
        return subscribeService.getFilters(chatId, companyId)
                .stream().map((filter) -> modelMapper.map(filter, FilterShortInfoDTO.class)).toList();
    }

    @GetMapping("/{filter_id}")
    public FilterShortInfoDTO getFilter(@PathVariable("filter_id") long filterId) {
        Filter filter = filterService.findById(filterId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "Filter not found"));
        return modelMapper.map(filter, FilterShortInfoDTO.class);
    }

    @PostMapping
    public void addFilter(@RequestBody FilterDTO filter) {
        subscribeService.addFilter(filter);
    }

    @DeleteMapping("/{filter_id}")
    public void deleteFilter(@PathVariable("filter_id") long filterId) {
        subscribeService.deleteFilter(filterId);
    }

    @GetMapping("/vacancies/{filter_id}")
    public List<VacancyShortDTO> getVacanciesByFilter(@PathVariable("filter_id") long filterId) {
        return subscribeService.getVacanciesByFilter(filterId).stream().map(vacancy ->
                modelMapper.map(vacancy, VacancyShortDTO.class)).toList();
    }
}
