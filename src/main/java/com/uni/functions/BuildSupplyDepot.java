package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.uni.functions.BuildStructure;
import com.uni.surveyor.GameMap;
import com.uni.utils.UniBotUtils;

public interface BuildSupplyDepot extends BuildStructure {

    default void tryToBuildSupply(ObservationInterface observation, ActionInterface actions, boolean asSingle) {
        if (!(observation.getFoodUsed() <= observation.getFoodCap() - calculateThreshold(observation)) &&
                observation.getFoodCap() != 200) {
            tryBuildStructure(observation, actions, Abilities.BUILD_SUPPLY_DEPOT, getPosition(observation), asSingle, null);
        }
    }

    // TODO: Improve this
    private Point2d getPosition(ObservationInterface observation) {
        if (UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT) +
                UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT_LOWERED) == 4) {
            return GameMap.main.techAndUpgraders().mainSupplyCoordinates().supply5();
        } else if (UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT) +
                UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT_LOWERED) == 5) {
            return GameMap.main.techAndUpgraders().mainSupplyCoordinates().supply6();
        } else if (UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT) +
                UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT_LOWERED) == 6) {
            return GameMap.main.techAndUpgraders().mainSupplyCoordinates().supply7();
        } else if (UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT) +
                UniBotUtils.countMyUnit(observation, Units.TERRAN_SUPPLY_DEPOT_LOWERED) == 7) {
            return GameMap.main.techAndUpgraders().mainSupplyCoordinates().supply8();
        } else {
            return null;
        }
    }

    // TODO: Optimize it!
    private int calculateThreshold(ObservationInterface observation) {
        if (UniBotUtils.countMyUnit(observation, Units.TERRAN_COMMAND_CENTER) < 2) {
            return 4;
        } else if (UniBotUtils.countMyUnit(observation, Units.TERRAN_COMMAND_CENTER) < 3) {
            return 6;
        } else {
            return 8;
        }
    }
}
