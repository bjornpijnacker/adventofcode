package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SolutionDay(year = 2024, day = 23)
@Slf4j
public class Day23 extends AdventOfCodeSolution {

    public Day23(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var graph = new DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        input.lines().forEach(line -> {
            var l = line.split("-")[0];
            var r = line.split("-")[1];
            graph.addVertex(l);
            graph.addVertex(r);
            graph.addEdge(l, r);
        });

        var triples = new HashSet<Set<String>>();
        for (var vertex : graph.vertexSet()) {
            for (var edge1 : graph.edgesOf(vertex)) {
                for (var edge2 : graph.edgesOf(vertex)) {
                    var neighbor1 = graph.getEdgeSource(edge1).equals(vertex) ? graph.getEdgeTarget(edge1) : graph.getEdgeSource(edge1);
                    var neighbor2 = graph.getEdgeSource(edge2).equals(vertex) ? graph.getEdgeTarget(edge2) : graph.getEdgeSource(edge2);

                    if (graph.getEdge(neighbor1, neighbor2) != null) {
                        triples.add(Set.of(vertex, neighbor1, neighbor2));
                    }
                }
            }
        }

        var tTriples = triples.stream()
                .filter(triple -> triple.stream().anyMatch(t -> t.startsWith("t")))
                .toList();

        log.info(String.valueOf(tTriples));

        return "%d".formatted(tTriples.size());
    }

    protected String runSolutionB(String input) {
        var graph = new DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        input.lines().forEach(line -> {
            var l = line.split("-")[0];
            var r = line.split("-")[1];
            graph.addVertex(l);
            graph.addVertex(r);
            graph.addEdge(l, r);
        });

        var triples = new HashSet<String>();
        for (var vertex : graph.vertexSet()) {
            for (var edge1 : graph.edgesOf(vertex)) {
                var neighbors = new HashSet<String>();
                var neighbor1 = graph.getEdgeSource(edge1).equals(vertex) ? graph.getEdgeTarget(edge1) : graph.getEdgeSource(edge1);
                neighbors.add(neighbor1);
                neighbors.add(vertex);

                for (var edge2 : graph.edgesOf(vertex)) {
                    if (edge1.equals(edge2)) continue;
                    var neighbor2 = graph.getEdgeSource(edge2).equals(vertex) ? graph.getEdgeTarget(edge2) : graph.getEdgeSource(edge2);
                    if (neighbors.stream().allMatch(neighbor -> graph.getEdge(neighbor, neighbor2) != null)) {
                        neighbors.add(neighbor2);
                    }
                }

                if (neighbors.size() > triples.size()) triples = neighbors;
            }
        }

        return triples.stream().toList().stream().sorted(Comparator.naturalOrder()).collect(Collectors.joining(","));
    }
}
