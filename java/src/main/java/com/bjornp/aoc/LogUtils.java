package com.bjornp.aoc;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

@UtilityClass
public class LogUtils {
    public void prettyInfo(String value, Logger log) {
        log.info("┌%s┐".formatted(new String(new char[value.length() + 4]).replace('\0', '─')));
        log.info("│  %s  │".formatted(value));
        log.info("└%s┘".formatted(new String(new char[value.length() + 4]).replace('\0', '─')));
    }

    public void printGrid(String[][] grid, Logger log) {
        for (String[] row : grid) {
            StringBuilder sb = new StringBuilder();
            for (String cell : row) {
                sb.append(cell);
            }
            log.info(sb.toString());
        }
    }
}
