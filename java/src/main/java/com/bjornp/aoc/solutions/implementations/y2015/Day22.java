package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

@SolutionDay(year = 2015, day = 22)
@Slf4j
public class Day22 extends AdventOfCodeSolution {
    private final Map<String, Spell> spells = Map.of(
            "Magic Missile", new MagicMissile(),
            "Drain", new Drain(),
            "Shield", new Shield(),
            "Poison", new Poison(),
            "Recharge", new Recharge()
    );

    public Day22() {
        super(22, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {


        return "%,d".formatted(0);
    }

    public String runSolutionB(String input) {


        return "%,d".formatted(0);
    }

    public Pair<Long, Boolean> simulateGame(Character player, Character boss, boolean turn) {
        if (player.getHealth() < 0 || boss.getHealth() < 0) {
            return Pair.of(0L, player.getHealth() > 0);
        }

        return Pair.of(null, null); // todo solve puzzle
    }

    @Data
    private static class Character {
        private int health;
        private int damage;
        private int armor;
        private int mana;
    }

    @Data
    @RequiredArgsConstructor
    private abstract static class Spell {
        private final int cost;
        private final int duration;

        protected int timer = 0;

        public abstract void cast(Character player, Character boss);
        public abstract void tick(Character player, Character boss);

        public boolean isActive() {
            return timer > 0;
        }
    }

    private static class MagicMissile extends Spell {
        public MagicMissile() {
            super(53, 0);
        }

        @Override
        public void cast(Character player, Character boss) {
            this.timer = this.getDuration();
            boss.setHealth(boss.getHealth() - 4);
        }

        @Override
        public void tick(Character player, Character boss) {
            this.timer--;
        }
    }

    private static class Drain extends Spell {
        public Drain() {
            super(73, 0);
        }

        @Override
        public void cast(Character player, Character boss) {
            this.timer = this.getDuration();
            boss.setHealth(boss.getHealth() - 2);
            player.setHealth(player.getHealth() + 2);
        }

        @Override
        public void tick(Character player, Character boss) {
            this.timer--;
        }
    }

    private static class Shield extends Spell {
        public Shield() {
            super(113, 6);
        }

        @Override
        public void cast(Character player, Character boss) {
            this.timer = this.getDuration();
            player.setArmor(player.getArmor() + 7);
        }

        @Override
        public void tick(Character player, Character boss) {
            this.timer--;
            if (timer == 0) {
                player.setArmor(player.getArmor() - 7);
            }
        }
    }

    private static class Poison extends Spell {
        public Poison() {
            super(173, 6);
        }

        @Override
        public void cast(Character player, Character boss) {
            this.timer = this.getDuration();
            boss.setHealth(boss.getHealth() - 3);
        }

        @Override
        public void tick(Character player, Character boss) {
            this.timer--;
            boss.setHealth(boss.getHealth() - 3);
        }
    }

    private static class Recharge extends Spell {
        public Recharge() {
            super(229, 5);
        }

        @Override
        public void cast(Character player, Character boss) {
            this.timer = this.getDuration();
        }

        @Override
        public void tick(Character player, Character boss) {
            this.timer--;
            player.setMana(player.getMana() + 101);
        }
    }
}
