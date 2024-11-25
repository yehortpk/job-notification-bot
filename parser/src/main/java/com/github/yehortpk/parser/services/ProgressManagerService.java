package com.github.yehortpk.parser.services;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ProgressManagerService {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CLEAR_LINE = "\u001B[2K";
    private static final String ANSI_MOVE_UP = "\u001B[1A";
    private static final String ANSI_RETURN = "\r";

    private static final String DONE_MARKER = "+";
    private static final String ERROR_MARKER = "-";
    private static final String PENDING_MARKER = ".";

    private final Map<String, ProgressBar> bars = new LinkedHashMap<>();
    private final ReentrantLock renderLock = new ReentrantLock();
    private final ReentrantLock barsLock = new ReentrantLock();
    private boolean isRunning = true;
    private volatile boolean isFirstRender = true;

    public static class ProgressBar {
        final String name;
        final int totalSteps;
        final boolean[] completedSteps;
        int completedStepsCount = 0;
        final boolean[] errorSteps;
        int currentPosition;
        boolean isCompleted;

        public ProgressBar(String name, int totalSteps) {
            this.name = name;
            this.totalSteps = totalSteps;
            this.completedSteps = new boolean[totalSteps];
            this.errorSteps = new boolean[totalSteps];
            this.currentPosition = 0;
            this.isCompleted = false;
        }
    }

    public ProgressManagerService() {
        Thread renderThread = new Thread(() -> {
            while (isRunning) {
                render();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        renderThread.start();
    }

    public void addBar(String name, int totalSteps) {
        barsLock.lock();
        try {
            if (!bars.containsKey(name)) {
                bars.put(name, new ProgressBar(name, totalSteps));
                System.out.println(); // Add space for new bar
            }
        } finally {
            barsLock.unlock();
        }
    }

    public void changeBarStepsCount(String name, int totalSteps) {
        bars.replace(name, new ProgressBar(name, totalSteps));
        render();
    }

    private boolean isBarCompleted(ProgressBar bar) {
        return bar.completedStepsCount == bar.totalSteps;
    }

    private void updateBarCompletion(ProgressBar bar) {
        bar.isCompleted = isBarCompleted(bar);
        if (bar.isCompleted) {
            boolean allBarsCompleted = true;
            for (ProgressBar pBar : bars.values()) {
                if (!pBar.isCompleted) {
                    allBarsCompleted = false;
                    break;
                }
            }
            isRunning = !allBarsCompleted;
        }
    }

    public void markStepDone(String barName, int step) {
        barsLock.lock();
        try {
            ProgressBar bar = bars.get(barName);
            if (bar != null && step < bar.totalSteps && !bar.completedSteps[step]) {
                bar.completedSteps[step] = true;
                bar.completedStepsCount++;
                bar.currentPosition = Math.max(bar.currentPosition, step + 1);
                updateBarCompletion(bar);
            }
        } finally {
            barsLock.unlock();
        }
    }

    public void markStepError(String barName, int step) {
        barsLock.lock();
        try {
            ProgressBar bar = bars.get(barName);
            if (bar != null && step < bar.totalSteps && !bar.errorSteps[step]) {
                bar.errorSteps[step] = true;
                bar.completedStepsCount++;
                bar.currentPosition = Math.max(bar.currentPosition, step + 1);
                updateBarCompletion(bar);
            }
        } finally {
            barsLock.unlock();
        }
    }


    @SneakyThrows
    private void render() {
        if (!renderLock.tryLock()) {
            return; // Skip this render if another is in progress
        }

        try {
            barsLock.lock();
            try {
                if (!isFirstRender) {
                    for (int i = 0; i < bars.size(); i++) {
                        System.out.print(ANSI_MOVE_UP);
                    }
                }

                for (ProgressBar bar : bars.values()) {
                    System.out.print(ANSI_RETURN + ANSI_CLEAR_LINE);

                    String progressCount = String.format("%d/%d", bar.completedStepsCount, bar.totalSteps);
                    String nameField = String.format("%-15s", bar.name + " (" + progressCount + ")");

                    System.out.print(nameField + " [");

                    for (int i = 0; i < bar.totalSteps; i++) {
                        if (bar.completedSteps[i]) {
                            System.out.print(ANSI_GREEN + DONE_MARKER + ANSI_RESET);
                        } else if (bar.errorSteps[i]) {
                            System.out.print(ANSI_RED + ERROR_MARKER + ANSI_RESET);
                        } else {
                            System.out.print(PENDING_MARKER);
                        }
                    }

                    int percentage = (int) ((bar.completedStepsCount * 100.0) / bar.totalSteps);
                    System.out.printf("] %3d%%\n", percentage);
                }

                System.out.flush();
                isFirstRender = false;
            } finally {
                barsLock.unlock();
            }
        } finally {
            renderLock.unlock();
        }
    }
}
