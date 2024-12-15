package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.ParsingProgressDTO;
import com.github.yehortpk.parser.models.ProgressStepEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ProgressManagerService {
    @Getter
    private final Map<Integer, ProgressBar> bars = new LinkedHashMap<>();
    private final ReentrantLock barsLock = new ReentrantLock();

    @Setter
    private int parsedVacanciesCnt = 0;
    @Setter
    private int newVacanciesCnt = 0;
    @Setter
    private int outdatedVacanciesCnt = 0;

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

    private boolean isParsingCompleted() {
        if (bars.isEmpty()) {
            return false;
        }
        return bars.values().stream().allMatch((pb)->pb.currentPosition == pb.totalSteps);
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
                .finished(isParsingCompleted())
                .parsedVacanciesCnt(parsedVacanciesCnt)
                .newVacanciesCnt(newVacanciesCnt)
                .outdatedVacanciesCnt(outdatedVacanciesCnt)
                .build();
    }
}
