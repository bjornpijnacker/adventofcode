package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Grid2D;
import com.bjornp.aoc.util.IntVector2D;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SolutionDay(year = 2024, day = 14)
@Slf4j
public class Day14 extends AdventOfCodeSolution {

    public Day14(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        // INPUT READING
        var values = this.parseInput(input);
        var w = values.getLeft();
        var h = values.getMiddle();
        var robots = values.getRight();

        var q = new int[]{0, 0, 0, 0};

        for (var robot : robots) {
            var finalPos = robot.getLeft().add(robot.getRight().mult(100));
            var finalPosLimit = new IntVector2D(((finalPos.getX() % w) + w) % w, ((finalPos.getY() % h) + h) % h);

            var centerW = w / 2;
            var centerH = h / 2;
            // check quadrant
            if (finalPosLimit.getX() < centerW && finalPosLimit.getY() < centerH) {
                q[0]++;
            } else if (finalPosLimit.getX() < centerW && finalPosLimit.getY() > centerH) {
                q[2]++;
            } else if (finalPosLimit.getX() > centerW && finalPosLimit.getY() < centerH) {
                q[1]++;
            } else if (finalPosLimit.getX() > centerW && finalPosLimit.getY() > centerH) {
                q[3]++;
            }
        }

        return "%d".formatted(q[0] * q[1] * q[2] * q[3]);
    }

    @SneakyThrows
    protected String runSolutionB(String input) {
        var values = this.parseInput(input);
        var w = values.getLeft();
        var h = values.getMiddle();
        var robots = values.getRight();

        var grid = new Grid2D<>(Collections
                .nCopies(h, Collections.nCopies(w, " ").toArray(String[]::new))
                .toArray(String[][]::new));

        long iterations = 0;
        w: while (true) {
            // FLOOD FILL, LOOK FOR 50+ CONNECTED COMPONENTS
            var seen = new HashSet<IntVector2D>();
            var robotLocations = robots.stream().map(Pair::getLeft).collect(Collectors.toSet());

            for (var p : robotLocations) {
                if (seen.contains(p)) continue;
                seen.add(p);

                // found a robot
                var queue = new ArrayDeque<IntVector2D>();
                var area = new HashSet<IntVector2D>();
                queue.add(p);
                while (!queue.isEmpty()) {
                    var q = queue.pop();
                    area.add(q);
                    seen.add(q);
                    if (robotLocations.contains(q.n()) && !seen.contains(q.n())) queue.add(q.n());
                    if (robotLocations.contains(q.e()) && !seen.contains(q.e())) queue.add(q.e());
                    if (robotLocations.contains(q.s()) && !seen.contains(q.s())) queue.add(q.s());
                    if (robotLocations.contains(q.w()) && !seen.contains(q.w())) queue.add(q.w());
                }

                if (area.size() > 50) break w;
            }
            // iterate each robot
            robots = this.iterate(w, h, robots);

            iterations++;
        }

        var bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        var g = bi.getGraphics();

        // draw the picture
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.WHITE);
        for (var r : robots) {
            g.fillRect(r.getLeft().getX(), r.getLeft().getY(), 1, 1);
        }

        // save the picture
        ImageIO.write(bi, "png", new File("output/%d.png".formatted(iterations)));


        return "%d".formatted(iterations);
    }

    private Triple<Integer, Integer, List<Pair<IntVector2D, IntVector2D>>> parseInput(String input) {
        // INPUT READING
        var split = input.split("\n\n");

        var wh = Pattern.compile("w=(?<w>\\d+), h=(?<h>\\d+)").matcher(split[0]);
        wh.matches();

        int w = Integer.parseInt(wh.group("w"));
        int h = Integer.parseInt(wh.group("h"));

        var robots = new ArrayList<Pair<IntVector2D, IntVector2D>>();

        for (var robotText : split[1].lines().toList()) {
            var r = Pattern.compile("p=(?<px>\\d+),(?<py>\\d+) v=(?<vx>-?\\d+),(?<vy>-?\\d+)").matcher(robotText);
            r.matches();

            var p = new IntVector2D(Integer.parseInt(r.group("px")), Integer.parseInt(r.group("py")));
            var v = new IntVector2D(Integer.parseInt(r.group("vx")), Integer.parseInt(r.group("vy")));

            robots.add(Pair.of(p, v));
        }

        return Triple.of(w, h, robots);
    }

    private List<Pair<IntVector2D, IntVector2D>> iterate(int w, int h, List<Pair<IntVector2D, IntVector2D>> robots) {
        return robots.stream().map(robot -> Pair.of(robot.getLeft().add(robot.getRight()).mod(w, h), robot.getRight())).toList();
    }
}
