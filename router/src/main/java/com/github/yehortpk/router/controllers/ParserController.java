package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.parser.ParsingProgressDTO;
import com.github.yehortpk.router.models.response.APIResponse;
import com.github.yehortpk.router.services.ParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parser")
public class ParserController {
    private final RestTemplate restTemplate;
    private final ParsingService parsingService;

    @Value("${parser-service-url}")
    private String parserServiceURL;

    @PostMapping("/start")
    public APIResponse startParsing() {
        return restTemplate.postForEntity(parserServiceURL + "/parser/start", null, APIResponse.class).getBody();
    }

    @GetMapping("/progress")
    public ParsingProgressDTO getParsingProgress() {
        ParsingProgressDTO progress = restTemplate.getForEntity(parserServiceURL + "/parser/progress", ParsingProgressDTO.class).getBody();
        if (progress != null && progress.isFinished()) {
            if (parsingService.findByParsingHash(progress.getParsingHash()).isEmpty()) {
                parsingService.saveParsingProgress(progress);
            }
        }
        return progress;
    }

    @PostMapping("/progress")
    public void saveParsingProgress(@RequestBody ParsingProgressDTO parsingProgress) {
        parsingService.saveParsingProgress(parsingProgress);
    }
}
