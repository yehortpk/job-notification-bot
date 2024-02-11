package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.CompanyShortInfoDTO;
import com.github.yehortpk.router.models.SubscriptionDTO;
import com.github.yehortpk.router.services.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    @Autowired
    private SubscribeService subscribeService;

    @GetMapping("/{chat_id}")
    public List<CompanyShortInfoDTO> getSubscriptions(@PathVariable("chat_id") long chatId) {
        return subscribeService.getSubscriptions(chatId);
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