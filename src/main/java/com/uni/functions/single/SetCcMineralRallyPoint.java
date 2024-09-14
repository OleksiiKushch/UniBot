package com.uni.functions.single;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.functions.MineralLineOptimizer;

import java.util.ArrayList;
import java.util.List;

public class SetCcMineralRallyPoint {

    public static void set(ObservationInterface observation, ActionInterface actions) {
        List<UnitInPool> ccs = new ArrayList<>();
        ccs.addAll(observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_COMMAND_CENTER)));
        ccs.addAll(observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_ORBITAL_COMMAND)));
        ccs.addAll(observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_PLANETARY_FORTRESS)));

        ccs.stream()
                .map(UnitInPool::unit)
                .filter(cc -> cc.getOrders().stream()
                        .anyMatch(order -> order.getAbility() == Abilities.TRAIN_SCV))
                .filter(cc -> getTrainScvProgress(cc) >= 0.69f)
                .forEach(cc -> {
                    Unit mineral = MineralLineOptimizer.findOptionalMineral(observation, getTrainScvProgress(cc));
                    if (mineral != null) { // result found and ready to be used and processed
                        actions.unitCommand(cc, Abilities.RALLY_COMMAND_CENTER, mineral, false);
                    }
                });
    }

    private static float getTrainScvProgress(Unit cc) {
        return cc.getOrders().stream()
                .filter(order -> order.getAbility() == Abilities.TRAIN_SCV)
                .findAny()
                .map(unitOrder -> unitOrder.getProgress()
                        .orElse(0.0f))
                .orElse(0.0f);
    }
}
