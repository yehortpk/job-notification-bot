package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.ParsingProgressDTO;
import com.github.yehortpk.parser.models.ProgressStepEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service("progressManagerService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProgressManagerService {
    @Getter
    private Map<Integer, ProgressBar> bars;
    private ReentrantLock barsLock;
    private final ReentrantLock progressLock = new ReentrantLock();

    @Setter
    private int parsedVacanciesCnt;
    @Setter
    private int newVacanciesCnt;
    @Setter
    private int outdatedVacanciesCnt;
    @Setter
    private boolean finished;

    public static class ProgressBar {
        int id;
        String title;
        ProgressStepEnum[] steps;
        int totalSteps;
        int currentPosition;

        public ProgressBar(int id, String title, int totalSteps) {
            this.id = id;
            this.title = title;
            this.totalSteps = totalSteps;
            this.steps = new ProgressStepEnum[totalSteps];
            Arrays.fill(steps, ProgressStepEnum.STEP_PENDING);
            this.currentPosition = 0;
        }
    }

    public ProgressManagerService() {
        initialize();
    }

    public void initialize() {
        progressLock.lock();
        this.parsedVacanciesCnt = 0;
        this.newVacanciesCnt = 0;
        this.outdatedVacanciesCnt = 0;
        this.finished = false;

        this.bars = new LinkedHashMap<>();
        this.barsLock = new ReentrantLock();
        progressLock.unlock();
    }

    public void addBar(int id, String title, int totalSteps) {
        barsLock.lock();
        try {
            if (!bars.containsKey(id)) {
                bars.put(id, new ProgressBar(id, title, totalSteps));// Add space for new bar
            }
        } finally {
            barsLock.unlock();
        }
    }

    public void changeBarStepsCount(int id, int totalSteps) {
        ProgressBar progressBar = bars.get(id);
        progressBar.totalSteps = totalSteps;
        progressBar.steps = new ProgressStepEnum[totalSteps];
        bars.replace(id, progressBar);
    }

    public void markStepDone(int id, int step) {
        barsLock.lock();
        try {
            ProgressBar bar = bars.get(id);
            if (bar != null && step < bar.totalSteps) {
                bar.steps[step] = ProgressStepEnum.STEP_DONE;
                bar.currentPosition = Math.max(bar.currentPosition, step + 1);
            }
        } finally {
            barsLock.unlock();
        }
    }

    public void markStepError(int id, int step) {
        barsLock.lock();
        try {
            ProgressBar bar = bars.get(id);
            if (bar != null && step < bar.totalSteps) {
                bar.steps[step] = ProgressStepEnum.STEP_ERROR;
                bar.currentPosition = Math.max(bar.currentPosition, step + 1);
            }
        } finally {
            barsLock.unlock();
        }
    }

    public ParsingProgressDTO getProgress() {
        List<ParsingProgressDTO.ParserProgress> parsers = new ArrayList<>();
        for (Map.Entry<Integer, ProgressBar> stringProgressBarEntry : bars.entrySet()) {
            ProgressBar value = stringProgressBarEntry.getValue();
            parsers.add(new ParsingProgressDTO.ParserProgress(
                stringProgressBarEntry.getKey(),
                value.title,
                Arrays.stream(value.steps).map(
                        (ps) -> ps == null? ProgressStepEnum.STEP_PENDING.getValue() : ps.getValue()
                ).toList()
            ));
        }

        return ParsingProgressDTO.builder()
                .parsers(parsers)
                .finished(finished)
                .parsedVacanciesCnt(parsedVacanciesCnt)
                .newVacanciesCnt(newVacanciesCnt)
                .outdatedVacanciesCnt(outdatedVacanciesCnt)
                .build();
    }
}
