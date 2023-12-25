package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.GusfieldGomoryHuCutTree;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.CollectionUtil;

import java.sql.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SolutionDay(day = 25, year = 2023)
@Slf4j
public class Day25 extends AdventOfCodeSolution {
    private final Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

    public Day25() {
        super(25, 2023);

        register("a", this::runSolutionA);
    }

    public String runSolutionA(String input) {
        input.lines().forEach(line -> {
            var left = line.split(":")[0];
            var right = line.split(":")[1].trim().split(" ");
            for (String dest : right) {
                graph.addVertex(left);
                graph.addVertex(dest);
                graph.addEdge(left, dest);
            }
        });

        MinimumSTCutAlgorithm<String, DefaultEdge> minCut = new GusfieldGomoryHuCutTree<>(graph);
        while (true) {
            var vertices = new ArrayList<>(graph.vertexSet());
            Collections.shuffle(vertices);

            var minCutResult = minCut.calculateMinCut(vertices.get(0), vertices.get(1));

            if (minCutResult == 3) break;
        }

        return "%,d".formatted(minCut.getSinkPartition().size() * minCut.getSourcePartition().size());
    }
}
