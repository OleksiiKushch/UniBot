package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BuildStructure {

    /**
     * When is it useful that `amountAtOneTime = 1`:
     * - regular adjustment of the "Supply Depot" (one by one) to maintain the limit;
     * - build single structure like: "Fusion Core";
     * - if the build requires the sequential construction of buildings one by one to quickly reach a certain technology, for example: 1 - 1 - 1;
     * Noted: With random position.
     */
    default void tryBuildStructure(ObservationInterface observation, ActionInterface actions,
                                   Ability abilityTypeForStructure, boolean asSingle, Predicate<Unit> additionalFilterForFindingScv) {
        tryBuildStructure(observation, actions, abilityTypeForStructure, null, asSingle, additionalFilterForFindingScv);
    }

    default void tryBuildStructure(ObservationInterface observation, ActionInterface actions,
                                   Ability abilityTypeForStructure, Point2d targetPoint, boolean asSingle,
                                   Predicate<Unit> additionalFilterForFindingScv) {
        if (asSingle && isAlreadyBuildIt(observation, abilityTypeForStructure)) return;

        List<Unit> units = getNearestFreeScv(observation, targetPoint, 1, additionalFilterForFindingScv);
        if (!units.isEmpty()) {
            Unit unit = units.get(0);
            actions.unitCommand(
                    unit,
                    abilityTypeForStructure,
                    targetPoint != null ? targetPoint : getRandomPosition(unit),
                    false);
        }
    }

    default List<Unit> getNearestFreeScv(ObservationInterface observation, Point2d target, int amount,
                                         Predicate<Unit> additionalFilter) {
        List<Tag> gases = getListOfActiveTerranRefineries(observation);
        Stream<Unit> unitStream = observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SCV)).stream()
                .filter(UnitInPool.isCarryingMinerals().negate())
                .filter(UnitInPool.isCarryingVespene().negate())
                .map(UnitInPool::unit)
                .filter(unit -> {
                    boolean result = isScvGoingToMineMinerals(unit) && isScvNotGoingToMineGas(gases, unit) || isScvJustMove(unit);
                    if (additionalFilter != null) {
                        return result || additionalFilter.test(unit);
                    }
                    return result;
                });

        if (target != null) {
            unitStream = unitStream.sorted(Comparator.comparing(scv -> scv.getPosition().toPoint2d().distance(target)));
        }

        return unitStream.limit(amount).collect(Collectors.toList());
    }

    private boolean isScvJustMove(Unit unit) {
        return !unit.getOrders().isEmpty() && Abilities.MOVE.equals(unit.getOrders().get(0).getAbility());
    }

    private boolean isScvGoingToMineMinerals(Unit unit) {
        return !unit.getOrders().isEmpty() && Abilities.HARVEST_GATHER.equals(unit.getOrders().get(0).getAbility());
    }

    private boolean isScvNotGoingToMineGas(List<Tag> gases, Unit unit) {
        return !gases.contains(unit.getOrders().get(0).getTargetedUnitTag().orElse(null));
    }

    private Point2d getRandomPosition(Unit unit) {
        return unit.getPosition().toPoint2d().add(Point2d.of(getRandomScalar(), getRandomScalar()).mul(15.0f));
    }

    private List<Tag> getListOfActiveTerranRefineries(ObservationInterface observation) {
        return observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_REFINERY)).stream()
                .map(unitInPool1 -> unitInPool1.unit().getTag())
                .collect(Collectors.toList());
    }

    private boolean isAlreadyBuildIt(ObservationInterface observation, Ability abilityTypeForStructure) {
        return !observation.getUnits(Alliance.SELF, doesBuildWith(abilityTypeForStructure)).isEmpty();
    }

    private Predicate<UnitInPool> doesBuildWith(Ability abilityTypeForStructure) {
        return unitInPool -> unitInPool.unit()
                .getOrders()
                .stream()
                .anyMatch(unitOrder -> abilityTypeForStructure.equals(unitOrder.getAbility()));
    }

    private float getRandomScalar() {
        return ThreadLocalRandom.current().nextFloat() * 2 - 1;
    }
}
