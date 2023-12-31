package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@SolutionDay(year = 2015, day = 21)
@Slf4j
public class Day21 extends AdventOfCodeSolution {
    private final List<Item> weapons = List.of(
            new Item(8, 4, 0),
            new Item(10, 5, 0),
            new Item(25, 6, 0),
            new Item(40, 7, 0),
            new Item(74, 8, 0)
    );
    private final List<Item> armor = List.of(
            new Item(13, 0, 1),
            new Item(31, 0, 2),
            new Item(53, 0, 3),
            new Item(75, 0, 4),
            new Item(102, 0, 5),
            new Item(0, 0, 0)
    );
    private final List<Item> rings = List.of(
            new Item(25, 1, 0),
            new Item(50, 2, 0),
            new Item(100, 3, 0),
            new Item(20, 0, 1),
            new Item(40, 0, 2),
            new Item(80, 0, 3),
            new Item(0, 0, 0),
            new Item(0, 0, 0)
    );

    public Day21() {
        super(21, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {
        var lines = input.split("\n");
        var bossHealth = Integer.parseInt(lines[0].split(": ")[1]);
        var bossDamage = Integer.parseInt(lines[1].split(": ")[1]);
        var bossArmor = Integer.parseInt(lines[2].split(": ")[1]);

        var minCost = Integer.MAX_VALUE;

        for (var weapon : weapons) {
            for (var armor : armor) {
                for (var ring1 : rings) {
                    for (var ring2 : rings) {
                        if (ring1 == ring2) {
                            continue;
                        }

                        var cost = weapon.getCost() + armor.getCost() + ring1.getCost() + ring2.getCost();
                        var playerDamage = weapon.getDamage() + armor.getDamage() + ring1.getDamage() + ring2.getDamage();
                        var playerArmor = weapon.getArmor() + armor.getArmor() + ring1.getArmor() + ring2.getArmor();

                        var wins = simulateFight(100, bossHealth, bossDamage, bossArmor, playerDamage, playerArmor);
                        if (wins && cost < minCost) {
                            minCost = cost;
                        }
                    }
                }
            }
        }

        return "%,d".formatted(minCost);
    }

    public String runSolutionB(String input) {
        var lines = input.split("\n");
        var bossHealth = Integer.parseInt(lines[0].split(": ")[1]);
        var bossDamage = Integer.parseInt(lines[1].split(": ")[1]);
        var bossArmor = Integer.parseInt(lines[2].split(": ")[1]);

        var maxCost = Integer.MIN_VALUE;

        for (var weapon : weapons) {
            for (var armor : armor) {
                for (var ring1 : rings) {
                    for (var ring2 : rings) {
                        if (ring1 == ring2) {
                            continue;
                        }

                        var cost = weapon.getCost() + armor.getCost() + ring1.getCost() + ring2.getCost();
                        var playerDamage = weapon.getDamage() + armor.getDamage() + ring1.getDamage() + ring2.getDamage();
                        var playerArmor = weapon.getArmor() + armor.getArmor() + ring1.getArmor() + ring2.getArmor();

                        var wins = simulateFight(100, bossHealth, bossDamage, bossArmor, playerDamage, playerArmor);
                        if (!wins && cost > maxCost) {
                            maxCost = cost;
                        }
                    }
                }
            }
        }

        return "%,d".formatted(maxCost);
    }

    private boolean simulateFight(int playerHealth, int bossHealth, int bossDamage, int bossArmor, int playerDamage, int playerArmor) {
        while (playerHealth > 0 && bossHealth > 0) {
            bossHealth -= Math.max(1, playerDamage - bossArmor);
            if (bossHealth <= 0) {
                return true;
            }
            playerHealth -= Math.max(1, bossDamage - playerArmor);
        }
        return false;
    }

    @Data
    private static class Item {
        private final int cost;
        private final int damage;
        private final int armor;

        public Item(int cost, int damage, int armor) {
            this.cost = cost;
            this.damage = damage;
            this.armor = armor;
        }
    }
}
