package com.uni.utils;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class UniBotUtils {

    public static int countMyUnit(ObservationInterface observation, Units unitType) {
        return observation.getUnits(Alliance.SELF, UnitInPool.isUnit(unitType)).size();
    }

    public static Optional<Unit> getMyUnit(ObservationInterface observation, Units unitType) {
        return observation.getUnits(Alliance.SELF, UnitInPool.isUnit(unitType)).stream()
                .map(UnitInPool::unit)
                .findFirst();
    }

    public static Optional<Unit> getUnitByTag(ObservationInterface observation, Tag tag) {
        return observation.getUnits(Alliance.NEUTRAL).stream()
                .map(UnitInPool::unit)
                .filter(unit -> unit.getTag().equals(tag))
                .findFirst();
    }

    public static boolean isNotActive(Unit unit) {
        return !unit.getActive().orElse(true); // `true` because it's negative condition
    }

    public static void startAbility(ObservationInterface observation, ActionInterface actions, Units unitType, Abilities abilityToUpgrade) {
        Optional<Unit> unit = getMyUnit(observation, unitType);
        unit.ifPresent(u -> actions.unitCommand(u, abilityToUpgrade, false));
    }

    public static boolean isStartAbility(ObservationInterface observation, Units unitType, Abilities abilityToUpgrade) {
        Optional<Unit> unit = getMyUnit(observation, unitType);
        return unit.map(u -> u.getOrders().stream()
                .anyMatch(order -> order.getAbility() == abilityToUpgrade)).orElse(false);
    }

    public static List<Unit> findNearestUnits(ObservationInterface observation, Point2d target, Set<UnitType> unitTypes, Alliance unitAlliance,
                                              double radius, int limit, Predicate<Unit> additionalFilter) {
        return observation.getUnits(unitAlliance).stream()
                .map(UnitInPool::unit)
                .filter(u -> unitTypes.contains(u.getType()))
                .filter(u -> u.getPosition().toPoint2d().distance(target) <= radius)
                .filter(additionalFilter)
                .sorted(Comparator.comparing(unit -> unit.getPosition().toPoint2d().distance(target)))
                .limit(limit)
                .toList();
    }

    public static Optional<Unit> findNearestBase(ObservationInterface observation, Point2d target) {
        return Optional.ofNullable(
                findNearestUnits(observation, target,
                        Set.of(Units.TERRAN_COMMAND_CENTER,
                                Units.TERRAN_ORBITAL_COMMAND,
                                Units.TERRAN_PLANETARY_FORTRESS),
                        Alliance.SELF, 10.0d, 1, u -> true).get(0));
    }

    public static boolean enemyUnitInVision(ObservationInterface observation, Unit myUnit, float range) {
        return observation.getUnits(Alliance.ENEMY).stream()
                .anyMatch(u -> u.unit().getPosition().distance(myUnit.getPosition()) <= range);
    }
}
