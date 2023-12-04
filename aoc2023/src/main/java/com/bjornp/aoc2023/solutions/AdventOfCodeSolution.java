package com.bjornp.aoc2023.solutions;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuperBuilder
public abstract class AdventOfCodeSolution {
    private final int day;

    private final AdventOfCodeInputReader inputReader;

    public AdventOfCodeSolution(int day) {
        inputReader = new AdventOfCodeInputReader(day);
        this.day = day;
    }

    protected abstract String runSolutionA(String input);

    protected abstract String runSolutionB(String input);

    public void run(RunType runType, boolean testmode) {
        var inputString = testmode ? inputReader.readInput("test.txt") : inputReader.readInput();

        if (runType == RunType.FIRST || runType == RunType.BOTH) {
            log.info("Running solution %dA".formatted(day));
            log.info("Solution %dA :: %s".formatted(day, runSolutionA(inputString)));
        }

        if (runType == RunType.SECOND || runType == RunType.BOTH) {
            log.info("Running solution %dB".formatted(day));
            log.info("Solution %dB :: %s".formatted(day, runSolutionB(inputString)));
        }
    }

    public enum RunType {
        FIRST,
        SECOND,
        BOTH
    }
}
