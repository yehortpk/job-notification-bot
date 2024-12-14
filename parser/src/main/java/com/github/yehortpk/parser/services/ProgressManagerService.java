package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.ParsingProgressDTO;
import com.github.yehortpk.parser.models.ProgressStepEnum;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class ProgressManagerService {
    private final Map<Integer, ProgressBar> bars = new LinkedHashMap<>();
    private final ReentrantLock barsLock = new ReentrantLock();

    public static class ProgressBar {
        final int id;
        final ProgressStepEnum[] steps;
        final int totalSteps;
        int currentPosition;

        public ProgressBar(int id, int totalSteps) {
            this.id = id;
            this.totalSteps = totalSteps;
            this.steps = new ProgressStepEnum[totalSteps];
            Arrays.fill(steps, ProgressStepEnum.STEP_UNKNOWN);
            this.currentPosition = 0;
        }
    }

    public void addBar(int id, int totalSteps) {
        barsLock.lock();
        try {
            if (!bars.containsKey(id)) {
                bars.put(id, new ProgressBar(id, totalSteps));// Add space for new bar
            }
        } finally {
            barsLock.unlock();
        }
    }

    public void changeBarStepsCount(int id, int totalSteps) {
        bars.replace(id, new ProgressBar(id, totalSteps));
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
        ParsingProgressDTO parsingProgressDTO = new ParsingProgressDTO();

        for (Map.Entry<Integer, ProgressBar> stringProgressBarEntry : bars.entrySet()) {
            parsingProgressDTO.addParserProgress(
                stringProgressBarEntry.getKey(),
                Arrays.stream(stringProgressBarEntry.getValue().steps).map(
                        (ps) -> ps == null? ProgressStepEnum.STEP_UNKNOWN.getValue() : ps.getValue()
                ).toList()
            );
        }

        parsingProgressDTO.setFinished(isParsingCompleted());

        return parsingProgressDTO;
    }
}
