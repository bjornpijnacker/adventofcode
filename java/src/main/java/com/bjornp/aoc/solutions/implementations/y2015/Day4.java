package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.security.MessageDigest;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

@SolutionDay(year = 2015, day = 4)
@Slf4j
public class Day4 extends AdventOfCodeSolution {
    public Day4() {
        super(4, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    @SneakyThrows
    public String runSolutionA(String input) {
        long i = 0;
        while (true) {
            var hex = Hex.encodeHexString(MessageDigest.getInstance("MD5").digest((input + i).getBytes())) ;
            if (hex.startsWith("00000")) {
                break;
            }
            ++i;
        }

        return "%,d".formatted(i);
    }

    @SneakyThrows
    public String runSolutionB(String input) {
        long i = 0;
        while (true) {
            var hex = Hex.encodeHexString(MessageDigest.getInstance("MD5").digest((input + i).getBytes())) ;
            if (hex.startsWith("000000")) {
                break;
            }
            ++i;
        }

        return "%,d".formatted(i);
    }
}
