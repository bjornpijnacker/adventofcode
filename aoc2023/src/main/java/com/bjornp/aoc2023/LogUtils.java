package com.bjornp.aoc2023;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class LogUtils {
    public void prettyInfo(String value) {
        log.info("┌%s┐".formatted(new String(new char[value.length() + 4]).replace('\0', '─')));
        log.info("│  %s  │".formatted(value));
        log.info("└%s┘".formatted(new String(new char[value.length() + 4]).replace('\0', '─')));
    }
}
