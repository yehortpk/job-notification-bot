package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.vacancy.VacancyShortDTO;
import com.github.yehortpk.router.services.FilterService;
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
    private final FilterService filterService;

    @GetMapping("/chat/{chat_id}")
    public List<FilterDTO> getFilters(
            @PathVariable("chat_id") int chatId
    ) {
        return filterService.getFilters(chatId)
                .stream().map((filter) -> modelMapper.map(filter, FilterDTO.class)).toList();
    }

    @GetMapping("/{filter_id}")
    public FilterDTO getFilter(@PathVariable("filter_id") long filterId) {
        Filter filter = filterService.findById(filterId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "Filter not found"));
        return modelMapper.map(filter, FilterDTO.class);
    }

    @PostMapping
    public void addFilter(@RequestBody FilterDTO filter) {
        filterService.addFilter(filter);
    }

    @DeleteMapping("/{filter_id}")
    public void deleteFilter(@PathVariable("filter_id") long filterId) {
        filterService.deleteFilter(filterId);
    }

    @GetMapping("/{filter_id}/vacancies")
    public List<VacancyShortDTO> getVacanciesByFilter(@PathVariable("filter_id") long filterId) {
        return filterService.getVacanciesByFilter(filterId).stream().map(vacancy ->
                modelMapper.map(vacancy, VacancyShortDTO.class)).toList();
    }
}
