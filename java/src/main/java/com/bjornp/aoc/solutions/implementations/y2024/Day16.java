package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Direction2D;
import com.bjornp.aoc.util.Grid2D;
import com.bjornp.aoc.util.IntVector2D;
import com.bjornp.aoc.util.viz.Animation;
import com.bjornp.aoc.util.viz.Colormap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;

@SolutionDay(year = 2024, day = 16)
@Slf4j
public class Day16 extends AdventOfCodeSolution {
    private Grid2D<String> grid;

    public Day16(int day, int year) {
        super(day, year);

        register("a", this::runSolution);
    }

    protected String runSolution(String input) {
        this.grid = new Grid2D<>(input
                .lines()
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));

        var start = grid.findAll("S").stream().findAny().orElseThrow();
        var end = grid.findAll("E").stream().findAny().orElseThrow();

        var shortestPathCost_numTilesAllShortestPaths = shortestPath(start, Direction2D.EAST, end);

        return "A: %d\tB: %d".formatted(
                shortestPathCost_numTilesAllShortestPaths.getLeft(),
                shortestPathCost_numTilesAllShortestPaths.getRight()
        );
    }

    private Pair<Long, Long> shortestPath(IntVector2D pos, Direction2D dir, IntVector2D goal) {
        var q = new PriorityQueue<Triple<IntVector2D, Direction2D, Long>>(Comparator.comparingLong(Triple::getRight));
        var dist = new HashMap<Pair<IntVector2D, Direction2D>, Long>();
        var prev = new HashMap<Pair<IntVector2D, Direction2D>, HashSet<Pair<IntVector2D, Direction2D>>>();

        var vs = new ArrayList<>(grid.findAll("."));
        vs.add(pos);
        vs.add(goal);

        for (var v : vs) {
            dist.put(Pair.of(v, Direction2D.NORTH), (long) Integer.MAX_VALUE);
            dist.put(Pair.of(v, Direction2D.EAST), (long) Integer.MAX_VALUE);
            dist.put(Pair.of(v, Direction2D.SOUTH), (long) Integer.MAX_VALUE);
            dist.put(Pair.of(v, Direction2D.WEST), (long) Integer.MAX_VALUE);
        }

        dist.put(Pair.of(pos, dir), 0L);
        q.add(Triple.of(pos, dir, 0L));

        while (!q.isEmpty()) {
            var u = q.poll();

            // for each neighbor v of u:
            var vMove = Pair.of(Pair.of(u.getLeft().move(u.getMiddle()), u.getMiddle()), 1L);
            var vLeft = Pair.of(Pair.of(u.getLeft(), u.getMiddle().left()), 1000L);
            var vRight = Pair.of(Pair.of(u.getLeft(), u.getMiddle().right()), 1000L);

            for (var n : List.of(vMove, vLeft, vRight)) {
                var neighbor = n.getLeft();
                var cost = n.getRight();
                if (grid.get(neighbor.getLeft()).equals("#")) {
                    continue;
                }
                var alt = dist.get(Pair.of(u.getLeft(), u.getMiddle())) + cost;
                if (alt < dist.getOrDefault(neighbor, (long) Integer.MAX_VALUE)) {
                    dist.put(neighbor, alt);
                    prev.put(neighbor, new HashSet<>(List.of(Pair.of(u.getLeft(), u.getMiddle()))));
                    q.add(Triple.of(neighbor.getLeft(), neighbor.getRight(), alt));
                } else if (alt == dist.getOrDefault(neighbor, (long) Integer.MAX_VALUE)) {
                    prev.get(neighbor).add(Pair.of(u.getLeft(), u.getMiddle()));
                }
            }
        }

        var shortestDistanceNode = dist
                .entrySet()
                .stream()
                .filter(d -> d.getKey().getLeft().equals(goal))
                .min(Comparator.comparingLong(Map.Entry::getValue))
                .orElseThrow();

        // find paths
        var visited = new HashSet<IntVector2D>();
        var toCheck = new ArrayDeque<Pair<IntVector2D, Direction2D>>();
        toCheck.push(shortestDistanceNode.getKey());

        while (!toCheck.isEmpty()) {
            var v = toCheck.pop();
            visited.add(v.getLeft());
            toCheck.addAll(prev.getOrDefault(v, new HashSet<>()));
        }

        return Pair.of(shortestDistanceNode.getValue(), (long) visited.size());
    }
}
