package com.bjornp.aoc2023;

import com.bjornp.aoc2023.annotation.SolutionRegistry;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        final var logger = LoggerFactory.getLogger(Main.class);

        var options = new Options();

        var dayOption = new Option("d", "day", true, "Set AOC day (number)");
        dayOption.setRequired(true);
        options.addOption(dayOption);

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
        String method = "";
        boolean testInput = false;

        try {
            var command = parser.parse(options, args);

            day = Integer.parseInt(command.getOptionValue(dayOption.getOpt()));
            method = command.getOptionValue(methodOption.getOpt());
            testInput = command.hasOption(testinputOption.getOpt());
        } catch (ParseException | NumberFormatException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("aoc2023", options);
            System.exit(1);
        }

        logger.info("Running AOC for day {}", day);

        SolutionRegistry.getInstance().runSolutionsScan();
        SolutionRegistry.getInstance().runSolution(day, method, testInput);
    }
}