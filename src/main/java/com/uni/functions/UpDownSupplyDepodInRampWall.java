package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.uni.surveyor.GameMap;
import com.uni.utils.UniBotUtils;

public class UpDownSupplyDepodInRampWall {

    public static void upDown(ObservationInterface observation, ActionInterface actions) {
        observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SUPPLY_DEPOT_LOWERED)).stream()
                .map(UnitInPool::unit)
                .filter(unit -> GameMap.main.rampWall().firstSupplyPosition().equals(unit.getPosition().toPoint2d()))
                .forEach(supply -> {
                    if (UniBotUtils.enemyUnitInVision(observation, supply, 7)) {
                        actions.unitCommand(supply, Abilities.MORPH_SUPPLY_DEPOT_RAISE, false);
                    }
                });
        observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SUPPLY_DEPOT)).stream()
                .map(UnitInPool::unit)
                .filter(unit -> GameMap.main.rampWall().firstSupplyPosition().equals(unit.getPosition().toPoint2d()))
                .forEach(supply -> {
                    if (!UniBotUtils.enemyUnitInVision(observation, supply, 7)) {
                        actions.unitCommand(supply, Abilities.MORPH_SUPPLY_DEPOT_LOWER, false);
                    }
                });
    }
}
