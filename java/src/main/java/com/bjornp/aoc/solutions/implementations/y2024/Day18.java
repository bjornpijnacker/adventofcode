package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.IntVector2D;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.List;

@SolutionDay(year = 2024, day = 18)
@Slf4j
public class Day18 extends AdventOfCodeSolution {
    private final static int WIDTH = 71;

    private final static int HEIGHT = 71;

    public Day18(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var graph = getGraph();

        input.lines().toList().subList(0, 1024).forEach(b -> {
            int bx = Integer.parseInt(b.split(",")[0]);
            int by = Integer.parseInt(b.split(",")[1]);
            var bc = new IntVector2D(bx, by);
            graph.removeVertex(bc);
        });

        var dsp = new DijkstraShortestPath<>(graph);
        var shortestPath = dsp.getPath(new IntVector2D(0, 0), new IntVector2D(WIDTH - 1, HEIGHT - 1));

        return "%d".formatted(shortestPath.getLength());
    }

    protected String runSolutionB(String input) {
        var coords = input.lines().map(b -> {
            int bx = Integer.parseInt(b.split(",")[0]);
            int by = Integer.parseInt(b.split(",")[1]);
            return new IntVector2D(bx, by);
        }).toList();

        var l = 0;
        var r = coords.size();

        while (r - l > 1) {
            int c = (l + r) / 2;
            var g = getGraph();

            for (IntVector2D coord : coords.subList(0, c)) {
                g.removeVertex(coord);
            }

            var shortestPath = DijkstraShortestPath.findPathBetween(
                    g,
                    new IntVector2D(0),
                    new IntVector2D(WIDTH - 1, HEIGHT - 1)
            );
            if (shortestPath != null) {
                l = c;
            } else {
                r = c;
            }
        }

        return String.valueOf(coords.get(l));
    }

    private DefaultUndirectedGraph<IntVector2D, DefaultEdge> getGraph() {
        var graph = new DefaultUndirectedGraph<IntVector2D, DefaultEdge>(DefaultEdge.class);

        for (int x = 0; x < WIDTH; ++x) {
            for (int y = 0; y < HEIGHT; ++y) {
                var c = new IntVector2D(x, y);
                graph.addVertex(c);
                for (var n : List.of(c.n(), c.e(), c.s(), c.w())) {
                    if (graph.vertexSet().contains(n)) {
                        graph.addEdge(c, n);
                    }
                }
            }
        }

        return graph;
    }
}
