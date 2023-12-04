package com.bjornp.aoc2023.solutions;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class AdventOfCodeInputReader {
    private final int day;

    public String readInput() {
        return this.readInput("%d.txt".formatted(day));
    }

    @SneakyThrows
    public String readInput(String filename) {
        try (var inputStream = ClassLoader.getSystemResourceAsStream("aoc/%s".formatted(filename))) {
            if (inputStream == null) {
                throw new FileNotFoundException("File aoc/%s does not exist".formatted(filename));
            }

            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
