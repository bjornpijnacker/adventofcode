package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SolutionDay(day = 19)
@Slf4j
public class Day19 extends AdventOfCodeSolution {
    public Day19() {
        super(19);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var workflows = Arrays.stream(input.split("\n\n")[0].split("\n"))
                .map(Workflow::new)
                .collect(Collectors.toMap(Workflow::getId, Function.identity()));
        var parts = Arrays.stream(input.split("\n\n")[1].split("\n")).map(Part::new).toList();

        List<Part> accepted = new ArrayList<>();

        parts.forEach(part -> {
            while (!List.of("A", "R").contains(part.getCurrentWorkflow())) {
                workflows.get(part.getCurrentWorkflow()).apply(part);
            }
            if (part.getCurrentWorkflow().equals("A")) {
                accepted.add(part);
            }
        });

        return "%,d".formatted(accepted.stream().mapToLong(Part::getSum).sum());
    }

    private String runSolutionB(String input) {
        var workflows = Arrays.stream(input.split("\n\n")[0].split("\n"))
                .map(Workflow::new)
                .collect(Collectors.toMap(Workflow::getId, Function.identity()));

        var q = new LinkedList<RangePart>();
        var accepted = new ArrayList<RangePart>();
        q.add(new RangePart());

        while (!q.isEmpty()) {
            var part = q.pop();
            var newParts = workflows.get(part.getCurrentWorkflow()).apply(part);

//            log.info("Part {} turned into {}", part, newParts);
            accepted.addAll(newParts.stream().filter(p -> p.getCurrentWorkflow().equals("A")).toList());

            newParts = newParts.stream()
                    .filter(p -> !(p.getCurrentWorkflow().equals("R") || p.getCurrentWorkflow().equals("A")))
                    .toList();
            newParts.forEach(q::push);
        }

        var result = accepted.stream().mapToLong(RangePart::getSize).sum();

        return "%d".formatted(result);
    }

    @Data
    public static class Workflow {
        @ToString.Exclude
        private final Pattern workflowPattern = Pattern.compile("^(?<id>[a-z]+)\\{(?<rules>.+)\\}$");

        private String id;

        private List<Rule> rules;

        public Workflow(String workflowString) {
            var matcher = workflowPattern.matcher(workflowString);
            if (matcher.matches()) {
                id = matcher.group("id");
                rules = Stream.of(matcher.group("rules").split(",")).map(Rule::new).toList();
            } else {
                throw new IllegalArgumentException("Invalid workflow string: " + workflowString);
            }
        }

        public void apply(Part part) {
            for (Rule rule : rules) {
                if (rule.applies(part)) {
                    part.setCurrentWorkflow(rule.nextWorkflow);
                    return;
                }
            }
            throw new IllegalStateException("No rule applied to part: " + part);
        }

        public List<RangePart> apply(RangePart p) {
            var output = new ArrayList<RangePart>();
            var thisRound = new ArrayList<RangePart>();
            var nextRound = new ArrayList<RangePart>();
            thisRound.add(p);
            for (Rule rule : rules) {
                for (RangePart part : thisRound) {
                    var newParts = rule.apply(part);
                    nextRound.addAll(newParts.stream()
                            .filter(part2 -> part2.getCurrentWorkflow().equals(this.id))
                            .toList());
                    output.addAll(newParts.stream()
                            .filter(part2 -> !part2.getCurrentWorkflow().equals(this.id))
                            .toList());
                }
                thisRound.clear();
                thisRound.addAll(nextRound);
                nextRound.clear();
            }
            return output;
        }
    }

    @Data
    public static class Rule {
        @ToString.Exclude
        private final Pattern rulePattern = Pattern.compile(
                "^(?:(?<ruleField>[amsx])(?<ruleType>[<>])(?<ruleValue>\\d+):)?(?<nextWorkflow>[a-z]+|[AR])$");

        private Character ruleField;

        private boolean ruleGreater;

        private String ruleValue;

        private String nextWorkflow;

        public Rule(String ruleString) {
            var matcher = rulePattern.matcher(ruleString);
            if (matcher.matches()) {
                ruleField = matcher.group("ruleField") != null ? matcher.group("ruleField").charAt(0) : null;
                ruleGreater = ">".equals(matcher.group("ruleType"));
                ruleValue = matcher.group("ruleValue");
                nextWorkflow = matcher.group("nextWorkflow");
            } else {
                throw new IllegalArgumentException("Invalid rule string: " + ruleString);
            }
        }

        public boolean applies(Part part) {
            if (ruleField == null) {
                return true;
            }
            switch (ruleField) {
                case 'a' -> {
                    var value = part.getA();
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return ruleGreater ? value > ruleValue : value < ruleValue;
                }
                case 'm' -> {
                    var value = part.getM();
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return ruleGreater ? value > ruleValue : value < ruleValue;
                }
                case 's' -> {
                    var value = part.getS();
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return ruleGreater ? value > ruleValue : value < ruleValue;
                }
                case 'x' -> {
                    var value = part.getX();
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return ruleGreater ? value > ruleValue : value < ruleValue;
                }
                default -> throw new IllegalArgumentException("Invalid rule field: " + ruleField);
            }
        }

