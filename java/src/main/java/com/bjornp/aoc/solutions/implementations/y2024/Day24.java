package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.lang.model.type.IntersectionType;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SolutionDay(year = 2024, day = 24)
@Slf4j
public class Day24 extends AdventOfCodeSolution {
    public Day24(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private Map<String, Integer> initRegisters(String lines) {
        Map<String, Integer> registers = new HashMap<>();
        var pattern = Pattern.compile("(?<name>.{3}): (?<num>\\d)");
        for (String s : lines.split("\n")) {
            var matcher = pattern.matcher(s);
            if (!matcher.matches()) throw new AssertionError();
            registers.put(matcher.group("name"), Integer.parseInt(matcher.group("num")));
        }
        return registers;
    }

    private HashSet<Gate> getGates(String lines) {
        var gates = new HashSet<Gate>();

        var pattern = Pattern.compile("(?<l>.{3}) (?<op>OR|XOR|AND) (?<r>.{3}) -> (?<o>.{3})");
        for (String s : lines.split("\n")) {
            var matcher = pattern.matcher(s);
            if (!matcher.matches()) throw new AssertionError();

            var gate = new Gate(matcher.group("l"), matcher.group("r"), matcher.group("o"), matcher.group("op"));
            gates.add(gate);
        }

        return gates;
    }

    private void processGates(Map<String, Integer> registers, HashSet<Gate> gates) {
        var q = new ArrayDeque<>(gates);

        while (!q.isEmpty()) {
            var gate = q.pop();
            if (registers.containsKey(gate.i1) && registers.containsKey(gate.i2)) {
                switch (gate.op) {
                    case "OR" -> registers.put(gate.o, registers.get(gate.i1) | registers.get(gate.i2));
                    case "XOR" -> registers.put(gate.o, registers.get(gate.i1) ^ registers.get(gate.i2));
                    case "AND" -> registers.put(gate.o, registers.get(gate.i1) & registers.get(gate.i2));
                    default -> throw new IllegalStateException("gate.op is not AND, OR, or XOR: %s".formatted(gate.op));
                }
            } else {
                q.add(gate);  // back on the end it goes
            }
        }
    }

    private Long getValue(Map<String, Integer> registers, String prefix) {
        var bits = registers.entrySet()
                .stream().filter(entry -> entry.getKey().startsWith(prefix))
                .map(entry -> Pair.of(Integer.parseInt(entry.getKey().substring(1)), entry.getValue()))
                .sorted(Comparator.comparingInt(entry -> -entry.getKey()))
                .map(Map.Entry::getValue)
                .map(Object::toString)
                .collect(Collectors.joining());
        if (prefix.equals("z") )log.info("{}   %50s".formatted(bits), prefix);
        return Long.valueOf(bits, 2);
    }

    protected String runSolutionA(String input) {
        var registers = initRegisters(input.split("\n\n")[0]);
        var gates = getGates(input.split("\n\n")[1]);
        processGates(registers, gates);

        var bits = registers.entrySet()
                .stream().filter(entry -> entry.getKey().startsWith("z"))
                .map(entry -> Pair.of(Integer.parseInt(entry.getKey().substring(1)), entry.getValue()))
                .sorted(Comparator.comparingInt(entry -> -entry.getKey()))
                .map(Map.Entry::getValue)
                .map(Object::toString)
                .collect(Collectors.joining());

        return "%d".formatted(Long.parseLong(bits, 2));
    }

    protected String runSolutionB(String input) {
        var registers = initRegisters(input.split("\n\n")[0]);
        var gates = getGates(input.split("\n\n")[1]);

        processGates(registers, gates);
        log.info("x+y %50s".formatted(Long.toBinaryString(getValue(registers, "x") + getValue(registers, "y"))));
        if (getValue(registers, "x") + getValue(registers, "y") == getValue(registers, "z")) {
            log.info("Winner!");
        }

        return "";
    }

    private record Gate(
            String i1,
            String i2,
            String o,
            String op
    ){}
}
