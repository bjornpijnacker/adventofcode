package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SolutionDay(day = 7)
@Slf4j
public class Day7 extends AdventOfCodeSolution {
    private static boolean allowJoker = false;

    public Day7() {
        super(7);

        register("a", this::runSolutionA);
        register("b", (input) -> {
            allowJoker = true;
            return this.runSolutionA(input);
        });
    }

    private String runSolutionA(String input) {
        var games = Arrays.stream(input.split("\n")).map(game -> {
            var cards = game.split(" ")[0].split("");
            var bid = game.split(" ")[1];
            return new Hand(cards, bid);
        }).sorted().toList();

        var sum = 0;
        for (int i = 0; i < games.size(); i++) {
            sum += games.get(i).getBid() * (i + 1);
        }

        return "%,d".formatted(sum);
    }

    @Data
    private static class Hand implements Comparable<Hand> {
        private List<Card> cards;
        private int bid;

        public Hand(String[] cards, String bid) {
            if (cards.length != 5) {
                throw new IllegalArgumentException("Invalid hand length: " + Arrays.toString(cards));
            }
            this.cards = Arrays.stream(cards).map(Card::fromInput).toList();
            this.bid = Integer.parseInt(bid);
        }

        private int getScore(List<Card> cards) {
            var cardCounts = cards.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            if (cardCounts.containsValue(5L)) {
                return 6; // five of a kind
            }
            if (cardCounts.values().containsAll(List.of(1L, 4L))) {
                return 5; // four of a kind
            }
            if (cardCounts.values().containsAll(List.of(2L, 3L))) {
                return 4; // full house
            }
            if (CollectionUtils.isEqualCollection(cardCounts.values(), List.of(1L, 1L, 3L))) {
                return 3; // three of a kind
            }
            if (CollectionUtils.isEqualCollection(cardCounts.values(), List.of(2L, 2L, 1L))) {
                return 2; // two pair
            }
            if (cardCounts.containsValue(2L)) {
                return 1; // one pair
            }
            return 0; // high card
        }

        private int getScore() {
            if (allowJoker) {
                return Arrays.stream(Card.values()).mapToInt(joker -> {
                    var replacedCards = new ArrayList<>(cards);
                    replacedCards.replaceAll(card -> card == Card.J ? joker : card);
                    return this.getScore(replacedCards);
                }).max().orElseThrow();
            } else {
                return this.getScore(cards);
            }
        }

        @Override
        public int compareTo(Hand o) {
            var thisScore = this.getScore();
            var otherScore =  o.getScore();

            if (thisScore != otherScore) {
                return Integer.compare(thisScore, otherScore);
            }

            // check for higher card
            for (int i = 0; i < 5; i++) {
                var thisCard = this.cards.get(i);
                var otherCard = o.cards.get(i);
                if (thisCard != otherCard) {
                    return Integer.compare(thisCard.cardOrdinal(), otherCard.cardOrdinal());
                }
            }

            return 0;
        }
    }

    @RequiredArgsConstructor
    private enum Card {
        A,
        K,
        Q,
        T,
        V9,
        V8,
        V7,
        V6,
        V5,
        V4,
        V3,
        V2,
        J;

        public int cardOrdinal() {
            return switch (this) {
                case A -> 14;
                case K -> 13;
                case Q -> 12;
                case J -> allowJoker ? 0 : 11;
                case T -> 10;
                case V9 -> 9;
                case V8 -> 8;
                case V7 -> 7;
                case V6 -> 6;
                case V5 -> 5;
                case V4 -> 4;
                case V3 -> 3;
                case V2 -> 2;
            };
        }

        public static Card fromInput(String inputCard) {
            return switch (inputCard) {
                case "A" -> Card.A;
                case "K" -> Card.K;
                case "Q" -> Card.Q;
                case "J" -> Card.J;
                case "T" -> Card.T;
                case "9" -> Card.V9;
                case "8" -> Card.V8;
                case "7" -> Card.V7;
                case "6" -> Card.V6;
                case "5" -> Card.V5;
                case "4" -> Card.V4;
                case "3" -> Card.V3;
                case "2" -> Card.V2;
                default -> throw new IllegalArgumentException("Invalid card: " + inputCard);
            };
        }
    }
}
