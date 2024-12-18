package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Direction2D;
import com.bjornp.aoc.util.Grid2D;
import com.bjornp.aoc.util.IntVector2D;
import com.bjornp.aoc.util.viz.Animation;
import com.bjornp.aoc.util.viz.Colormap;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SolutionDay(year = 2024, day = 12)
@Slf4j
public class Day12 extends AdventOfCodeSolution {

    public Day12(int day, int year) {
        super(day, year);

        register("a", input -> runSolution(input, true));
        register("b", input -> runSolution(input, false));
    }

    @SneakyThrows
    protected String runSolution(String input, boolean usePerim) {
        var grid = new Grid2D<>(input.lines().map(line -> line.split("")).toArray(String[][]::new));
        return "%d".formatted(calculateFenceCost(grid, usePerim));
    }

    private long calculateFenceCost(Grid2D<String> grid, boolean usePerim) throws IOException {
        var textHeight = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics().getFontMetrics().getHeight();
        var pad = 3;
        var cubeSize = 6;
        try (var animation = new Animation(
                this,
                "Flood Fill test",
                grid.getWidth() * cubeSize + grid.getWidth() * pad,
                (int) (grid.getHeight() * cubeSize + grid.getHeight() * pad + textHeight * 1.5),
                60)) {
            var animationI = 0;

            long cost = 0;
            var toCheck = new ArrayDeque<IntVector2D>();
            var seen = new HashSet<IntVector2D>();

            var animationAreas = new ArrayList<HashSet<IntVector2D>>();
            var animationSegments = new HashSet<Segment>();

            for (int x = 0; x < grid.getWidth(); ++x) {
                for (int y = 0; y < grid.getHeight(); ++y) {
                    toCheck.add(new IntVector2D(x, y));
                }
            }

            while (!toCheck.isEmpty()) {
                var areaStart = toCheck.pop();
                if (seen.contains(areaStart)) continue;

                // flood fill the area
                var areaQ = new ArrayDeque<IntVector2D>();
                areaQ.add(areaStart);
                var area = new HashSet<IntVector2D>();
                var sideSegments = new HashSet<Segment>();

                while (!areaQ.isEmpty()) {
                    var cur = areaQ.pop();
                    if (!area.contains(cur)) {
                        area.add(cur);

                        for (var neighborD : List.of(
                                Direction2D.NORTH,
                                Direction2D.EAST,
                                Direction2D.SOUTH,
                                Direction2D.WEST
                        )) {
                            var neighbor = cur.move(neighborD);
                            if (areaQ.contains(neighbor) || area.contains(neighbor)) continue;
                            if (grid.inBounds(neighbor) && grid.get(neighbor).equals(grid.get(cur))) {
                                areaQ.add(neighbor);
                            } else {
                                switch (neighborD) {
                                    case NORTH -> sideSegments.add(new Segment(
                                            new IntVector2D(cur),
                                            new IntVector2D(cur.e()),
                                            Direction2D.SOUTH
                                    ));
                                    case EAST -> sideSegments.add(new Segment(
                                            new IntVector2D(cur.e()),
                                            new IntVector2D(cur.se()),
                                            Direction2D.WEST
                                    ));
                                    case SOUTH -> sideSegments.add(new Segment(
                                            new IntVector2D(cur.s()),
                                            new IntVector2D(cur.se()),
                                            Direction2D.NORTH
                                    ));
                                    case WEST -> sideSegments.add(new Segment(
                                            new IntVector2D(cur),
                                            new IntVector2D(cur.s()),
                                            Direction2D.EAST
                                    ));
                                }
                            }
                        }
                    }

                    if (animationI % 6 == 0) {
                        var bi = animation.image();
                        var g = bi.getGraphics();
                        g.setColor(Color.BLACK);
                        g.fillRect(0, 0, animation.getWidth(), animation.getHeight());

                        for (int i = 0; i < animationAreas.size(); ++i) {
                            g.setColor(Colormap.Turbo.get(i % 10 / 10d).darker());
                            for (var c : animationAreas.get(i)) {
                                g.fillRect(c.getX() * (cubeSize + pad) + pad, c.getY() * (cubeSize + pad) + pad, cubeSize, cubeSize);
                            }
                        }
                        g.setColor(Color.WHITE);
                        for (var c : area) {
                            g.fillRect(c.getX() * (cubeSize + pad) + pad, c.getY() * (cubeSize + pad) + pad, cubeSize, cubeSize);
                        }
                        for (var s : sideSegments) {
                            g.drawLine(
                                    s.start.getX() * (cubeSize + pad) + 1,
                                    s.start.getY() * (cubeSize + pad) + 1,
                                    s.end.getX() * (cubeSize + pad) + 1,
                                    s.end.getY() * (cubeSize + pad) + 1
                            );
                        }
                        for (var s : animationSegments) {
                            g.drawLine(
                                    s.start.getX() * (cubeSize + pad) + 1,
                                    s.start.getY() * (cubeSize + pad) + 1,
                                    s.end.getX() * (cubeSize + pad) + 1,
                                    s.end.getY() * (cubeSize + pad) + 1
                            );
                        }
                        g.setColor(Color.GRAY);
                        for (var c : areaQ) {
                            g.fillRect(c.getX() * (cubeSize + pad) + pad, c.getY() * (cubeSize + pad) + pad, cubeSize, cubeSize);
                        }
                        g.setColor(Color.WHITE);
                        g.drawString("Cost: %,d".formatted(cost), 0, animation.getHeight() - textHeight / 2);
                        animation.registerFrame(bi);
                    }
                    animationI++;
                }
                seen.addAll(area);
                animationAreas.add(area);
                animationSegments.addAll(sideSegments);

                if (usePerim) {  // puzzle 1
                    cost += (long) sideSegments.size() * area.size();
                } else {  // puzzle 2
                    var sideSegmentsNew = new HashSet<Segment>();
                    var merges = 0;
                    do {
                        merges = 0;
                        var segQue = new ArrayDeque<>(sideSegments);  // queue instead of loop to avoid ConcurrentModificationException
                        while (!segQue.isEmpty()) {
                            var segment = segQue.pop();  // for this segment, find another segment that can merge to its end
                            var mergeable = sideSegments.stream().filter(segment::canMerge).findFirst();
                            if (mergeable.isPresent()) {
                                merges++;
                                sideSegmentsNew.add(new Segment(
                                        new IntVector2D(segment.start),
                                        new IntVector2D(mergeable.get().end),
                                        segment.normal
                                ));

                                // remove the merged segments from the set if already added
                                // in case the mergeable segment was already handled before
                                sideSegmentsNew.remove(mergeable.get());
                                sideSegmentsNew.remove(segment);

                                // don't reuse the mergeable element either
                                segQue.remove(mergeable.get());
                                sideSegments.remove(segment);
                                sideSegments.remove(mergeable.get());
                            } else {
                                sideSegmentsNew.add(segment);
                            }
                        }
                        sideSegments = new HashSet<>(sideSegmentsNew);
                    } while (merges != 0);
                    cost += (long) area.size() * sideSegmentsNew.size();
                }
            }

            return cost;
        }
    }

        private record Segment(
            IntVector2D start,
            IntVector2D end,
            Direction2D normal
    ) {
        private boolean canMerge(Segment o) {
            return this.end.equals(o.start) && this.normal == o.normal;
        }
    }
}
