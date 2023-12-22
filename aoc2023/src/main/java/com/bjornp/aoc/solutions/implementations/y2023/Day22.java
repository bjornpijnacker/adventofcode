package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@SolutionDay(year = 2023, day = 22)
@Slf4j
public class Day22 extends AdventOfCodeSolution {
    private HashMap<IntVector3D, Brick> brickMatrix = new HashMap<>();
    private ArrayList<Brick> bricks = new ArrayList<>();

    public Day22() {
        super(22, 2023);
        
        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private void prepare(String input) {
        var pattern = Pattern.compile("(?<x1>\\d+),(?<y1>\\d+),(?<z1>\\d+)~(?<x2>\\d+),(?<y2>\\d+),(?<z2>\\d+)");

        // calculate bricks
        AtomicInteger character = new AtomicInteger('A');

        bricks.addAll(Arrays.stream(input.split("\n")).map(line -> {
            var matcher = pattern.matcher(line);
            if (matcher.matches()) {
                // obtain brick
                var brick = Brick.builder()
                        .name(String.valueOf((char) character.getAndIncrement()))
                        .start(new IntVector3D(
                                Integer.parseInt(matcher.group("x1")),
                                Integer.parseInt(matcher.group("y1")),
                                Integer.parseInt(matcher.group("z1"))
                        ))
                        .end(new IntVector3D(
                                Integer.parseInt(matcher.group("x2")),
                                Integer.parseInt(matcher.group("y2")),
                                Integer.parseInt(matcher.group("z2"))
                        ))
                        .build();

                // put Brick in the sparse grid
                if (brick.getStart().x != brick.getEnd().x) {
                    for (int x = brick.getStart().x; x <= brick.getEnd().x; x++) {
                        brickMatrix.put(new IntVector3D(x, brick.getStart().y, brick.getStart().z), brick);
                    }
                } else if (brick.getStart().y != brick.getEnd().y) {
                    for (int y = brick.getStart().y; y <= brick.getEnd().y; y++) {
                        brickMatrix.put(new IntVector3D(brick.getStart().x, y, brick.getStart().z), brick);
                    }
                } else if (brick.getStart().z != brick.getEnd().z) {
                    for (int z = brick.getStart().z; z <= brick.getEnd().z; z++) {
                        brickMatrix.put(new IntVector3D(brick.getStart().x, brick.getStart().y, z), brick);
                    }
                } else {
                    brickMatrix.put(brick.getStart(), brick);
                }


                return brick;
            } else {
                throw new RuntimeException("Invalid input");
            }
        }).toList());

        this.settle(this.brickMatrix, this.bricks);
    }

    private long settle(HashMap<IntVector3D, Brick> matrix, List<Brick> bricks) {
        Set<String> settled = new HashSet<>();
        // settle bricks
        var fallQueue = new LinkedList<>(bricks);
        while (!fallQueue.isEmpty()) {
            var brick = fallQueue.poll();

            // check if the brick is supported
            var support = findSupports(brick, matrix);
            var supports = support.getLeft();
            var supportedBy = support.getRight();

            if (supportedBy.isEmpty() && brick.getStart().z > 1) {
                this.removeFromMatrix(brick, matrix);

                brick.goDown();
                settled.add(brick.getName());

                this.placeInMatrix(brick, matrix);

                fallQueue.add(brick);
                fallQueue.addAll(supports);
            }
        }
        return settled.size();
    }

    private String runSolutionA(String input) {
        this.prepare(input);

        // find killable bricks
        var sum = 0;
        for (var brick : bricks) {
            var support = findSupports(brick, brickMatrix);
            var supports = support.getLeft();
            var supportedBy = support.getRight();

            var canBeKilled = false;

            if (!supports.isEmpty()) {
                canBeKilled = supports.stream().allMatch(supportingBrick -> {
                    var supportingSupport = findSupports(supportingBrick, brickMatrix);
                    var supportingSupports = supportingSupport.getLeft();
                    var supportingSupportedBy = supportingSupport.getRight();

                    return supportingSupportedBy.size() > 1;  // they are supported by another brick
                });
            } else {
                canBeKilled = true;
            }

            if (canBeKilled) {
                sum++;
            };
        }

        return "%,d".formatted(sum);
    }

    private String runSolutionB(String input) {
        this.prepare(input);

        // find killable bricks
        var sum = 0;
        for (var brick : bricks) {
            var tempMatrix = SerializationUtils.clone(this.brickMatrix);
            var tempBricks = SerializationUtils.clone(this.bricks);
            var relativeBrick = tempBricks.stream().filter(b -> b.getName().equals(brick.getName())).findFirst().get();
            removeFromMatrix(relativeBrick, tempMatrix);
            tempBricks.remove(relativeBrick);
            var moved = settle(tempMatrix, tempBricks);
            sum += moved;
        }

        return "%,d".formatted(sum);
    }

    private void placeInMatrix(Brick brick, HashMap<IntVector3D, Brick> brickMatrix) {
        if (brick.getStart().x != brick.getEnd().x) {
            for (int x = brick.getStart().x; x <= brick.getEnd().x; x++) {
                brickMatrix.put(new IntVector3D(x, brick.getStart().y, brick.getStart().z), brick);
            }
        } else if (brick.getStart().y != brick.getEnd().y) {
            for (int y = brick.getStart().y; y <= brick.getEnd().y; y++) {
                brickMatrix.put(new IntVector3D(brick.getStart().x, y, brick.getStart().z), brick);
            }
        } else if (brick.getStart().z != brick.getEnd().z) {
            for (int z = brick.getStart().z; z <= brick.getEnd().z; z++) {
                brickMatrix.put(new IntVector3D(brick.getStart().x, brick.getStart().y, z), brick);
            }
        } else {
            brickMatrix.put(brick.getStart(), brick);
        }
    }

    private void removeFromMatrix(Brick brick, HashMap<IntVector3D, Brick> brickMatrix) {
        if (brick.getStart().x != brick.getEnd().x) {
            for (int x = brick.getStart().x; x <= brick.getEnd().x; x++) {
                brickMatrix.put(new IntVector3D(x, brick.getStart().y, brick.getStart().z), null);
            }
        } else if (brick.getStart().y != brick.getEnd().y) {
            for (int y = brick.getStart().y; y <= brick.getEnd().y; y++) {
                brickMatrix.put(new IntVector3D(brick.getStart().x, y, brick.getStart().z), null);
            }
        } else if (brick.getStart().z != brick.getEnd().z) {
            for (int z = brick.getStart().z; z <= brick.getEnd().z; z++) {
                brickMatrix.put(new IntVector3D(brick.getStart().x, brick.getStart().y, z), null);
            }
        } else {
            brickMatrix.put(brick.getStart(), null);
        }
    }

    private Pair<Set<Brick>, Set<Brick>> findSupports(Brick brick, HashMap<IntVector3D, Brick> brickMatrix) {
        var supportedBy = new HashSet<Brick>();
        var supports = new HashSet<Brick>();

        if (brick.getStart().x != brick.getEnd().x) {
            for (int x = brick.getStart().x; x <= brick.getEnd().x; x++) {
                var below = brickMatrix.get(new IntVector3D(x, brick.getStart().y, brick.getStart().z - 1));
                if (below != null) {
                    supportedBy.add(below);
                }
                var above = brickMatrix.get(new IntVector3D(x, brick.getStart().y, brick.getStart().z + 1));
                if (above != null) {
                    supports.add(above);
                }
            }
        } else if (brick.getStart().y != brick.getEnd().y) {
            for (int y = brick.getStart().y; y <= brick.getEnd().y; y++) {
                var below = brickMatrix.get(new IntVector3D(brick.getStart().x, y, brick.getStart().z - 1));
                if (below != null) {
                    supportedBy.add(below);
                }
                var above = brickMatrix.get(new IntVector3D(brick.getStart().x, y, brick.getStart().z + 1));
                if (above != null) {
                    supports.add(above);
                }
            }
        } else {
            var below = brickMatrix.get(new IntVector3D(brick.getStart().x, brick.getStart().y, brick.getStart().z - 1));
            if (below != null) {
                supportedBy.add(below);
            }
            var above = brickMatrix.get(new IntVector3D(brick.getStart().x, brick.getStart().y, brick.getEnd().z + 1));
            if (above != null) {
                supports.add(above);
            }
        }
        return Pair.of(supports, supportedBy);
    }
    
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class IntVector3D implements Serializable {
        @EqualsAndHashCode.Include
        private int x;

        @EqualsAndHashCode.Include
        private int y;

        @EqualsAndHashCode.Include
        private int z;

        public IntVector3D add(IntVector3D other) {
            return new IntVector3D(x + other.x, y + other.y, z + other.z);
        }

        public List<IntVector3D> neighbors() {
            return List.of(left(), right(), above(), below(), forward(), backward());
        }

        public IntVector3D left() {
            return new IntVector3D(x - 1, y, z);
        }

        public IntVector3D right() {
            return new IntVector3D(x + 1, y, z);
        }

        public IntVector3D forward() {
            return new IntVector3D(x, y - 1, z);
        }

        public IntVector3D backward() {
            return new IntVector3D(x, y + 1, z);
        }

        public IntVector3D above() {
            return new IntVector3D(x, y, z + 1);
        }

        public IntVector3D below() {
            return new IntVector3D(x, y, z - 1);
        }

        @Override
        public String toString() {
            return "<%,d, %,d, %,d>".formatted(x, y, z);
        }
    }

    @Data
    @Builder
    @EqualsAndHashCode
    private static class Brick implements Serializable {
        @EqualsAndHashCode.Exclude
        private String name;
        private IntVector3D start;
        private IntVector3D end;

        public void goDown() {
            start = start.below();
            end = end.below();
        }
    }
    
}
