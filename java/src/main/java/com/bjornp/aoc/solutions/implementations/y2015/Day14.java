package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.google.common.collect.HashBasedTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 14)
@Slf4j
public class Day14 extends AdventOfCodeSolution {
    public Day14() {
        super(14, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {
        var target = 2503;

        var p = Pattern.compile("^[A-Za-z]+ can fly (?<speed>\\d+) km\\/s for (?<time1>\\d+) seconds, but then must rest for (?<time2>\\d+) seconds\\.$");
        var res = input.lines().mapToLong(reindeer -> {
            var m = p.matcher(reindeer);
            if (m.matches()) {
                var dist = Long.parseLong(m.group("speed")) * Long.parseLong(m.group("time1"));
                var time = Long.parseLong(m.group("time1")) + Long.parseLong(m.group("time2"));
                var travelled = (target / time) * dist;
                if (target % time > Long.parseLong(m.group("time1"))) {
                    travelled += dist;
                } else {
                    travelled += (target % time) *  Long.parseLong(m.group("speed"));
                }
                return travelled;
            }
            throw new IllegalArgumentException("Invalid: " + reindeer);
        }).max();

        return "%,d".formatted(res.getAsLong());
    }

    public String runSolutionB(String input) {
        var p = Pattern.compile("^[A-Za-z]+ can fly (?<speed>\\d+) km\\/s for (?<time1>\\d+) seconds, but then must rest for (?<time2>\\d+) seconds\\.$");

        var reindeers = input.lines().map(line -> {
            var m = p.matcher(line);
            if (m.matches()) {
                return new Reindeer(Integer.parseInt(m.group("speed")), Integer.parseInt(m.group("time1")), Integer.parseInt(m.group("time2")));
            }
            throw new IllegalArgumentException("Invalid: " + line);
        }).toList();

        for (AtomicInteger i = new AtomicInteger(0); i.get() < 2503; i.incrementAndGet()) {
            reindeers.forEach(r -> r.step(i.get()));
            var lead = reindeers.stream().max(Comparator.comparingInt(Reindeer::getPos)).get().pos;
            reindeers.stream().filter(r -> r.pos == lead).forEach(r -> r.points++);
        }

        return "%,d".formatted(reindeers.stream().max(Comparator.comparingInt(Reindeer::getPoints)).get().getPoints());
    }

    @Data
    @RequiredArgsConstructor
    private static class Reindeer {
        private final int speed;
        private final int travelTime;
        private final int sleepTime;
        private int pos = 0;
        private int points = 0;

        public void step(int curTime) {
            if (curTime % (travelTime + sleepTime) < travelTime) {
                pos += speed;
            }
        }
    }

}
