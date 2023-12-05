package com.bjornp.aoc2023;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        log.error("%s\n%s".formatted(e.getMessage(), sw.toString()));
    }
}
