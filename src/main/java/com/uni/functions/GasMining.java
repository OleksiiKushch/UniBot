package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.utils.UniBotUtils;

import java.util.List;
import java.util.Set;

public interface GasMining extends BuildStructure {

    // TODO: Sometimes, you get SCVs with minerals anyway!
    default void tryBuildGasRefinery(ObservationInterface observation, ActionInterface actions) {
        final int NEED_ONLY_ONE_SCV = 1;
        final double RADIUS = 10.0;

        observation.getUnits(Alliance.SELF).stream()
                .map(UnitInPool::unit)
                .filter(unit -> Units.TERRAN_COMMAND_CENTER.equals(unit.getType())
                                || Units.TERRAN_ORBITAL_COMMAND.equals(unit.getType())
                                || Units.TERRAN_PLANETARY_FORTRESS.equals(unit.getType()))
                .limit(1)
                .map(cc -> UniBotUtils.findNearestUnits(observation, cc.getPosition().toPoint2d(), Set.of(Units.NEUTRAL_VESPENE_GEYSER), Alliance.NEUTRAL, RADIUS, 2, u -> true))
                .flatMap(List::stream)
                .filter(gas -> isFree(observation, gas.getPosition().toPoint2d()))
                .limit(1)
                .forEach(gas -> getNearestFreeScv(observation, gas.getPosition().toPoint2d(), NEED_ONLY_ONE_SCV, null).stream()
                                .limit(NEED_ONLY_ONE_SCV)
                                .forEach(scv -> actions.unitCommand(scv, Abilities.BUILD_REFINERY, gas, false)
                ));
    }

    default void fillGasWithWorkers(ObservationInterface observation, ActionInterface actions, Unit gas) {
        getNearestFreeScv(observation, gas.getPosition().toPoint2d(), 2, null).forEach(worker ->
                actions.unitCommand(worker, Abilities.SMART, gas, false));
    }

    private boolean isFree(ObservationInterface observation, Point2d point) {
        return observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_REFINERY)).stream()
                .noneMatch(u -> u.unit().getPosition().toPoint2d().equals(point));
    }
}
