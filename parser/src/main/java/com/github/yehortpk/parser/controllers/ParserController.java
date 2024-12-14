package com.github.yehortpk.parser.controllers;

import com.github.yehortpk.parser.ParserRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/parser")
public class ParserController {
    private final ParserRunner parserRunner;

    @PostMapping("/start")
    public void startParsing() {
        parserRunner.start();
    }
}
