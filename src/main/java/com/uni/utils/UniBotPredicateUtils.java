package com.uni.utils;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;

import java.util.function.Predicate;

public class UniBotPredicateUtils {

    public static Predicate<UnitInPool> isHarvestReturnScv() {
        return (unitInPool) -> UnitInPool.isUnit(Units.TERRAN_SCV).test(unitInPool) &&
                Float.compare(unitInPool.unit().getBuildProgress(), 1.0f) == 0 &&
                UnitInPool.isCarryingVespene().negate().test(unitInPool) &&
                unitInPool.unit().getOrders().stream()
                        .anyMatch((unitOrder) -> Abilities.HARVEST_RETURN.equals(unitOrder.getAbility()));
    }

    public static Predicate<UnitInPool> isHarvestGatherScv() {
        return (unitInPool) -> UnitInPool.isUnit(Units.TERRAN_SCV).test(unitInPool) &&
                Float.compare(unitInPool.unit().getBuildProgress(), 1.0f) == 0 &&
                unitInPool.unit().getOrders().stream()
                        .anyMatch((unitOrder) -> Abilities.HARVEST_GATHER.equals(unitOrder.getAbility()));
    }
}
