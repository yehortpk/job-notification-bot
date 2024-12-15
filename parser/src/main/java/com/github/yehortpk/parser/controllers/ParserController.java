package com.github.yehortpk.parser.controllers;

import com.github.yehortpk.parser.ParserRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/parser")
public class ParserController {
    private final ParserRunner parserRunner;

    @PostMapping("/start")
    public ResponseEntity<String> startParsing() {
        if (!parserRunner.isAlive()) {
            parserRunner.start();
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Parsing has been started.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Parsing has already been started.");
        }
    }
}
