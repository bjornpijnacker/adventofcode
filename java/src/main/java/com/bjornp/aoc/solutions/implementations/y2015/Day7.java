package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 7)
@Slf4j
public class Day7 extends AdventOfCodeSolution {
    private final Map<String, Character> wires = new HashMap<>();

    public Day7() {
        super(7, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var prefix = Pattern.compile("^NOT (?<from>[a-z0-9]+) -> (?<to>[a-z]+)$");
        var infix = Pattern.compile("^(?<left>[a-z0-9]+) (?<op>[A-Z]+) (?<right>[a-z0-9]+) -> (?<to>[a-z]+)$");
        var noop = Pattern.compile("^(?<from>[a-z0-9]+) -> (?<to>[a-z]+)$");

        var queue = new LinkedList<>(input.lines().toList());
        while (!queue.isEmpty()) {
            var circuit = queue.poll();

            var prefixMatcher = prefix.matcher(circuit);
            var infixMatcher = infix.matcher(circuit);
            var noopMatcher = noop.matcher(circuit);

            if (prefixMatcher.matches()) {
                var from = prefixMatcher.group("from");
                var to = prefixMatcher.group("to");
                if (isNumeric(from)) {
                    wires.put(to, (char) ~Short.parseShort(from));
                } else {
                    if (wires.containsKey(from)) {
                        wires.put(to, (char) ~wires.get(from));
                    } else {
                        queue.add(circuit);  // retry later
                    }
                }
            } else if (infixMatcher.matches()) {
                var left = infixMatcher.group("left");
                var op = infixMatcher.group("op");
                var right = infixMatcher.group("right");
                var to = infixMatcher.group("to");
                switch (op) {
                    case "AND" -> {
                        if (isNumeric(left) && isNumeric(right)) {
                            wires.put(to, (char) (Short.parseShort(left) & Short.parseShort(right)));
                        } else if (isNumeric(left)) {
                            if (wires.containsKey(right)) {
                                wires.put(to, (char) (Short.parseShort(left) & wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else if (isNumeric(right)) {
                            if (wires.containsKey(left)) {
                                wires.put(to, (char) (wires.get(left) & Short.parseShort(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else {
                            if (wires.containsKey(left) && wires.containsKey(right)) {
                                wires.put(to, (char) (wires.get(left) & wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        }
                    }
                    case "OR" -> {
                        if (isNumeric(left) && isNumeric(right)) {
                            wires.put(to, (char) (Short.parseShort(left) | Short.parseShort(right)));
                        } else if (isNumeric(left)) {
                            if (wires.containsKey(right)) {
                                wires.put(to, (char) (Short.parseShort(left) | wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else if (isNumeric(right)) {
                            if (wires.containsKey(left)) {
                                wires.put(to, (char) (wires.get(left) | Short.parseShort(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else {
                            if (wires.containsKey(left) && wires.containsKey(right)) {
                                wires.put(to, (char) (wires.get(left) | wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        }
                    }
                    case "LSHIFT" -> {
                        if (isNumeric(left) && isNumeric(right)) {
                            wires.put(to, (char) (Short.parseShort(left) << Short.parseShort(right)));
                        } else if (isNumeric(left)) {
                            if (wires.containsKey(right)) {
                                wires.put(to, (char) (Short.parseShort(left) << wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else if (isNumeric(right)) {
                            if (wires.containsKey(left)) {
                                wires.put(to, (char) (wires.get(left) << Short.parseShort(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else {
                            if (wires.containsKey(left) && wires.containsKey(right)) {
                                wires.put(to, (char) (wires.get(left) << wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        }
                    }
                    case "RSHIFT" -> {
                        if (isNumeric(left) && isNumeric(right)) {
                            wires.put(to, (char) (Short.parseShort(left) >> Short.parseShort(right)));
                        } else if (isNumeric(left)) {
                            if (wires.containsKey(right)) {
                                wires.put(to, (char) (Short.parseShort(left) >> wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else if (isNumeric(right)) {
                            if (wires.containsKey(left)) {
                                wires.put(to, (char) (wires.get(left) >> Short.parseShort(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        } else {
                            if (wires.containsKey(left) && wires.containsKey(right)) {
                                wires.put(to, (char) (wires.get(left) >> wires.get(right)));
                            } else {
                                queue.add(circuit);  // retry later
                            }
                        }
                    }
                }
            } else if (noopMatcher.matches()) {
                var from = noopMatcher.group("from");
                var to = noopMatcher.group("to");
                if (isNumeric(from)) {
                    wires.put(to, (char) Short.parseShort(from));
                } else {
                    if (wires.containsKey(from)) {
                        wires.put(to, wires.get(from));
                    } else {
                        queue.add(circuit);  // retry later
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid circuit: " + circuit);
            }
        }

        log.info("{}", wires.entrySet().stream().map(entry -> entry.getKey() + ": " + (int) entry.getValue()).toList());
        return "%,d".formatted((int) wires.get("a"));
    }

    public String runSolutionB(String input) {
        return runSolutionA(input.replaceAll("14146 -> b", "956 -> b"));
    }

    private boolean isNumeric(String value) {
        try {
            Short.parseShort(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
