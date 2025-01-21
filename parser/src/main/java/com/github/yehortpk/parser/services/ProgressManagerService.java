package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.ParserProgress;
import com.github.yehortpk.parser.models.ParsingProgressDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ProgressManagerService {
    private String parsingHash;
    @Getter
    private Map<Integer, ParserProgress> parsers;
    private ReentrantLock parsersLock;
    private final ReentrantLock progressLock = new ReentrantLock();

    @Setter
    @Getter
    private int parsedVacanciesCnt;
    @Setter
    @Getter
    private int newVacanciesCnt;
    @Setter
    @Getter
    private boolean finished;

    public ProgressManagerService() {
        init();
    }



    public void init() {
        progressLock.lock();
        this.parsingHash = UUID.randomUUID().toString().substring(0, 10);
        this.parsedVacanciesCnt = 0;
        this.newVacanciesCnt = 0;
        this.finished = false;

        this.parsers = new LinkedHashMap<>();
        this.parsersLock = new ReentrantLock();
        progressLock.unlock();
    }

    public ParserProgress addParserProgress(int id, String title) {
        parsersLock.lock();
        try {
            ParserProgress newPB = new ParserProgress(id, title);
            if (!parsers.containsKey(id)) {
                parsers.put(id, newPB);
                return newPB;
            } else {
                throw new RuntimeException("Progress bar with this id is already exist");
            }
        } finally {
            parsersLock.unlock();
        }
    }

    public ParsingProgressDTO getProgress() {
        List<ParserProgress> parsers = new ArrayList<>(this.parsers.values());
        parsers.sort(Comparator.comparingInt(ParserProgress::getId));

        return ParsingProgressDTO.builder()
                .parsingHash(parsingHash)
                .parsers(parsers)
                .finished(finished)
                .parsedVacanciesTotalCount(parsedVacanciesCnt)
                .newVacanciesTotalCount(newVacanciesCnt)
                .build();
    }
}
