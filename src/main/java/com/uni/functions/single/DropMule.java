package com.uni.functions.single;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.uni.functions.MineralLineOptimizer;

public class DropMule {

    private static final int MULE_ENERGY = 50;

    public static void drop(ObservationInterface observation, ActionInterface actions) {
        observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_ORBITAL_COMMAND)).stream()
                .map(UnitInPool::unit)
                .filter(orbital -> orbital.getEnergy().orElse(0.0f) >= MULE_ENERGY)
                .forEach(orbital -> MineralLineOptimizer.findNearestMineralPatch(observation, orbital.getPosition().toPoint2d(), 1)
                        .ifPresent(mineralCoordinate -> actions.unitCommand(orbital, Abilities.EFFECT_CALL_DOWN_MULE, mineralCoordinate, false)));
    }
}
