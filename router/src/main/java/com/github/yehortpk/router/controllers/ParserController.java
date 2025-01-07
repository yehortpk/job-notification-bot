package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.parser.ParsingProgressDTO;
import com.github.yehortpk.router.models.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parser")
public class ParserController {
    private final RestTemplate restTemplate;

    @Value("${parser-service-url}")
    private String parserServiceURL;

    @PostMapping("/start")
    public APIResponse startParsing() {
        return restTemplate.postForEntity(parserServiceURL + "/parser/start", null, APIResponse.class).getBody();
    }

    @GetMapping("/progress")
    public ParsingProgressDTO getParsingProgress() {
        return restTemplate.getForEntity(parserServiceURL + "/progress", ParsingProgressDTO.class).getBody();
    }
}
