package com.bjornp.aoc;

import com.bjornp.aoc.annotation.SolutionRegistry;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {
    private static final String banner = """
                 _       _                 _            __    ____          _        ____   ___ ____  _____\s
                / \\   __| |_   _____ _ __ | |_    ___  / _|  / ___|___   __| | ___  |___ \\ / _ \\___ \\|___ /\s
               / _ \\ / _` \\ \\ / / _ \\ '_ \\| __|  / _ \\| |_  | |   / _ \\ / _` |/ _ \\   __) | | | |__) | |_ \\\s
              / ___ \\ (_| |\\ V /  __/ | | | |_  | (_) |  _| | |__| (_) | (_| |  __/  / __/| |_| / __/ ___) |
             /_/   \\_\\__,_| \\_/ \\___|_| |_|\\__|  \\___/|_|    \\____\\___/ \\__,_|\\___| |_____|\\___/_____|____/\s
                                                                                                           \s
            """;

    @SneakyThrows
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        final var logger = LoggerFactory.getLogger(Main.class);

        var options = new Options();

        var dayOption = new Option("d", "day", true, "Set AOC day (number)");
        dayOption.setRequired(true);
        options.addOption(dayOption);

        var yearOption = new Option("y", "year", true, "Set AOC year (number)");
        yearOption.setRequired(true);
        options.addOption(yearOption);

        var methodOption = new Option("m", "method", true, "Set method");
        methodOption.setRequired(true);
        methodOption.setType(String.class);
        options.addOption(methodOption);

        var testinputOption = new Option("t", "testinput", false, "Set flag to enable loading test.txt");
        testinputOption.setType(Boolean.class);
        options.addOption(testinputOption);

        var parser = new DefaultParser();
        var formatter = new HelpFormatter();

        int day = 0;
        int year = 0;
        String method = "";
        boolean testInput = false;

        try {
            var command = parser.parse(options, args);

            day = Integer.parseInt(command.getOptionValue(dayOption.getOpt()));
            year = Integer.parseInt(command.getOptionValue(yearOption.getOpt()));
            method = command.getOptionValue(methodOption.getOpt());
            testInput = command.hasOption(testinputOption.getOpt());
        } catch (ParseException | NumberFormatException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("aoc2023", options);
            System.exit(1);
        }

        Arrays.stream(banner.split("\n")).forEach(logger::info);

        logger.info("Running AOC for day {} {}", day, year);

        SolutionRegistry.getInstance().runSolutionsScan();
        SolutionRegistry.getInstance().runSolution(day, year, method, testInput);
    }
}