package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SolutionDay(year = 2015, day = 22)
@Slf4j
public class Day22 extends AdventOfCodeSolution {
    private long lowestSoFar = Long.MAX_VALUE;

    public Day22() {
        super(22, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
        register("debug", this::debug);
    }


    public String runSolutionA(String input) {
        var player = Character.builder().armor(0).damage(0).health(50).mana(500).build();
        var boss = Character.builder().armor(0).damage(8).health(55).mana(0).build();

        return "%,d".formatted(simulateGame(player, boss, true, List.of(), 0, 0, List.of()));
    }

    public String runSolutionB(String input) {
        var player = Character.builder().armor(0).damage(0).health(50).mana(500).tick(1).build();
        var boss = Character.builder().armor(0).damage(8).health(55).mana(0).build();

        return "%,d".formatted(simulateGame(player, boss, true, List.of(), 0, 0, List.of()));
    }

    public String debug(String input) {
        var player = Character.builder().armor(0).damage(0).health(10).mana(250).tick(0).build();
        var boss = Character.builder().armor(0).damage(8).health(13).mana(0).build();
        simulateDebugGame(player, boss, List.of(Poison.class, MagicMissile.class));
        return "";
    }

    private long simulateGame(Character player, Character boss, boolean turn, List<Spell> activeSpells, long totalSpent, int depth, List<Class<? extends Spell>> cast) {
        if (totalSpent > lowestSoFar) {
            return totalSpent;
        }

        player.tick();
        // check for lose conditions
        if (player.health <= 0 || player.mana <= 0) {
            return Integer.MAX_VALUE;
        }

        // start of the round, active spells apply
        var spells = activeSpells.stream().map(Spell::clone).filter(Spell::isActive).toList();
        spells.forEach(spell -> spell.tick(player, boss));
        spells = new ArrayList<>(spells.stream().filter(Spell::isActive).toList());

        // check for lose conditions
        if (player.health <= 0 || player.mana <= 0) {
            return Integer.MAX_VALUE;
        }

        // check for win conditions
        if (boss.health <= 0) {
            if (totalSpent < lowestSoFar) lowestSoFar = totalSpent;
            log.info("WIN\t{}\t{}", totalSpent, cast);
            return totalSpent;
        }

        // play player's turn
        if (turn) {
            List<Spell> finalSpells = spells;
            var spellsToCast = Stream.of(new Poison(), new MagicMissile(), new Drain(), new Shield(), new Recharge())
                    .filter(newSpell -> finalSpells.stream().noneMatch(spell -> spell.cost == newSpell.cost))  // check that the spell is not already active
                    .filter(spell -> player.mana >= spell.cost)  // check that the player can afford to cast the spell
                    .toList();
            if (spellsToCast.isEmpty()) return Integer.MAX_VALUE;  // YOU LOSE
            return spellsToCast.stream().mapToLong(spell -> {
                // cast the spell
                var playerCopy = new Character(player);
                var bossCopy = new Character(boss);

                playerCopy.mana -= spell.cost;
                spell.cast(playerCopy, bossCopy);

                // save the spell
                var spellsCopy = new ArrayList<>(finalSpells);
                spellsCopy.add(spell);

                // save debug info
                var castCopy = new ArrayList<>(cast);
                castCopy.add(spell.getClass());

                // run the game
                return simulateGame(playerCopy, bossCopy, false, spellsCopy, totalSpent + spell.cost, depth + 1, castCopy);
            }).min().getAsLong();
        } else {
            var playerCopy = new Character(player);
            playerCopy.health -= Math.max(boss.damage - playerCopy.armor, 1);
            return simulateGame(playerCopy, new Character(boss), true, spells, totalSpent, depth + 1, cast);
        }
    }

    @SneakyThrows
    private void simulateDebugGame(Character player, Character boss, List<Class<? extends Spell>> castList) {
        var activeSpells = new ArrayList<Spell>();
        long spent = 0;

        for (var spellClass : castList) {
            log.debug("");
            // player turn
            log.debug("-- Player turn HP:{} M:{} A:{} --", player.health, player.mana, player.armor);
            log.debug("- Boss HP:{} -", boss.health);
            player.tick();

            // run through active spells
            activeSpells.forEach(spell -> {
                log.debug("{} ticks, timer is now {}", spell.name, spell.timer);
                spell.tick(player, boss);
            });
            activeSpells = new ArrayList<>(activeSpells.stream().filter(Spell::isActive).toList());

            // check for lose conditions
            if (player.health <= 0 || player.mana <= 0) {
                log.info("Player loses");
                return;
            }

            // check for win conditions
            if (boss.health <= 0) {
                log.info("Player wins");
                log.info(String.valueOf(spent));
                return;
            }

            // cast the spell
            var newSpell = spellClass.getConstructor().newInstance();
            newSpell.cast(player, boss);
            log.info("Player casts {}", newSpell.name);
            player.mana -= newSpell.cost;
            activeSpells.add(newSpell);
            spent += newSpell.cost;

            log.debug("");
            // boss turn
            log.debug("-- Boss turn HP:{} --", boss.health);
            log.debug("- Player HP:{} M:{} A:{} -", player.health, player.mana, player.armor);

            // run through active spells
            activeSpells.forEach(spell -> {
                log.debug("{} ticks, timer is now {}", spell.name, spell.timer);
                spell.tick(player, boss);
            });
            activeSpells = new ArrayList<>(activeSpells.stream().filter(Spell::isActive).toList());

            // check for lose conditions
            if (player.health <= 0 || player.mana <= 0) {
                log.info("Player loses");
                return;
            }

            // check for win conditions
            if (boss.health <= 0) {
                log.info("Player wins");
                log.info(String.valueOf(spent));
                return;
            }

            // player damage
            player.health -= Math.max(boss.damage - player.armor, 1);
            log.info("Boss attacks for {} damage", Math.max(boss.damage - player.armor, 1));
        }

        log.debug("");
        // player turn
        log.debug("-- Player turn HP:{} M:{} A:{} --", player.health, player.mana, player.armor);
        log.debug("- Boss HP:{} -", boss.health);
        player.tick();

        // run through active spells
        activeSpells.forEach(spell -> {
            log.debug("{} ticks, timer is now {}", spell.name, spell.timer);
            spell.tick(player, boss);
        });
        activeSpells = new ArrayList<>(activeSpells.stream().filter(Spell::isActive).toList());

        // check for lose conditions
        if (player.health <= 0 || player.mana <= 0) {
            log.info("Player loses");
            return;
        }

        // check for win conditions
        if (boss.health <= 0) {
            log.info("Player wins");
            log.info(String.valueOf(spent));
            return;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Character {
        private int health;
        private int damage;
        private int armor;
        private long mana;
        private int tick;

        public Character(Character c) {
            this.health = c.health;
            this.damage = c.damage;
            this.armor = c.armor;
            this.mana = c.mana;
            this.tick = c.tick;
        }

        public void tick() {
            this.health -= this.tick;
        }
    }

    @Data
    @RequiredArgsConstructor
    @EqualsAndHashCode
    private abstract static class Spell {
        public final String name;
        private final int cost;
        private final int duration;

        protected int timer = 0;

        public abstract void cast(Character player, Character boss);
        public abstract void tick(Character player, Character boss);

        public boolean isActive() {
            return timer > 0;
        }

        public abstract Spell clone();
    }

    private static class MagicMissile extends Spell {
        public MagicMissile() {
            super("MagicMissile", 53, 0);
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

        @Override
        public Spell clone() {
            var spell = new MagicMissile();
            spell.timer = this.timer;
            return spell;
        }
    }

    private static class Drain extends Spell {
        public Drain() {
            super("Drain", 73, 0);
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

        @Override
        public Spell clone() {
            var spell = new Drain();
            spell.timer = this.timer;
            return spell;
        }
    }

    private static class Shield extends Spell {
        public Shield() {
            super("Shield", 113, 6);
        }

        @Override
        public void cast(Character player, Character boss) {
            player.setArmor(player.getArmor() + 7);
            this.timer = this.getDuration();
        }

        @Override
        public void tick(Character player, Character boss) {
            this.timer--;
            if (timer == 0) {
                player.setArmor(player.getArmor() - 7);
            }
        }

        @Override
        public Spell clone() {
            var spell = new Shield();
            spell.timer = this.timer;
            return spell;
        }
    }

    private static class Poison extends Spell {
        public Poison() {
            super("Poison", 173, 6);
        }

        @Override
        public void cast(Character player, Character boss) {
            this.timer = this.getDuration();
        }

        @Override
        public void tick(Character player, Character boss) {
            this.timer--;
            boss.setHealth(boss.getHealth() - 3);
        }

        @Override
        public Spell clone() {
            var spell = new Poison();
            spell.timer = this.timer;
            return spell;
        }
    }

    private static class Recharge extends Spell {
        public Recharge() {
            super("Recharge", 229, 5);
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

        @Override
        public Spell clone() {
            var spell = new Recharge();
            spell.timer = this.timer;
            return spell;
        }
    }
}
