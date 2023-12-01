package com.bjornp.aoc2023.annotation;

import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SolutionRegistry {
    @Getter(lazy = true)
    private static final SolutionRegistry instance = new SolutionRegistry();

    private final HashMap<Integer, Class<AdventOfCodeSolution>> solutions = new HashMap<>();

    private void registerSolution(int day, Class<AdventOfCodeSolution> solution) {
        if (solutions.containsKey(day)) {
            throw new IllegalArgumentException("Solution for day %d is already registered".formatted(day));
        }

        solutions.put(day, solution);
    }

    public void runSolutionsScan() {
        var reflections = new Reflections("com.bjornp.aoc2023");
        var scannedSolutions = reflections.getTypesAnnotatedWith(Solution.class);
        scannedSolutions.forEach(solution -> {
            if (solution.getSuperclass().equals(AdventOfCodeSolution.class)) {
                this.registerSolution(
                        solution.getAnnotation(Solution.class).day(),
                        (Class<AdventOfCodeSolution>) solution
                );
            }
        });

        log.info("Solution scan found {} solution classes, days: {}", solutions.size(), solutions.keySet());

        if (scannedSolutions.size() > solutions.size()) {
            log.warn("Found {} @Solution classes that are not AdventOfCodeSolution", scannedSolutions.size() - solutions.size());
        }
    }

    public void runSolution(int day, AdventOfCodeSolution.RunType runType) throws NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        if (!this.solutions.containsKey(day)) {
            throw new IllegalArgumentException("Day %d has not been registered".formatted(day));
        }

        var solutionClass = this.solutions.get(day);
        solutionClass.getConstructor().newInstance().run(runType);
    }
}
