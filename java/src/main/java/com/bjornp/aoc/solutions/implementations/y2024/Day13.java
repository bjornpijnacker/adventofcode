package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;

import java.util.regex.Pattern;

@SolutionDay(year = 2024, day = 13)
@Slf4j
public class Day13 extends AdventOfCodeSolution {

    public Day13(int day, int year) {
        super(day, year);

        register("a", input -> runSolution(input, 0, 100, 1e-7d));
        register("b", input -> runSolution(input, 10000000000000L, Long.MAX_VALUE, 1e-4d));
    }

    protected String runSolution(String input, long prizeOffset, long maxPresses, double eps) {
        var tokens = 0L;

        for (var eq : input.split("\n\n")) {
            var reButton = Pattern.compile("Button [AB]: X\\+(?<x>\\d+), Y\\+(?<y>\\d+)");
            var rePrize = Pattern.compile("Prize: X=(?<x>\\d+), Y=(?<y>\\d+)");

            var lines = eq.lines().toArray(String[]::new);
            var buttonA = reButton.matcher(lines[0]);
            var buttonB = reButton.matcher(lines[1]);
            var prize = rePrize.matcher(lines[2]);

            buttonA.matches();
            buttonB.matches();
            prize.matches();

            var a = new Array2DRowRealMatrix(new double[][]{
                    {Double.parseDouble(buttonA.group("x")), Double.parseDouble(buttonB.group("x"))},
                    {Double.parseDouble(buttonA.group("y")), Double.parseDouble(buttonB.group("y"))}
            });
            var b = new ArrayRealVector(new double[]{
                    Double.parseDouble(prize.group("x")) + prizeOffset,
                    Double.parseDouble(prize.group("y")) + prizeOffset
            });

            var solver = new LUDecomposition(a).getSolver();
            var solution = solver.solve(b);

            var n = solution.getEntry(0);
            var m = solution.getEntry(1);

            // </3 floating point numbers :(
            if (Math.abs(n - Math.round(n)) < eps && Math.abs(m - Math.round(m)) < eps  // integer solution!
                    && Math.round(n) <= maxPresses && Math.round(m) <= maxPresses) {
                tokens += Math.round(n) * 3 + Math.round(m);
            }
        }

        return "%d".formatted(tokens);
    }
}
