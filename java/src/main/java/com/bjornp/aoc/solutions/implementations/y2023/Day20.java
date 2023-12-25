package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.*;
import java.util.regex.Pattern;

@SolutionDay(year = 2023, day = 20)
@Slf4j
public class Day20 extends AdventOfCodeSolution {
    public static Map<String, Module> modules = new HashMap<>();
    private static LinkedList<Module> processQueue = new LinkedList<>();
    private static long numHigh = 0;
    private static long numLow = 0;

    public Day20() {
        super(20, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var pattern = Pattern.compile("^(?<type>[%&]?)(?<name>[a-z]+) -> (?<dest>.+)$");

        var lines = input.split("\n");

        var broadcastDests = new ArrayList<String>();
        var flipflops = new ArrayList<FlipFlop>();
        var conjunctions = new ArrayList<Conjunction>();

        for (String line : lines) {
            var matcher = pattern.matcher(line);
            if (matcher.find()) {
                String type;
                try {
                    type = matcher.group("type");
                } catch (IllegalArgumentException e) {
                    type = "";
                }

                var name = matcher.group("name");
                var dest = matcher.group("dest");

                if (type.isEmpty()) {
                    broadcastDests.addAll(Arrays.asList(dest.split(", ")));
                } else if (type.equals("%")) {
                    flipflops.add(new FlipFlop(name, Arrays.asList(dest.split(", "))));
                } else if (type.equals("&")) {
                    conjunctions.add(new Conjunction(name, Arrays.asList(dest.split(", "))));
                }
            }
        }

        for (Conjunction c : conjunctions) {
            var sources = new ArrayList<String>();
            broadcastDests.stream().filter(s -> s.equals(c.getId())).forEach(s -> sources.add(s));
            flipflops.stream().filter(s -> s.getDestination().contains(c.getId())).forEach(s -> sources.add(s.getId()));
            conjunctions.stream().filter(s -> s.getDestination().contains(c.getId())).forEach(s -> sources.add(s.getId()));
            log.info(String.valueOf(sources));
            c.initialize(sources);
            modules.put(c.getId(), c);
        }

        for (FlipFlop f : flipflops) {
            modules.put(f.getId(), f);
        }

        for (int i = 0; i < 1000; ++i) {
            log.info("ROUND {}", i);
            log.info("{} -{}-> {}", "button", "low", "broadcast");
            numLow += broadcastDests.size() + 1;
            broadcastDests.forEach(s -> {
                log.info("{} -{}-> {}", "broadcast", "low", s);
                modules.get(s).pulse("broadcast", false);
                processQueue.add(modules.get(s));
            });

            while (!processQueue.isEmpty()) {
//                log.info(String.valueOf(processQueue));
                processQueue.poll().sendPulse();
            }
        }

        log.info(String.valueOf(numLow));
        log.info(String.valueOf(numHigh));

        return "%,d".formatted(numLow * numHigh);
    }

    public String runSolutionB(String input) {
        /*
         * Step 1: read input
         */
        var pattern = Pattern.compile("^(?<type>[%&]?)(?<name>[a-z]+) -> (?<dest>.+)$");

        var lines = input.split("\n");

        var broadcastDests = new ArrayList<String>();
        var flipflops = new ArrayList<FlipFlop>();
        var conjunctions = new ArrayList<Conjunction>();

        for (String line : lines) {
            var matcher = pattern.matcher(line);
            if (matcher.find()) {
                String type;
                try {
                    type = matcher.group("type");
                } catch (IllegalArgumentException e) {
                    type = "";
                }

                var name = matcher.group("name");
                var dest = matcher.group("dest");

                if (type.isEmpty()) {
                    broadcastDests.addAll(Arrays.asList(dest.split(", ")));
                } else if (type.equals("%")) {
                    flipflops.add(new FlipFlop(name, Arrays.asList(dest.split(", "))));
                } else if (type.equals("&")) {
                    conjunctions.add(new Conjunction(name, Arrays.asList(dest.split(", "))));
                }
            }
        }

        for (Conjunction c : conjunctions) {
            var sources = new ArrayList<String>();
            broadcastDests.stream().filter(s -> s.equals(c.getId())).forEach(s -> sources.add(s));
            flipflops.stream().filter(s -> s.getDestination().contains(c.getId())).forEach(s -> sources.add(s.getId()));
            conjunctions.stream().filter(s -> s.getDestination().contains(c.getId())).forEach(s -> sources.add(s.getId()));
            c.initialize(sources);
            modules.put(c.getId(), c);
        }

        for (FlipFlop f : flipflops) {
            modules.put(f.getId(), f);
        }

        /*
         * Step 2: cluster detection
         */

        var clusters = new ArrayList<Pair<String, HashSet<String>>>();
        broadcastDests.forEach(clusterStart -> {
            var cluster = new HashSet<String>();
            var q = new LinkedList<Module>();

            cluster.add(clusterStart);
            q.add(modules.get(clusterStart));

            while (!q.isEmpty()) {
                var m = q.poll();
                for (var dest : m.getDestination()) {
                    if (!cluster.contains(dest) && modules.get(dest) != null) {
                        cluster.add(dest);
                        q.add(modules.get(dest));
                    }
                }
            }

            clusters.add(Pair.of(clusterStart, cluster));
        });

        log.info(String.valueOf(clusters));

        /*
         * Step 3: Run clusters
         */

        var clusterCycleLengths = new ArrayList<Long>();

        clusters.forEach(clusterPair -> {
            var start = clusterPair.getLeft();
            var target = "dn";

            var i = 0;
            var isTrue = false;
            outer: while (true) {
                modules.get(start).pulse("broadcaster", false);
                processQueue.add(modules.get(start));

                while (!processQueue.isEmpty()) {
                    var before = ((Conjunction) modules.get(target)).getStates().hashCode();
                    processQueue.poll().sendPulse();
                    var end = ((Conjunction) modules.get(target)).getStates().hashCode();
                    if (before != end) {
                        if (!isTrue) {
                            log.info(String.valueOf(((Conjunction) modules.get(target)).getStates()));
                            isTrue = true;
                        } else {
                            log.info(String.valueOf(((Conjunction) modules.get(target)).getStates()));
                            clusterCycleLengths.add((long) i + 1);
                            break outer;
                        }
                    }
                }

                ++i;
            }
        });

        return "%,d".formatted(clusterCycleLengths.stream().reduce(1L, ArithmeticUtils::lcm));
    }

    @Getter
    @ToString
    public abstract static class Module {
        private final String id;
        private final List<String> destination;

        protected Module(String id, List<String> destination) {
            this.id = id;
            this.destination = destination;
        }

        abstract public void pulse(String source, boolean high);
        abstract public void sendPulse();
    }

    @Getter
    public static class Void extends Module {
        public Void() {
            super("null", List.of());
        }

        @Override
        public void pulse(String source, boolean high) {}

        @Override
        public void sendPulse() {}
    }

    @Getter
    public static class FlipFlop extends Module {
        private boolean state = false;
        private LinkedList<Boolean> previousSignals = new LinkedList<>();

        public FlipFlop(String id, List<String> destination) {
            super(id, destination);
        }

        @Override
        public void pulse(String source, boolean high) {
            if (!high) {
                state = !state;
            }
            previousSignals.add(high);
        }

        public void sendPulse() {
            if (previousSignals.poll()) {
                return;
            }
            if (state) {
                numHigh += this.getDestination().size();
            } else {
                numLow += this.getDestination().size();
            }
            this.getDestination().forEach(dest -> {
//                log.info("{} -{}-> {}", this.getId(), state ? "high" : "low", dest);
                modules.getOrDefault(dest, new Void()).pulse(this.getId(), state);
                processQueue.add(modules.getOrDefault(dest, new Void()));
            });
        }
    }

    @Getter
    public static class Conjunction extends Module {
        private Map<String, Boolean> states = new HashMap<>();

        public Conjunction(String id, List<String> destination) {
            super(id, destination);
        }

        @Override
        public void pulse(String source, boolean high) {
            states.put(source, high);
        }

        public void initialize(List<String> sources) {
            for (String source : sources) {
                states.put(source, false);
            }
        }

        public void sendPulse() {
            var state = !states.values().stream().allMatch(b -> b);
            if (state) {
                numHigh += this.getDestination().size();
            } else {
                numLow += this.getDestination().size();
            }
            this.getDestination().forEach(dest -> {
//                log.info("{} -{}-> {}", this.getId(), state ? "high" : "low", dest);
                modules.getOrDefault(dest, new Void()).pulse(this.getId(), state);
                processQueue.add(modules.getOrDefault(dest, new Void()));
            });
        }
    }
}
