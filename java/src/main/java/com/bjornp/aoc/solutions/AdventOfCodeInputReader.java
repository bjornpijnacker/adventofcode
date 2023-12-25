package com.bjornp.aoc.solutions;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Builder
public class AdventOfCodeInputReader {
    private final int day;
    private final int year;

    public String readInput() throws IOException {
        var filename = "%d/%d.txt".formatted(year, day);
        return readInput(filename);
    }

    public String readInput(String filename) throws IOException {
        try (var inputStream = ClassLoader.getSystemResourceAsStream("aoc/%s".formatted(filename))) {
            if (inputStream == null) {
                throw new FileNotFoundException("File aoc/%s does not exist".formatted(filename));
            }

            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
