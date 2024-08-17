package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.surveyor.GameMap;
import com.uni.utils.UniBotUtils;

import java.util.Optional;


public interface MineralMining {

    int MULE_ENERGY = 50;
    float IS_CLOSE_ENOUGH_MINERAL = 8.0f;

    default void backToMineralMining(ObservationInterface observation, ActionInterface actions, Unit unit) {
        findNearestMineralPatch(observation, unit.getPosition().toPoint2d(), 1)
                .ifPresent(mineralPath -> actions.unitCommand(unit, Abilities.SMART, mineralPath, false));
    }

    default void dropMule(ObservationInterface observation, ActionInterface actions) {
        observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_ORBITAL_COMMAND)).stream()
                .map(UnitInPool::unit)
                .filter(orbital -> orbital.getEnergy().orElse(0.0f) >= MULE_ENERGY)
                .forEach(orbital -> findNearestMineralPatch(observation, orbital.getPosition().toPoint2d(), 1)
                        .ifPresent(mineralCoordinate -> actions.unitCommand(orbital, Abilities.EFFECT_CALL_DOWN_MULE, mineralCoordinate, false)));
    }

    default Optional<Unit> findNearestMineralPatch(ObservationInterface observation, Point2d target, int limit) {
        return UniBotUtils.findNearestUnits(observation, target, Units.NEUTRAL_MINERAL_FIELD, Alliance.NEUTRAL, limit, this::isMineralCloseEnoughActiveBase).stream()
                .findFirst();
    }

    // TODO: add condition for finding the largest mineral

    default void splitScvsBetweenMineralsOnStartGame(ObservationInterface observation, ActionInterface actions) {
        observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SCV)).stream()
                .map(UnitInPool::unit)
                .forEach(scv -> findNearestMineralPatch(observation, scv.getPosition().toPoint2d(), 1).ifPresent(
                            mineral -> actions.unitCommand(scv, Abilities.SMART, mineral, false))
                );
    }

    private boolean isMineralCloseEnoughActiveBase(Unit mineral) {
        return GameMap.basesCoordinates.get(0).distance(mineral.getPosition().toPoint2d()) < IS_CLOSE_ENOUGH_MINERAL;
    }
}
