package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Direction2D;
import com.bjornp.aoc.util.Grid2D;
import com.bjornp.aoc.util.IntVector2D;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.Ansi;

import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

@SolutionDay(year = 2024, day = 15)
@Slf4j
public class Day15 extends AdventOfCodeSolution {

    public Day15(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private long score(Grid2D<Tile> grid) {
        return grid.findAll(Tile.BOX).stream()
                .mapToLong(box -> box.getY() * 100L + box.getX())
                .sum();
    }

    private long scoreWide(Grid2D<WideTile> grid) {
        return grid.findAll(WideTile.LBOX).stream()
                .mapToLong(box -> box.getY() * 100L + box.getX())
                .sum();
    }

    private Grid2D<WideTile> widenGrid(Grid2D<Tile> grid) {
        var doubleGrid = new Grid2D<>(grid.getWidth() * 2, grid.getHeight(), WideTile.NONE);

        for (int x = 0; x < grid.getWidth(); ++x) {
            for (int y = 0; y < grid.getHeight(); ++y) {
                var p = new IntVector2D(x, y);
                var p2 = new IntVector2D(x * 2, y);

                switch (grid.get(p)) {
                    case NONE:
                        continue;
                    case BOX:
                        doubleGrid.set(p2, WideTile.LBOX);
                        doubleGrid.set(p2.e(), WideTile.RBOX);
                        break;
                    case ROBOT:
                        doubleGrid.set(p2, WideTile.ROBOT);
                        break;
                    case WALL:
                        doubleGrid.set(p2, WideTile.WALL);
                        doubleGrid.set(p2.e(), WideTile.WALL);
                        break;
                }
            }
        }

        return doubleGrid;
    }

    protected String runSolutionA(String input) {
        var grid = parseInputGrid(input.split("\n\n")[0]);
        var movements = parseInputMovements(input.split("\n\n")[1]);

        var robot = grid.findAll(Tile.ROBOT).stream().findAny().orElseThrow(() -> new IllegalStateException("No robot!"));

        for (var step : movements) {
            var toMove = new Stack<IntVector2D>();
            toMove.add(robot);

            // add elements to move to the stack until we find anything that's not a box
            do {
                toMove.push(toMove.peek().move(step));
            } while (grid.get(toMove.peek()) == Tile.BOX);

            if (grid.get(toMove.peek()) == Tile.NONE) {  // we can move!
                while (toMove.size() >= 2) {
                    grid.swap(toMove.pop(), toMove.peek());
                }
                robot = robot.move(step);
            }
        }

        log.debug(String.valueOf(grid));
        return "%d".formatted(score(grid));
    }

    protected String runSolutionB(String input) {
        var grid = widenGrid(parseInputGrid(input.split("\n\n")[0]));
        var movements = parseInputMovements(input.split("\n\n")[1]);

        var robot = grid.findAll(WideTile.ROBOT).stream().findAny().orElseThrow(() -> new IllegalStateException("No robot!"));

        for (var step : movements) {
            var toMove = new Stack<Set<IntVector2D>>();
            toMove.add(Set.of(robot));

            // add elements to move to the stack until we find anything that's not a box
            do {
                var next = new HashSet<IntVector2D>();
                var peek = toMove.peek();
                for (var p : peek) {
                    if (grid.get(p) == WideTile.NONE) continue;
                    if (step == Direction2D.NORTH || step == Direction2D.SOUTH) {
                        var pn = p.move(step);
                        next.add(pn);
                        if (grid.get(pn) == WideTile.LBOX) {
                            next.add(pn.e());
                        } else if (grid.get(pn) == WideTile.RBOX) {
                            next.add(pn.w());
                        }
                    } else {
                        next.add(p.move(step));
                    }
                }
                if (next.isEmpty()) break;
                toMove.push(next);
            } while (toMove.peek().stream().allMatch(tm -> grid.get(tm) != WideTile.WALL));

            if (toMove.peek().stream().noneMatch(tm -> grid.get(tm) == WideTile.WALL)) {  // we can move!
                while (!toMove.isEmpty()) {
                    var tms = toMove.pop();
                    for (var tm : tms) {
                        if (grid.get(tm) != WideTile.NONE) {
                            grid.swap(tm, tm.move(step));
                        }
                    }
                }
                robot = robot.move(step);
            }
        }

        log.info(String.valueOf(grid));
        return "%d".formatted(scoreWide(grid));
    }

    private Grid2D<Tile> parseInputGrid(String grid) {
        return new Grid2D<>(grid
                .lines()
                .map(line -> Arrays.stream(line.split("")).map(Tile::fromCh).toArray(Tile[]::new))
                .toArray(Tile[][]::new));
    }

    private List<Direction2D> parseInputMovements(String movements) {
        return movements.chars().filter(ch -> ch != '\n').mapToObj(ch -> {
            return switch (ch) {
                case '<' -> Direction2D.WEST;
                case '^' -> Direction2D.NORTH;
                case '>' -> Direction2D.EAST;
                case 'v' -> Direction2D.SOUTH;
                default -> throw new IllegalArgumentException();
            };
        }).toList();
    }

    @AllArgsConstructor
    enum Tile {
        NONE(".", null),
        ROBOT("@", Ansi.Color.GREEN),
        WALL("#", null),
        BOX("O", Ansi.Color.YELLOW);

        public final String ch;

        public final Ansi.Color color;

        public static Tile fromCh(String ch) {
            return switch (ch) {
                case "." -> Tile.NONE;
                case "@" -> Tile.ROBOT;
                case "#" -> Tile.WALL;
                case "O" -> Tile.BOX;
                default -> throw new IllegalArgumentException("Invalid argument to Tile::fromCh");
            };
        }


        @Override
        public String toString() {
            if (color != null) {
                return ansi().fg(color).a(ch).reset().toString();
            } else {
                return ch;
            }
        }
    }

    @AllArgsConstructor
    enum WideTile {
        NONE(".", null),
        ROBOT("@", Ansi.Color.GREEN),
        WALL("#", null),
        LBOX("[", Ansi.Color.YELLOW),
        RBOX("]", Ansi.Color.YELLOW);

        public final String ch;

        public final Ansi.Color color;

        @Override
        public String toString() {
            if (color != null) {
                return ansi().fg(color).a(ch).reset().toString();
            } else {
                return ch;
            }
        }
    }
}
