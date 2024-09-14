package com.uni.utils;

import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;

import java.util.Set;

public class UniBotConstants {

    public static final Set<UnitType> ALL_NEUTRAL_MINERAL_FIELD_TYPES = Set.of(
            Units.NEUTRAL_MINERAL_FIELD,
            Units.NEUTRAL_MINERAL_FIELD450,
            Units.NEUTRAL_MINERAL_FIELD750,
            Units.NEUTRAL_RICH_MINERAL_FIELD,
            Units.NEUTRAL_RICH_MINERAL_FIELD750,
            Units.NEUTRAL_MINERAL_FIELD_OPAQUE,
            Units.NEUTRAL_MINERAL_FIELD_OPAQUE900,
            Units.NEUTRAL_LAB_MINERAL_FIELD,
            Units.NEUTRAL_LAB_MINERAL_FIELD750,
            Units.NEUTRAL_BATTLE_STATION_MINERAL_FIELD,
            Units.NEUTRAL_BATTLE_STATION_MINERAL_FIELD750,
            Units.NEUTRAL_PURIFIER_MINERAL_FIELD,
            Units.NEUTRAL_PURIFIER_MINERAL_FIELD750,
            Units.NEUTRAL_PURIFIER_RICH_MINERAL_FIELD,
            Units.NEUTRAL_PURIFIER_RICH_MINERAL_FIELD750);

    public static final Set<UnitType> ALL_SMALL_NEUTRAL_MINERAL_FIELD_TYPES = Set.of(
            Units.NEUTRAL_MINERAL_FIELD450,
            Units.NEUTRAL_MINERAL_FIELD750,
            Units.NEUTRAL_RICH_MINERAL_FIELD750,
            Units.NEUTRAL_MINERAL_FIELD_OPAQUE900,
            Units.NEUTRAL_LAB_MINERAL_FIELD750,
            Units.NEUTRAL_BATTLE_STATION_MINERAL_FIELD750,
            Units.NEUTRAL_PURIFIER_MINERAL_FIELD750,
            Units.NEUTRAL_PURIFIER_RICH_MINERAL_FIELD750);

    public static final Set<UnitType> ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES = Set.of(
            Units.NEUTRAL_MINERAL_FIELD,
            Units.NEUTRAL_RICH_MINERAL_FIELD,
            Units.NEUTRAL_MINERAL_FIELD_OPAQUE,
            Units.NEUTRAL_LAB_MINERAL_FIELD,
            Units.NEUTRAL_BATTLE_STATION_MINERAL_FIELD,
            Units.NEUTRAL_PURIFIER_MINERAL_FIELD,
            Units.NEUTRAL_PURIFIER_RICH_MINERAL_FIELD);

    public static final Set<UnitType> ALL_NEUTRAL_GEYSER_TYPES = Set.of(
            Units.NEUTRAL_MINERAL_FIELD,
            Units.NEUTRAL_RICH_MINERAL_FIELD,
            Units.NEUTRAL_MINERAL_FIELD_OPAQUE,
            Units.NEUTRAL_PROTOSS_VESPENE_GEYSER,
            Units.NEUTRAL_PURIFIER_VESPENE_GEYSER,
            Units.NEUTRAL_SHAKURAS_VESPENE_GEYSER);
}
