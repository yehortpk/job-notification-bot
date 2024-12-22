package com.github.yehortpk.parser.controllers;

import com.github.yehortpk.parser.services.ParserRunnerService;
import com.github.yehortpk.parser.exceptions.ParsingAlreadyStartedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> startParsing() {
        try {
            parserRunnerService.runParsers();
            return ResponseEntity.ok("Parsing has been started");
        } catch (ParsingAlreadyStartedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