        public List<RangePart> apply(RangePart part) {
            if (ruleField == null) {
                part.currentWorkflow = this.nextWorkflow;
                return List.of(part);
            }
            switch (ruleField) {
                case 'a' -> {
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return part.splitA(ruleGreater ? ruleValue : ruleValue - 1, ruleGreater, this.nextWorkflow);
                }
                case 'm' -> {
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return part.splitM(ruleGreater ? ruleValue : ruleValue - 1, ruleGreater, this.nextWorkflow);
                }
                case 's' -> {
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return part.splitS(ruleGreater ? ruleValue : ruleValue - 1, ruleGreater, this.nextWorkflow);
                }
                case 'x' -> {
                    var ruleValue = Long.parseLong(this.ruleValue);
                    return part.splitX(ruleGreater ? ruleValue : ruleValue - 1, ruleGreater, this.nextWorkflow);
                }
                default -> throw new IllegalArgumentException("Invalid rule field: " + ruleField);
            }
        }
    }

    @Data
    public static class Part {
        @ToString.Exclude
        private final Pattern partPattern = Pattern.compile(
                "^\\{x=(?<x>\\d+),m=(?<m>\\d+),a=(?<a>\\d+),s=(?<s>\\d+)\\}$");

        private long x;

        private long m;

        private long a;

        private long s;

        private String currentWorkflow;

        public Part(String partString) {
            this.currentWorkflow = "in";

            var matcher = partPattern.matcher(partString);
            if (matcher.matches()) {
                x = Long.parseLong(matcher.group("x"));
                m = Long.parseLong(matcher.group("m"));
                a = Long.parseLong(matcher.group("a"));
                s = Long.parseLong(matcher.group("s"));
            } else {
                throw new IllegalArgumentException("Invalid part string: " + partString);
            }
        }

        public long getSum() {
            return x + m + a + s;
        }
    }

    @Data
    public static class RangePart {
        @ToString.Exclude
        private Range<Long> x;

        private Range<Long> m;

        private Range<Long> a;

        private Range<Long> s;

        private String currentWorkflow;

        public RangePart() {
            this.currentWorkflow = "in";

            x = Range.between(1L, 4000L);
            m = Range.between(1L, 4000L);
            a = Range.between(1L, 4000L);
            s = Range.between(1L, 4000L);
        }

        public RangePart(RangePart part) {
            this.currentWorkflow = part.currentWorkflow;
            this.x = part.x;
            this.m = part.m;
            this.a = part.a;
            this.s = part.s;
        }

        public List<RangePart> splitA(long value, boolean ruleGreater, String nextWorkflow) {
            if (a.contains(value)) {
                var left = new RangePart(this);
                left.a = Range.between(a.getMinimum(), value);
                var right = new RangePart(this);
                right.a = Range.between(value + 1, a.getMaximum());

                if (!ruleGreater) {
                    left.currentWorkflow = nextWorkflow;
                } else {
                    right.currentWorkflow = nextWorkflow;
                }

                return List.of(left, right);
            } else {
                if (value < a.getMaximum() && ruleGreater || value > a.getMaximum() && !ruleGreater) {
                    this.currentWorkflow = nextWorkflow;
                }
                return List.of(this);
            }
        }

        public List<RangePart> splitM(long value, boolean ruleGreater, String nextWorkflow) {
            if (m.contains(value)) {
                var left = new RangePart(this);
                left.m = Range.between(m.getMinimum(), value);
                var right = new RangePart(this);
                right.m = Range.between(value + 1, m.getMaximum());

                if (!ruleGreater) {
                    left.currentWorkflow = nextWorkflow;
                } else {
                    right.currentWorkflow = nextWorkflow;
                }

                return List.of(left, right);
            } else {
                if (value < m.getMaximum() && ruleGreater || value > m.getMaximum() && !ruleGreater) {
                    this.currentWorkflow = nextWorkflow;
                }
                return List.of(this);
            }
        }

        public List<RangePart> splitS(long value, boolean ruleGreater, String nextWorkflow) {
            if (s.contains(value)) {
                var left = new RangePart(this);
                left.s = Range.between(s.getMinimum(), value);
                var right = new RangePart(this);
                right.s = Range.between(value + 1, s.getMaximum());

                if (!ruleGreater) {
                    left.currentWorkflow = nextWorkflow;
                } else {
                    right.currentWorkflow = nextWorkflow;
                }

                return List.of(left, right);
            } else {
                if (value < s.getMaximum() && ruleGreater || value > s.getMaximum() && !ruleGreater) {
                    this.currentWorkflow = nextWorkflow;
                }
                return List.of(this);
            }
        }

        public List<RangePart> splitX(long value, boolean ruleGreater, String nextWorkflow) {
            if (x.contains(value)) {
                var left = new RangePart(this);
                left.x = Range.between(x.getMinimum(), value);
                var right = new RangePart(this);
                right.x = Range.between(value + 1, x.getMaximum());

                if (!ruleGreater) {
                    left.currentWorkflow = nextWorkflow;
                } else {
                    right.currentWorkflow = nextWorkflow;
                }

                return List.of(left, right);
            } else {
                if (value < x.getMaximum() && ruleGreater || value > x.getMaximum() && !ruleGreater) {
                    this.currentWorkflow = nextWorkflow;
                }
                return List.of(this);
            }
        }

        public long getSize() {
            return (x.getMaximum() - x.getMinimum() + 1) * (m.getMaximum() - m.getMinimum() + 1) * (a.getMaximum() - a.getMinimum() + 1) * (s.getMaximum() - s.getMinimum() + 1);
        }
    }
}
