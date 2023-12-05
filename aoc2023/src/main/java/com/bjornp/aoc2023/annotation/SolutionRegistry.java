package com.bjornp.aoc2023.annotation;

import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SolutionRegistry {
    @Getter(lazy = true)
    private static final SolutionRegistry instance = new SolutionRegistry();

    private final HashMap<Integer, Class<AdventOfCodeSolution>> solutions = new HashMap<>();

    public void runSolutionsScan() {
        var reflections = new Reflections("com.bjornp.aoc2023");
        var scannedSolutions = reflections.getTypesAnnotatedWith(SolutionDay.class);
        scannedSolutions.forEach(solution -> {
            if (solution.getSuperclass().equals(AdventOfCodeSolution.class)) {
                this.registerSolution(solution.getAnnotation(SolutionDay.class).day(),
                        (Class<AdventOfCodeSolution>) solution
                );
            }
        });

        log.info("SolutionDay scan found {} solution classes, days: {}", solutions.size(), solutions.keySet());

        if (scannedSolutions.size() > solutions.size()) {
            log.warn(
                    "Found {} @SolutionDay classes that are not AdventOfCodeSolution",
                    scannedSolutions.size() - solutions.size()
            );
        }
    }

    private void registerSolution(int day, Class<AdventOfCodeSolution> solution) {
        if (solutions.containsKey(day)) {
            throw new IllegalArgumentException("SolutionDay for day %d is already registered".formatted(day));
        }

        solutions.put(day, solution);
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
    public void runSolution(int day, String methodNames, boolean testMode) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!this.solutions.containsKey(day)) {
            throw new IllegalArgumentException("Day %d has not been registered".formatted(day));
        }

        var solutionClass = this.solutions.get(day);
        if (methodNames.equals("*")) {
            var solutionInstance = solutionClass.getConstructor().newInstance();
            for (Map.Entry<String, Function<String, String>> solution : solutionInstance.getSolutions().entrySet()) {
                solutionInstance.run(solution.getKey(), testMode);
            }
        } else {
            Set<String> uniqueValues = new HashSet<>();
            for (String method : methodNames.split(",")) {
                if (uniqueValues.add(method)) {  // run every method just once
                    var instance = solutionClass.getConstructor().newInstance();
                    if (!instance.getSolutions().containsKey(method)) {
                        throw new IllegalArgumentException("Day %d has no registered method '%s'".formatted(day, method));
                    }
                    instance.run(method, testMode);
                }
            }
        }
    }
}
