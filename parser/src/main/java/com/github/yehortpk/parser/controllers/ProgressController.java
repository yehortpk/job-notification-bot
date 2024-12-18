package com.github.yehortpk.parser.controllers;

import com.github.yehortpk.parser.models.ParsingProgressDTO;
import com.github.yehortpk.parser.services.ProgressManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/progress")
public class ProgressController {
    private final ProgressManagerService progressManagerService;

    @GetMapping
    @CrossOrigin("http://localhost:4200")
    public ParsingProgressDTO getProgress() {
        return progressManagerService.getProgress();
    }
}
