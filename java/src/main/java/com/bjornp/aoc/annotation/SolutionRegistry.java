package com.bjornp.aoc.annotation;

import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SolutionRegistry {
    @Getter(lazy = true)
    private static final SolutionRegistry instance = new SolutionRegistry();

    private final Map<Day, Class<AdventOfCodeSolution>> solutions = new HashMap<>();

    public void runSolutionsScan() {
        var reflections = new Reflections("com.bjornp.aoc.solutions.implementations");
        var scannedSolutions = reflections.getTypesAnnotatedWith(SolutionDay.class);
        scannedSolutions.forEach(solution -> {
            if (solution.getSuperclass().equals(AdventOfCodeSolution.class)) {
                this.registerSolution(solution.getAnnotation(SolutionDay.class).day(),
                        solution.getAnnotation(SolutionDay.class).year(),
                        (Class<AdventOfCodeSolution>) solution
                );
            }
        });

        log.info("SolutionDay scan found {} solution classes", solutions.size());

        if (scannedSolutions.size() > solutions.size()) {
            log.warn("Found {} @SolutionDay classes that are not AdventOfCodeSolution",
                    scannedSolutions.size() - solutions.size()
            );
        }
    }

    private void registerSolution(int day, int year, Class<AdventOfCodeSolution> solution) {
        if (solutions.containsKey(new Day(day, year))) {
            throw new IllegalArgumentException("SolutionDay for day %d %d is already registered".formatted(day, year));
        }

        solutions.put(new Day(day, year), solution);
    }

    /**
     * Run a solution from the registry. A solution will be automatically registered when it has @Solution(day = d) and is a subclass of AdventOfCodeSolution. Each AdventOfCodeSolution can register runnable String -> String solution methods in the solutions Map, of which the name will be used in this class.
     *
     * @param day         The solution day to run
     * @param methodNames The comma-separated list of methods to run, or '*' to run all
     * @param testMode    Set to true to run with test input, false to run with that day's input
     * @throws NoSuchMethodException     If the solution has no suitable constructor
     * @throws InvocationTargetException If the solution constructor throws an exception
     * @throws InstantiationException    If the constructor of the solution is in an abstract class
     * @throws IllegalAccessException    If this Constructor object is enforcing Java language access control and the underlying constructor is inaccessible.
     * @throws IllegalArgumentException  If the day has no registered solution OR if the day has no registered method by the specified name
     */
    public void runSolution(int day, int year, String methodNames, boolean testMode) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (day != 0 && !this.solutions.containsKey(new Day(day, year))) {
            throw new IllegalArgumentException("Day %d %d has not been registered".formatted(day, year));
        }

        var start = System.nanoTime();

        if (day == 0) {
            var solutionClasses = this.solutions.values();
            for (var solutionClass : solutionClasses) {
                runSolution(solutionClass, methodNames, testMode, new Day(day, year));
            }
        } else {
            var solutionClass = this.solutions.get(new Day(day, year));
            runSolution(solutionClass, methodNames, testMode, new Day(day, year));
        }

        log.info("Complete runtime: {} ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
    }

    private void runSolution(Class<AdventOfCodeSolution> solutionClass, String methodNames, boolean testMode, Day day) throws
            NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<String> uniqueValues = new HashSet<>();
        for (String method : methodNames.split(",")) {
            if (uniqueValues.add(method)) {  // run every method just once
                AdventOfCodeSolution instance;
                try {
                    instance = solutionClass.getDeclaredConstructor(int.class, int.class).newInstance(day.day, day.year);
                } catch (NoSuchMethodException e) {
                    log.info("No int,int constructor found, falling back to legacy method");
                    instance = solutionClass.getDeclaredConstructor().newInstance();
                }
                if (!instance.getSolutions().containsKey(method)) {
                    throw new IllegalArgumentException("Day %d has no registered method '%s'".formatted(
                            instance.getDay(),
                            method
                    ));
                }
                instance.run(method, testMode);
            }
        }
    }

    @Data
    private static class Day {
        private final int day;

        private final int year;
    }
}
