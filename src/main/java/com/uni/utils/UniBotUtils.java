package com.uni.utils;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UniBotUtils {

    public static int countMyUnit(ObservationInterface observation, Units unitType) {
        return observation.getUnits(Alliance.SELF, UnitInPool.isUnit(unitType)).size();
    }

    public static Optional<Unit> getMyUnit(ObservationInterface observation, Units unitType) {
        return observation.getUnits(Alliance.SELF, UnitInPool.isUnit(unitType)).stream()
                .map(UnitInPool::unit)
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
                                              int limit, Predicate<Unit> additionalFilter) {
        return observation.getUnits(unitAlliance).stream()
                .map(UnitInPool::unit)
                .filter(u -> unitTypes.contains(u.getType()))
                .filter(additionalFilter)
                .sorted(Comparator.comparing(unit -> unit.getPosition().toPoint2d().distance(target)))
                .limit(limit)
                .toList();
    }

    public static Optional<Unit> findNearestBase(ObservationInterface observation, Point2d target) {
        return Optional.ofNullable(
                UniBotUtils.findNearestUnits(observation, target,
                        Set.of(Units.TERRAN_COMMAND_CENTER,
                                Units.TERRAN_ORBITAL_COMMAND,
                                Units.TERRAN_PLANETARY_FORTRESS),
                        Alliance.SELF, 1, u -> true).get(0));
    }

    public static boolean enemyUnitInVision(ObservationInterface observation, Unit myUnit, float range) {
        return observation.getUnits(Alliance.ENEMY).stream()
                .anyMatch(u -> u.unit().getPosition().distance(myUnit.getPosition()) <= range);
    }
}
