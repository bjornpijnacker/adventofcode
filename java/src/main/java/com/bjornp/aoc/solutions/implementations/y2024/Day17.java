package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SolutionDay(year = 2024, day = 17)
@Slf4j
public class Day17 extends AdventOfCodeSolution {
    private final List<Integer> output = new ArrayList<>();

    private Registers r;

    private int pc = 0;

    private final List<Operation> program = new ArrayList<>();

    private long debugGlobalSteps = 0L;

    public Day17(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
        register("b_rec", this::runSolutionB2);
    }

    private void readInput(String input) {
        var lines = input.lines().toArray(String[]::new);

        // parse input
        var a = Long.parseLong(lines[0].split(" ")[2]);
        var b = Long.parseLong(lines[1].split(" ")[2]);
        var c = Long.parseLong(lines[2].split(" ")[2]);
        r = new Registers(a, b, c);

        var instructionsText = lines[4];
        var pattern = Pattern.compile("\\d+,\\d+");
        var m = pattern.matcher(instructionsText);
        while (m.find()) {
            var instruction = m.group();
            var opcode = Opcode.valueOf(Integer.parseInt(instruction.split(",")[0]));
            var operand = Long.parseLong(instruction.split(",")[1]);
            program.add(new Operation(opcode, operand));
        }
    }

    protected String runSolutionA(String input) {
        readInput(input);

        while (pc >= 0 && pc < program.size()) {
            run(program.get(pc));
        }

        return StringUtils.join(output, ",");
    }

    protected String runSolutionB(String input) {
        readInput(input);

        var a = 0L;
        var stepsTotal = 0L;

        for (int n = 0; n < program.size() * 2; ++n) {
            // take partial output from n to end of program
            var target = program.stream()
                    .flatMap(p -> Stream.of(p.opcode.opcode, (int) p.operand))
                    .skip(program.size() * 2L - n - 1)
                    .toList();

            // a is int-divided by 8 every iteration, so for each iteration only the last 3 bits matter
            // find the 3 bits for the current program num / output num then shift these onwards
            var new_a = a << 3;
            var steps = 0L;

            while (true) {
                steps++;
                this.pc = 0;
                this.output.clear();
                this.r = new Registers(new_a, 0, 0);

                while (pc >= 0 && pc < program.size()) {
                    run(program.get(pc));
                }

                if (output.equals(target)) {
                    a = new_a;
                    break;
                }
                new_a++;
            }

            stepsTotal += steps;
        }

        log.debug(String.valueOf(stepsTotal));
        return "%d".formatted(a);
    }

    /*
     * This recursive solution checks all possible intermediate candidates for `a` instead of just the first one. This
     * avoids a problem where the code continues with a value of `a` that isn't satisfactory and where the 3-bit segments
     * must overflow in order to compensate. The more naive solution works, but is massively slower in such cases.
     */
    protected String runSolutionB2(String input) {
        readInput(input);

        var values = findA(program.stream()
                .flatMap(p -> Stream.of(p.opcode.opcode, (int) p.operand))
                .toList());

        log.debug(String.valueOf(values));
        log.debug(String.valueOf(debugGlobalSteps));
        return "";
    }

    private List<Long> findA(List<Integer> target) {
        List<Long> aLs;
        if (target.size() == 1) {
            aLs = List.of(0L);
        } else {
            aLs = findA(target.stream().skip(1).toList());
        }

        ArrayList<Long> candidates = new ArrayList<>();

        for (var a : aLs) {
            var newA = a << 3;

            for (long steps = 0L; steps < 8; ++steps) {
                debugGlobalSteps++;

                this.pc = 0;
                this.output.clear();
                this.r = new Registers(newA + steps, 0, 0);

                while (pc >= 0 && pc < program.size()) {
                    run(program.get(pc));
                }

                if (output.equals(target)) {
                    candidates.add(newA + steps);
                }
            }
        }

        return candidates;
    }

    @AllArgsConstructor
    private enum Opcode {
        ADV(0),
        BXL(1),
        BST(2),
        JNZ(3),
        BXC(4),
        OUT(5),
        BDV(6),
        CDV(7);

        private final int opcode;

        public static Opcode valueOf(int opcode) {
            return Arrays
                    .stream(Opcode.values())
                    .filter(op -> op.opcode == opcode)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No Opcode with value %s".formatted(opcode)));
        }
    }

    private record Registers(
            long a,
            long b,
            long c
    ) {
    }

    private record Operation(
            Opcode opcode,
            long operand
    ) {


    }

    private void run(Operation op) {
        switch (op.opcode) {
            case ADV -> r = new Registers(r.a >> combo(op.operand), r.b, r.c);
            case BXL -> r = new Registers(r.a, r.b ^ op.operand, r.c);
            case BST -> r = new Registers(r.a, combo(op.operand) & 0b0111, r.c);
            case JNZ -> {
                if (r.a != 0) {
                    pc = (int) op.operand;
                    return;
                }
            }
            case BXC -> r = new Registers(r.a, r.b ^ r.c, r.c);
            case OUT -> output.add((int) (combo(op.operand) & 0b0111));
            case BDV -> r = new Registers(r.a, r.a >> combo(op.operand), r.c);
            case CDV -> r = new Registers(r.a, r.b, r.a >> combo(op.operand));
        }
        pc++;
    }

    private long combo(long operand) {
        if (operand >= 0 && operand <= 3) {
            return operand;
        }
        if (operand == 4) {
            return r.a;
        }
        if (operand == 5) {
            return r.b;
        }
        if (operand == 6) {
            return r.c;
        }
        throw new IllegalArgumentException("Invalid operand %s".formatted(operand));
    }
}
