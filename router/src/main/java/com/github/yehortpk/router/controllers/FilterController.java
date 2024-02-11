package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.FilterShortDTO;
import com.github.yehortpk.router.models.FilterShortInfoDTO;
import com.github.yehortpk.router.models.VacancyShortDTO;
import com.github.yehortpk.router.services.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filter")
public class FilterController {
    @Autowired
    SubscribeService subscribeService;

    @GetMapping("/{chat_id}/{company_id}")
    public List<FilterShortInfoDTO> getFilters(
            @PathVariable("chat_id") int chatId,
            @PathVariable("company_id") int companyId
    ) {
        return subscribeService.getFilters(chatId, companyId);
    }

    @GetMapping("/{filter_id}")
    public FilterShortInfoDTO getFilter(@PathVariable("filter_id") long filterId) {
        return subscribeService.getFilter(filterId);
    }

    @PostMapping
    public void addFilter(@RequestBody FilterShortDTO filter) {
        subscribeService.addFilter(filter);
    }

    @DeleteMapping("/{filter_id}")
    public void deleteFilter(@PathVariable("filter_id") long filterId) {
        subscribeService.deleteFilter(filterId);
    }

    @GetMapping("/vacancies/{filter_id}")
    public List<VacancyShortDTO> getVacanciesByFilter(@PathVariable("filter_id") long filterId) {
        return subscribeService.getVacanciesByFilter(filterId);
    }
}
