package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.microsoft.z3.Context;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.stream.IntStream;

@Slf4j
@SolutionDay(day = 24, year = 2023)
public class Day24 extends AdventOfCodeSolution {
    public Day24() {
        super(24, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var boundary = Range.between(200000000000000.0, 400000000000000.0);

        var hailStorm = input.lines().map(line -> {
            var split = line.split("[,@]");
            return new Hail(new Vector3D(Double.parseDouble(split[0].trim()),
                    Double.parseDouble(split[1].trim()),
                    Double.parseDouble(split[2].trim())
            ), new Vector3D(Double.parseDouble(split[3].trim()),
                    Double.parseDouble(split[4].trim()),
                    Double.parseDouble(split[5].trim())
            ));
        }).toList();

        var sum = 0L;
        for (int i = 0; i < hailStorm.size(); i++) {
            for (int j = i + 1; j < hailStorm.size(); j++) {
                var intersection = hailStorm.get(i).intersect(hailStorm.get(j));
                if (intersection != null && hailStorm.get(i).isFuture(intersection) && hailStorm.get(j)
                        .isFuture(intersection) && boundary.contains(intersection.getX()) && boundary.contains(
                        intersection.getY())) {
                    log.info("Intersection at {} between {} and {}", intersection, hailStorm.get(i), hailStorm.get(j));
                    ++sum;
                }
            }
        }

        return "%,d".formatted(sum);
    }

    public String runSolutionB(String input) {
        var hailStorm = input.lines().map(line -> {
            var split = line.split("[,@]");
            return new Hail(new Vector3D(Double.parseDouble(split[0].trim()),
                    Double.parseDouble(split[1].trim()),
                    Double.parseDouble(split[2].trim())
            ), new Vector3D(Double.parseDouble(split[3].trim()),
                    Double.parseDouble(split[4].trim()),
                    Double.parseDouble(split[5].trim())
            ));
        }).toList();

        try (Context ctx = new Context()) {
            var solver = ctx.mkSolver();
            var pxi = ctx.mkSymbol("pxi");
            var pyi = ctx.mkSymbol("pyi");
            var pzi = ctx.mkSymbol("pzi");
            var vxi = ctx.mkSymbol("vxi");
            var vyi = ctx.mkSymbol("vyi");
            var vzi = ctx.mkSymbol("vzi");
            var ts = IntStream.range(0, hailStorm.size()).mapToObj(ctx::mkSymbol).toList();

            for (int i = 0; i < hailStorm.size(); ++i) {
                var hail = hailStorm.get(i);
                var leftX = ctx.mkAdd(ctx.mkReal((long) hail.position.getX()),
                        ctx.mkMul(ctx.mkReal((long) hail.velocity.getX()), ctx.mkRealConst(ts.get(i)))
                );
                var rightX = ctx.mkAdd(ctx.mkRealConst(pxi),
                        ctx.mkMul(ctx.mkRealConst(vxi), ctx.mkRealConst(ts.get(i)))
                );
                solver.add(ctx.mkEq(leftX, rightX));

                var leftY = ctx.mkAdd(ctx.mkReal((long) hail.position.getY()),
                        ctx.mkMul(ctx.mkReal((long) hail.velocity.getY()), ctx.mkRealConst(ts.get(i)))
                );
                var rightY = ctx.mkAdd(ctx.mkRealConst(pyi),
                        ctx.mkMul(ctx.mkRealConst(vyi), ctx.mkRealConst(ts.get(i)))
                );
                solver.add(ctx.mkEq(leftY, rightY));

                var leftZ = ctx.mkAdd(ctx.mkReal((long) hail.position.getZ()),
                        ctx.mkMul(ctx.mkReal((long) hail.velocity.getZ()), ctx.mkRealConst(ts.get(i)))
                );
                var rightZ = ctx.mkAdd(ctx.mkRealConst(pzi),
                        ctx.mkMul(ctx.mkRealConst(vzi), ctx.mkRealConst(ts.get(i)))
                );
                solver.add(ctx.mkEq(leftZ, rightZ));
            }

            solver.check();
            var resXYZ = solver.getModel()
                    .eval(ctx.mkAdd(ctx.mkRealConst(pxi), ctx.mkRealConst(pyi), ctx.mkRealConst(pzi)), true);
            log.info("{}", resXYZ);
        }

        return null;
    }

    @AllArgsConstructor
    @ToString
    private static class Hail {
        private final Vector3D position;

        private final Vector3D velocity;

        public Vector2D intersect(Hail other) {
            var thisEquation = this.getLinearEquation();
            var otherEquation = other.getLinearEquation();
            if (thisEquation.getLeft().equals(otherEquation.getLeft())) {
                return null;  // nowhere or everywhere
            }
            var x = (otherEquation.getRight() - thisEquation.getRight()) / (thisEquation.getLeft() - otherEquation.getLeft());
            var y = thisEquation.getLeft() * (otherEquation.getRight() - thisEquation.getRight()) / (thisEquation.getLeft() - otherEquation.getLeft()) + thisEquation.getRight();
            return new Vector2D(x, y);
        }

        public Pair<Double, Double> getLinearEquation() {
            var slope = velocity.getY() / velocity.getX();
            var intercept = -(velocity.getY() / velocity.getX()) * position.getX() + position.getY();
            return Pair.of(slope, intercept);
        }

        public boolean isFuture(Vector2D point) {
            return velocity.getX() > 0 ? point.getX() > position.getX() : point.getX() < position.getX();
        }
    }
}
