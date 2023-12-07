package com.bjornp.aoc2023.solutions;

import com.bjornp.aoc2023.LogUtils;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SuperBuilder
@Getter
public abstract class AdventOfCodeSolution {
    private final int day;

    private final AdventOfCodeInputReader inputReader;

    private final Map<String, Function<String, String>> solutions;

    public AdventOfCodeSolution(int day) {
        inputReader = new AdventOfCodeInputReader(day);
        this.day = day;
        this.solutions = new HashMap<>();
    }

    protected void register(String name, Function<String, String> function) {
        solutions.put(name, function);
    }

    public void run(String name, boolean testmode) {
        var log = LoggerFactory.getLogger(this.getClass());

        var inputString = testmode ? inputReader.readInput("test.txt") : inputReader.readInput();

        LogUtils.prettyInfo("Running solution %d %s".formatted(day, name), log);

        long start = System.nanoTime();
        var solution = this.solutions.get(name).apply(inputString);
        long end = System.nanoTime();

        log.info("Solution %d %s (in %,d ms) :: %s".formatted(day, name, TimeUnit.NANOSECONDS.toMillis(end - start), solution));
    }
}
