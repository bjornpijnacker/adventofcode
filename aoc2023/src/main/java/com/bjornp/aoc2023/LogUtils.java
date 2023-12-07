package com.bjornp.aoc2023;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

@UtilityClass
public class LogUtils {
    public void prettyInfo(String value, Logger log) {
        log.info("┌%s┐".formatted(new String(new char[value.length() + 4]).replace('\0', '─')));
        log.info("│  %s  │".formatted(value));
        log.info("└%s┘".formatted(new String(new char[value.length() + 4]).replace('\0', '─')));
    }
}
