package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

@SolutionDay(year = 2023, day = 6)
@Slf4j
public class Day6 extends AdventOfCodeSolution {
    public Day6() {
        super(6, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var times = Arrays.stream(input.split("\n")[0].split(":")[1].split(" "))
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .toList();
        var distances = Arrays.stream(input.split("\n")[1].split(":")[1].split(" "))
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .toList();
        var races = new ArrayList<Race>();

        for (int i = 0; i < times.size(); i++) {
            races.add(Race.builder().time(times.get(i)).distance(distances.get(i)).build());
        }

        var result = races.stream().mapToInt(race -> {
            var numWins = 0;
            for (int holdTime = 0; holdTime <= race.getTime(); ++holdTime) {
                var score = race.getScore(holdTime);
                if (score > race.getDistance()) {
                    numWins++;
                } else if (numWins != 0) {  // wins must be sequential
                    break;
                }
            }
            return numWins;
        }).filter(val -> val != 0).reduce(1, (a, b) -> a * b);

        return String.valueOf(result);
    }

    private String runSolutionB(String input) {
        var time = Integer.parseInt(input.split("\n")[0].split(":")[1].replaceAll(" ", "").trim());

        var distance = Long.parseLong(input.split("\n")[1].split(":")[1].replaceAll(" ", "").trim());
        var race = Race.builder().time(time).distance(distance).build();

        var numWins = 0;
        for (int holdTime = 0; holdTime <= race.getTime(); ++holdTime) {
            var score = race.getScore(holdTime);
            if (score > race.getDistance()) {
                numWins++;
            } else if (numWins != 0) {  // wins must be sequential
                break;
            }
        }

        return String.valueOf(numWins);
    }

    @Data
    @Builder
    private static class Race {
        private int time;

        private long distance;

        public long getScore(long holdTime) {
            return time * holdTime - holdTime * holdTime;
        }
    }
}
