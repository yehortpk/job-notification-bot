package com.github.yehortpk.parser.controllers;

import com.github.yehortpk.parser.services.ParserRunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/parser")
public class ParserController {
    private final ParserRunnerService parserRunnerService;

    @CrossOrigin("http://localhost:4200")
    @PostMapping("/start")
    public String startParsing() {
        parserRunnerService.runParsers();
        return "Parsing has been started";
    }
}
