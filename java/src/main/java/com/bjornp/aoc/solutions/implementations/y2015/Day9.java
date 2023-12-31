package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jgrapht.alg.tour.GreedyHeuristicTSP;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 9)
@Slf4j
public class Day9 extends AdventOfCodeSolution {
    public Day9() {
        super(9, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var graph = new DefaultUndirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        var pattern = Pattern.compile("^(?<from>[A-Za-z]+) to (?<to>[A-Za-z]+) = (?<weight>\\d+)$");

        input.lines().forEach(line -> {
            var matcher = pattern.matcher(line);

            if (matcher.matches()) {
                var from = matcher.group("from");
                var to = matcher.group("to");
                var weight = Integer.parseInt(matcher.group("weight"));

                graph.addVertex(from);
                graph.addVertex(to);
                graph.addEdge(from, to);
                graph.setEdgeWeight(from, to, weight);
            }
        });

        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        CollectionUtils.permutations(graph.vertexSet()).forEach(perm -> {
            var sum = 0;
            for (int i = 0; i < perm.size() - 1; ++i) {
                var e = graph.getEdge(perm.get(i), perm.get(i + 1));
                sum += (int) graph.getEdgeWeight(e);
            }
            if (sum < min.get()) min.set(sum);
        });

        return "%,d".formatted(min.get());
    }

    public String runSolutionB(String input) {
        var graph = new DefaultUndirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        var pattern = Pattern.compile("^(?<from>[A-Za-z]+) to (?<to>[A-Za-z]+) = (?<weight>\\d+)$");

        input.lines().forEach(line -> {
            var matcher = pattern.matcher(line);

            if (matcher.matches()) {
                var from = matcher.group("from");
                var to = matcher.group("to");
                var weight = Integer.parseInt(matcher.group("weight"));

                graph.addVertex(from);
                graph.addVertex(to);
                graph.addEdge(from, to);
                graph.setEdgeWeight(from, to, weight);
            }
        });

        AtomicInteger max = new AtomicInteger(0);
        CollectionUtils.permutations(graph.vertexSet()).forEach(perm -> {
            var sum = 0;
            for (int i = 0; i < perm.size() - 1; ++i) {
                var e = graph.getEdge(perm.get(i), perm.get(i + 1));
                sum += (int) graph.getEdgeWeight(e);
            }
            if (sum > max.get()) max.set(sum);
        });

        return "%,d".formatted(max.get());
    }
}
