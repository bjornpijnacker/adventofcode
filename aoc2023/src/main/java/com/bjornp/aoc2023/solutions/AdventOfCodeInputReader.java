package com.bjornp.aoc2023.solutions;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class AdventOfCodeInputReader {
    private final int day;

    public AdventOfCodeInputReader(int day) {
        this.day = day;
    }

    @SneakyThrows
    public String readInputA() {
        return this.readInput("a.txt");
    }

    @SneakyThrows
    public String readInputB() {
        return this.readInput("b.txt");
    }

    @SneakyThrows
    private String readInput(String filename) {
        try (var inputStream = ClassLoader.getSystemResourceAsStream("aoc/%d%s".formatted(day, filename))) {
            if (inputStream == null) {
                throw new FileNotFoundException("File aoc/%d%s does not exist".formatted(day, filename));
            }

            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
