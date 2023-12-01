package com.bjornp.aoc2023.solutions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AdventOfCodeSolution {
    private final AdventOfCodeInputReader inputReader;
    private final int day;

    public AdventOfCodeSolution(int day) {
        inputReader = new AdventOfCodeInputReader(day);
        this.day = day;
    }

    protected abstract String runSolutionA(String input);

    protected abstract String runSolutionB(String input);

    public void run(RunType runType) {
        if (runType == RunType.ONLY_FIRST || runType == RunType.BOTH) {
            log.info("Running solution %dA".formatted(day));
            log.info("Solution %dA :: %s".formatted(day, runSolutionA(inputReader.readInputA())));
        }

        if (runType == RunType.ONLY_SECOND || runType == RunType.BOTH) {
            log.info("Running solution %dB".formatted(day));
            log.info("Solution %dB :: %s".formatted(day, runSolutionB(inputReader.readInputB())));
        }
    }

    public enum RunType {
        ONLY_FIRST, ONLY_SECOND, BOTH
    }
}
