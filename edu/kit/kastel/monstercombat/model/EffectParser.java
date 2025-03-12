package edu.kit.kastel.monstercombat.model;

import edu.kit.kastel.monstercombat.model.effect.Effect;
import edu.kit.kastel.monstercombat.model.effect.EffectContinue;
import edu.kit.kastel.monstercombat.model.effect.EffectDamage;
import edu.kit.kastel.monstercombat.model.effect.EffectDefense;
import edu.kit.kastel.monstercombat.model.effect.EffectHealing;
import edu.kit.kastel.monstercombat.model.effect.EffectStatChange;
import edu.kit.kastel.monstercombat.model.effect.EffectStatusCondition;
import edu.kit.kastel.monstercombat.model.exception.ConfigurationException;

/**
 * Parser for effects in the configuration.
 * @author ursxd
 */
public class EffectParser {

    /**
     * Parses an effect from a configuration line.
     *
     * @param line the configuration line
     * @return the parsed effect
     * @throws ConfigurationException if the effect configuration is invalid
     */
    public Effect parseEffect(String line) throws ConfigurationException {
        String[] parts = line.split(" ");

        if (parts.length < 2) {
            throw new ConfigurationException("Invalid effect format: " + line);
        }

        String effectType = parts[0];

        return switch (effectType) {
            case "damage" -> parseDamageEffect(parts);
            case "inflictStatusCondition" -> parseStatusConditionEffect(parts);
            case "inflictStatChange" -> parseStatChangeEffect(parts);
            case "protectStat" -> parseDefenseEffect(parts);
            case "heal" -> parseHealingEffect(parts);
            case "continue" -> parseContinueEffect(parts);
            default -> throw new ConfigurationException("Unknown effect type: " + effectType);
        };
    }

    /**
     * Parses a damage effect.
     *
     * @param parts the split effect line
     * @return the parsed damage effect
     * @throws ConfigurationException if the damage effect configuration is invalid
     */
    public Effect parseDamageEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid damage effect: " + String.join(" ", parts));
        }

        Effect.TargetType targetType = parseTargetType(parts[1]);
        EffectDamage.DamageType damageType;
        int value;

        switch (parts[2]) {
            case "base" -> {
                damageType = EffectDamage.DamageType.BASE;
                value = Integer.parseInt(parts[3]);
            }
            case "rel" -> {
                damageType = EffectDamage.DamageType.RELATIVE;
                value = Integer.parseInt(parts[3]);
            }
            case "abs" -> {
                damageType = EffectDamage.DamageType.ABSOLUTE;
                value = Integer.parseInt(parts[3]);
            }
            default -> throw new ConfigurationException("Invalid damage type: " + parts[2]);
        }

        int hitRate = Integer.parseInt(parts[4]);

        return new EffectDamage(targetType, damageType, value, hitRate);
    }

    /**
     * Parses a status condition effect.
     *
     * @param parts the split effect line
     * @return the parsed status condition effect
     * @throws ConfigurationException if the status condition effect configuration is invalid
     */
    public Effect parseStatusConditionEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid status condition format: " + String.join(" ", parts));
        }

        Effect.TargetType targetType = parseTargetType(parts[1]);
        StatusCondition condition = StatusCondition.fromString(parts[2]);

        if (condition == StatusCondition.NONE) {
            throw new ConfigurationException("Invalid status condition: " + parts[2]);
        }

        int hitRate = Integer.parseInt(parts[3]);

        return new EffectStatusCondition(targetType, condition, hitRate);
    }

    /**
     * Parses a stat change effect.
     *
     * @param parts the split effect line
     * @return the parsed stat change effect
     * @throws ConfigurationException if the stat change effect configuration is invalid
     */
    public Effect parseStatChangeEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 5) {
            throw new ConfigurationException("Invalid stat change format: " + String.join(" ", parts));
        }

        Effect.TargetType targetType = parseTargetType(parts[1]);
        Stat stat = Stat.fromString(parts[2]);

        if (stat == null) {
            throw new ConfigurationException("Invalid stat: " + parts[2]);
        }

        int stages = Integer.parseInt(parts[3]);
        int hitRate = Integer.parseInt(parts[4]);

        return new EffectStatChange(targetType, stat, stages, hitRate);
    }

    /**
     * Parses a defense effect.
     *
     * @param parts the split effect line
     * @return the parsed defense effect
     * @throws ConfigurationException if the defense effect configuration is invalid
     */
    public Effect parseDefenseEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid defense format: " + String.join(" ", parts));
        }

        EffectDefense.ProtectionType target;

        if (parts[1].equals("health")) {
            target = EffectDefense.ProtectionType.HEALTH;
        } else if (parts[1].equals("stats")) {
            target = EffectDefense.ProtectionType.STATS;
        } else {
            throw new ConfigurationException("Invalid protection target: " + parts[1]);
        }

        int hitRate;

        if (parts[2].equals("random")) {
            if (parts.length < 6) {
                throw new ConfigurationException("Invalid random defense format: " + String.join(" ", parts));
            }

            int min = Integer.parseInt(parts[3]);
            int max = Integer.parseInt(parts[4]);
            hitRate = Integer.parseInt(parts[5]);

            return new EffectDefense(target, min, max, hitRate);
        } else {
            int count = Integer.parseInt(parts[2]);
            hitRate = Integer.parseInt(parts[3]);

            return new EffectDefense(target, count, hitRate);
        }
    }

    /**
     * Parses a healing effect.
     *
     * @param parts the split effect line
     * @return the parsed healing effect
     * @throws ConfigurationException if the healing effect configuration is invalid
     */
    public Effect parseHealingEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 4) {
            throw new ConfigurationException("Invalid healing format: " + String.join(" ", parts));
        }

        Effect.TargetType targetType = parseTargetType(parts[1]);
        EffectHealing.HealingType type;
        int value;

        switch (parts[2]) {
            case "base" -> {
                type = EffectHealing.HealingType.BASE;
                value = Integer.parseInt(parts[3]);
            }
            case "rel" -> {
                type = EffectHealing.HealingType.RELATIVE;
                value = Integer.parseInt(parts[3]);
            }
            case "abs" -> {
                type = EffectHealing.HealingType.ABSOLUTE;
                value = Integer.parseInt(parts[3]);
            }
            default -> throw new ConfigurationException("Invalid healing type: " + parts[2]);
        }

        int hitRate = Integer.parseInt(parts[4]);

        return new EffectHealing(targetType, type, value, hitRate);
    }

    /**
     * Parses a continue effect.
     *
     * @param parts the split effect line
     * @return the parsed continue effect
     * @throws ConfigurationException if the continue effect configuration is invalid
     */
    public Effect parseContinueEffect(String[] parts) throws ConfigurationException {
        if (parts.length < 2) {
            throw new ConfigurationException("Invalid continue format: " + String.join(" ", parts));
        }

        int hitRate = Integer.parseInt(parts[1]);

        return new EffectContinue(hitRate);
    }

    /**
     * Parses a target type.
     *
     * @param targetStr the target string
     * @return the target type
     * @throws IllegalArgumentException if the target type is invalid
     */
    private Effect.TargetType parseTargetType(String targetStr) {
        return switch (targetStr) {
            case "user" -> Effect.TargetType.USER;
            case "target" -> Effect.TargetType.TARGET;
            default -> throw new IllegalArgumentException("Invalid target type: " + targetStr);
        };
    }
}