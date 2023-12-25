package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SolutionDay(year = 2023, day = 4)
@Slf4j
public class Day4 extends AdventOfCodeSolution {
    public Day4() {
        super(4, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        return String.valueOf(Arrays.stream(input.split("\n")).mapToInt(card -> {
            var splitCard = card.split("[:|]");
            var winning = Arrays.stream(splitCard[1].trim().split(" "))
                    .filter(StringUtils::isNotBlank)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            var numbers = Arrays.stream(splitCard[2].trim().split(" "))
                    .filter(StringUtils::isNotBlank)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            var intersect = winning.stream().filter(numbers::contains).collect(Collectors.toSet());
            return (int) Math.pow(2, intersect.size() - 1);
        }).sum());
    }

    protected String runSolutionB(String input) {
        var cards = List.of(input.split("\n"));
        var cardMap = new HashMap<Integer, ScratchCard>();
        cards.forEach(card -> {
            var number = Integer.parseInt(card.split(":")[0].substring(5).trim());
            cardMap.put(number, ScratchCard.builder().card(card).build());
        });

        cardMap.forEach((day, card) -> {
            var splitCard = card.getCard().split("[:|]");
            var winning = Arrays.stream(splitCard[1].trim().split(" "))
                    .filter(StringUtils::isNotBlank)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            var numbers = Arrays.stream(splitCard[2].trim().split(" "))
                    .filter(StringUtils::isNotBlank)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            var intersect = winning.stream().filter(numbers::contains).collect(Collectors.toSet());
            for (int j = 1; j < intersect.size() + 1; j++) {
                cardMap.get(day + j).add(card.getDuplicates());
            }
        });

        return String.valueOf(cardMap.entrySet()
                .stream()
                .flatMapToInt(card -> IntStream.of(card.getValue().getDuplicates()))
                .sum());
    }

    @Builder
    @Data
    private static class ScratchCard {
        private String card;

        @Builder.Default
        private int duplicates = 1;

        public void add(int num) {
            this.duplicates += num;
        }
    }
}
