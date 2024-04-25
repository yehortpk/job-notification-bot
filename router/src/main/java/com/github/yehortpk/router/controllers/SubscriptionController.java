package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.company.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.subscription.SubscriptionDTO;
import com.github.yehortpk.router.services.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling subscriptions requests
 */
@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final ModelMapper modelMapper;
    private final SubscribeService subscribeService;

    @GetMapping("/{chat_id}")
    public List<CompanyShortInfoDTO> getSubscriptions(@PathVariable("chat_id") long chatId) {
        return subscribeService.getSubscriptions(chatId).stream().map((company) ->
                modelMapper.map(company, CompanyShortInfoDTO.class)).toList();
    }

    @PostMapping
    public void addSubscriptions(@RequestBody SubscriptionDTO subscription) {
        subscribeService.addSubscription(subscription);
    }

    @DeleteMapping("/{chat_id}/{company_id}")
    public void removeSubscription(@PathVariable("chat_id") long chatId, @PathVariable("company_id") long companyId) {
        subscribeService.deleteSubscription(chatId, companyId);
    }

}