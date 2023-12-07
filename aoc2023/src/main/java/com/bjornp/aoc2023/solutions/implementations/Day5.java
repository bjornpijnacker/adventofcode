package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@SolutionDay(day = 5)
@Slf4j
public class Day5 extends AdventOfCodeSolution {

    public Day5() {
        super(5);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
        register("b_brute", this::runSolutionBBrute);
    }

    private String runSolutionA(String input) {
        var seeds = Arrays.stream(input.split("\n")[0].substring(7).split(" ")).map(Long::parseUnsignedLong).toList();
        var maps = Arrays.stream(input.substring(input.indexOf('\n')).trim().split("\n\n")).map(SeedMap::new).toList();

        return String.valueOf(seeds.stream().mapToLong(seed -> {
            AtomicLong val = new AtomicLong(seed);
            maps.forEach(map -> val.set(map.map(val.get())));
            return val.get();
        }).min());
    }

    private String runSolutionB(String input) {
        var seeds = Arrays.stream(input.split("\n")[0].substring(7).split(" ")).map(Long::parseUnsignedLong).toList();
        var maps = Arrays.stream(input.substring(input.indexOf('\n')).trim().split("\n\n")).map(SeedMap::new).toList();

        var output = new TreeSet<Long>();

        for (int i = 0; (i + 1) < seeds.size(); i += 2) {
            AtomicReference<String> range = new AtomicReference<>("%d %d".formatted(seeds.get(i), seeds.get(i + 1)));
            maps.forEach(map -> {
                var outputRange = new AtomicReference<>("");
                var nums = Arrays.stream(range.get().split(" ")).map(Long::parseUnsignedLong).toList();
                for (int j = 0; (j + 1) < nums.size(); j += 2) {
                    var mapping = map.mapRange(nums.get(j), nums.get(j + 1));
                    outputRange.set((outputRange.get() + " " + mapping).trim());
                }
                range.set(outputRange.get());
            });

            var nums = Arrays.stream(range.get().split(" ")).map(Long::parseUnsignedLong).toList();
            for (int j = 0; (j + 1) < nums.size(); j += 2) {
                output.add(nums.get(j));
            }
        }

        return String.valueOf(output.first());
    }

    private String runSolutionBBrute(String input) {
        var seeds = Arrays.stream(input.split("\n")[0].substring(7).split(" ")).map(Long::parseUnsignedLong).toList();
        var maps = Arrays.stream(input.substring(input.indexOf('\n')).trim().split("\n\n")).map(SeedMap::new).toList();

        var reverseMaps = new ArrayList<>(maps);
        Collections.reverse(reverseMaps);

        for (int i = 0; ; i++) {
            if (i % 1e5 == 0) {
                log.debug("%,d".formatted(i));
            }
            AtomicLong result = new AtomicLong(i);

            reverseMaps.forEach(map -> result.set(map.reverseMap(result.get())));
            for (int s = 0; (s + 1) < seeds.size(); s += 2) {
                if (result.get() >= seeds.get(s) && result.get() < seeds.get(s) + seeds.get(s + 1)) {
                    return String.valueOf(i);
                }
            }
        }
    }

    private static class SeedMap {
        private final List<SingleMap> maps = new ArrayList<>();

        public SeedMap(String input) {
            var mapLines = input.split("\n");
            for (int i = 1; i < mapLines.length; i++) {
                var nums = mapLines[i].split(" ");
                var source = Long.parseUnsignedLong(nums[1]);
                var destination = Long.parseUnsignedLong(nums[0]);
                var range = Long.parseUnsignedLong(nums[2]);
                maps.add(SingleMap.builder().destination(destination).range(range).source(source).build());
            }
            maps.sort(Comparator.comparingLong(SingleMap::getSource));
        }

        public long map(long val) {
            var matchedMap = maps.stream().filter(map -> map.inRange(val)).findFirst();
            return matchedMap.map(singleMap -> singleMap.map(val)).orElse(val);
        }

        public long reverseMap(long val) {
            var matchedMap = maps.stream().filter(map -> map.inReverseRange(val)).findFirst();
            return matchedMap.map(singleMap -> singleMap.reverseMap(val)).orElse(val);
        }

        public String mapRange(long start, long range) {
            if (range == 0) {
                return "";
            }

            var matchingMap = maps.stream().filter(map -> map.inRange(start)).findFirst();
            if (matchingMap.isPresent()) {
                var mapMax = matchingMap.get().getSource() + matchingMap.get().getRange();
                var step = mapMax - start;
                if (step > range || step == 0) {
                    return "%d %d".formatted(matchingMap.get().map(start), range);
                } else {
                    return "%d %d %s".formatted(matchingMap.get().map(start),
                            step,
                            mapRange(start + step, range - step)
                    );
                }
            } else {
                var firstMatchingMap = maps.stream().filter(map -> map.source >= start).findFirst();
                if (firstMatchingMap.isEmpty()) {
                    return "%d %d".formatted(start, range);
                }
                var skip = firstMatchingMap.get().getSource() - start;
                if (skip < 0 || skip > range) {
                    return "%d %d".formatted(start, range);
                }
                return "%d %d %s".formatted(start, skip, mapRange(start + skip, range - skip));
            }
        }

        @Data
        @Builder
        private static class SingleMap {
            private long destination;

            private long range;

            private long source;

            public long map(long val) {
                if (!inRange(val)) {
                    throw new IllegalArgumentException("Val not in range");
                }
                return val + (destination - source);
            }

            public boolean inRange(long val) {
                return val >= source && val < source + range;
            }

            public long reverseMap(long val) {
                if (!inReverseRange(val)) {
                    throw new IllegalArgumentException("Val not in range");
                }
                return val + (source - destination);
            }

            public boolean inReverseRange(long val) {
                return val >= destination && val < destination + range;
            }
        }
    }
}
