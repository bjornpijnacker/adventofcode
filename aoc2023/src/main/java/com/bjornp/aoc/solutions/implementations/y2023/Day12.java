package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@SolutionDay(year = 2023, day = 12)
@Slf4j
public class Day12 extends AdventOfCodeSolution {
    private final Map<String, Long> mem = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            return size() > 250;  // limit the max size of the map to limit memory usage
        }
    };

    public Day12() {
        super(12, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var rows = input.split("\n");

        return "%,d".formatted(Arrays.stream(rows).mapToLong(row -> {
            var nums = Arrays.stream(row.split(" ")[1].split(",")).mapToInt(Integer::parseInt).toArray();
            return calculateRowOptions(row.split(" ")[0], nums, false);
        }).sum());
    }

    private String runSolutionB(String input) {
        var rows = input.split("\n");
        var bigRows = Arrays.stream(rows).map(row -> {
            var nums = row.split(" ")[1];
            var springs = row.split(" ")[0];
            return "%s %s".formatted(StringUtils.repeat(springs, "?", 5), StringUtils.repeat(nums, ",", 5));
        }).toArray(String[]::new);
        return runSolutionA(String.join("\n", bigRows));
    }

    private long memoizeReturn(String row, int[] nums, boolean previousHash, long value) {
        mem.put(formatKey(row, nums, previousHash), value);
        return value;
    }

    private String formatKey(String row, int[] nums, boolean previousHash) {
        return "%s %s %b".formatted(row, Arrays.toString(nums), previousHash);
    }

    private long calculateRowOptions(String row, int[] nums, boolean previousHash) {
        if (mem.get(formatKey(row, nums, previousHash)) != null) {
            return mem.get(formatKey(row, nums, previousHash));
        }

        if (Arrays.stream(nums).sum() > row.length()) {
            return memoizeReturn(row, nums, previousHash, 0);
        }

        if (row.isEmpty() && Arrays.stream(nums).sum() == 0) {
            return memoizeReturn(row, nums, previousHash,1);
        }

        if (row.startsWith(".")) {
            return memoizeReturn(row, nums,previousHash, calculateRowOptions(row.substring(1), nums, false));
        } else if (row.startsWith("#")) {
            if (nums.length == 0) {  // safety check
                return memoizeReturn(row, nums, previousHash,0);
            }

            if (row.substring(0, nums[0]).contains(".")) {  // meaning the current situation is impossible
                return memoizeReturn(row, nums, previousHash,0);
            } else {  // consume the current number of locations and continue
                return memoizeReturn(row, nums, previousHash,previousHash ? 0 : calculateRowOptions(row.substring(nums[0]), ArrayUtils.remove(nums, 0), true));
            }
        } else if (row.startsWith("?")) {
            if (nums.length == 0) {  // safety check
                return memoizeReturn(row, nums,previousHash, calculateRowOptions(row.substring(1), nums, false));
            }

            if (row.substring(0, nums[0]).contains(".")) {  // meaning the current situation is impossible
                return memoizeReturn(row, nums, previousHash,calculateRowOptions(row.substring(1), nums, false));
            } else {  // consume the current number of locations and continue PLUS try with a dot
                var dots = calculateRowOptions(row.substring(1), nums, false);
                var hashes = previousHash ? 0 : calculateRowOptions(row.substring(nums[0]), ArrayUtils.remove(nums, 0), true);
                return memoizeReturn(row, nums, previousHash,dots + hashes);
            }
        }

        return 0;
    }

    private String nChar(char c, int n) {
        return new String(new char[n]).replace('\0', c);
    }
}
