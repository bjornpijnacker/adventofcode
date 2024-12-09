package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@SolutionDay(year = 2024, day = 9)
@Slf4j
public class Day9 extends AdventOfCodeSolution {

    public Day9(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var fs = parseInput(input);

        // REORDER FILES
        for (int i = 0; i < fs.size(); i++) {
            if (fs.get(i).type == Block.Type.FILE) {
                continue;
            }
            var space = fs.get(i);

            var fillers = new ArrayList<Block>();

            for (int j = fs.size() - 1; j >= 0; j--) {
                if (j < i) {
                    break; // don't go moving stuff later in the list
                }
                if (fs.get(j).type == Block.Type.FILE) {
                    fillers.add(fs.get(j));  // index from end
                    if (fillers.stream().mapToInt(f -> f.size).sum() >= space.size) {
                        break;
                    }
                }
            }

            for (var filler : fillers) {
                if (filler.size <= space.size) {
                    fs.remove(filler);
                    fs.add(i++, filler);
                    space.size -= filler.size;
                } else {  // need to split up the filler; should only occur for last item
                    var newBlock = new Block(filler.id, space.size, Block.Type.FILE);
                    fs.add(i++, newBlock);
                    filler.size -= space.size;
                    space.size -= newBlock.size;
                }
            }
            if (space.size <= 0) {
                fs.remove(space);
                i--;  // compensate for removing the space
            }
        }

        return "%d".formatted(calculateChecksum(fs));
    }

    protected String runSolutionB(String input) {
        var fs = parseInput(input);

        // REORDER FILES
        for (int i = fs.size() - 1; i >= 0; i--) {
            var file = fs.get(i);  // <- file with the highest file number; move to the left
            if (file.type == Block.Type.SPACE) {
                continue;
            }

            // FIND THE FIRST SPACE THAT WILL FIT
            var space = fs.stream().filter(f -> f.type == Block.Type.SPACE && f.size >= file.size).findFirst();
            if (space.isEmpty()) {
                continue;  // cannot move file, doesn't fit anywhere else
            }

            var index = fs.indexOf(space.get());
            if (index > i) {
                continue;  // don't move it to the right
            }
            fs.remove(file);
            fs.add(i, new Block(0, file.size, Block.Type.SPACE));  // add space where file was removed
            i++;  // compensate for moving the file
            fs.add(index, file);
            space.get().size -= file.size;

            // MERGE CONTIGUOUS SPACES
            // DOESN'T END UP BEING NECESSARY

//            for (int j = 0; j < fs.size() - 1; j++) {
//                var a = fs.get(j);
//                var b = fs.get(j + 1);
//                if (a.type == Block.Type.SPACE && b.type == Block.Type.SPACE) {
//                    a.size += b.size;
//                    fs.remove(b);
//                    j--;
//                }
//            }

        }

        return "%d".formatted(calculateChecksum(fs));
    }

    private List<Block> parseInput(String input) {
        var chars = input.lines().findFirst().get().toCharArray();
        List<Block> fs = new ArrayList<>();

        // READ INPUT
        boolean isSpace = false;
        int id = 0;
        for (var ch : chars) {
            fs.add(isSpace
                   ? new Block(0, ch - 48, Block.Type.SPACE)
                   : new Block(id++, ch - 48, Block.Type.FILE));
            isSpace = !isSpace;
        }

        return fs;
    }

    private long calculateChecksum(List<Block> fs) {
        int pos = 0;
        AtomicLong checksum = new AtomicLong();
        for (var block : fs) {
            if (block.type == Block.Type.FILE) {
                IntStream.range(pos, pos + block.size).forEach(p -> checksum.addAndGet((long) p * (long) block.id));
            }
            pos += block.size;
        }
        return checksum.get();
    }

    private void logFs(List<Block> fs) {
        var sb = new StringBuilder();
        fs.forEach(f -> sb.append((String.format("%-6s", f.type == Block.Type.SPACE ? "." : f.id)).repeat(f.size)));
        log.debug(sb.toString());
    }

    @Data
    private static class Block {
        private int id;

        private int size;

        private Type type;

        private UUID uid = UUID.randomUUID();

        public Block(int id, int size, Type type) {
            this.id = id;
            this.size = size;
            this.type = type;
        }

        public enum Type {
            FILE,
            SPACE
        }
    }
}
