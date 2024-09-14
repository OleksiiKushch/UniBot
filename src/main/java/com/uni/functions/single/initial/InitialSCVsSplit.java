package com.uni.functions.single.initial;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.utils.UniBotConstants;
import com.uni.utils.UniBotUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class InitialSCVsSplit {

    private static final Map<Unit, Unit> initialLastSCVsTargets = new LinkedHashMap<>();

    public static void splitSCVs(ObservationInterface observation, ActionInterface actions) {
        Collection<Unit> targetUnits = InitialSpeedMining.patchesByTag.values();
        Map<Unit, Unit> result = new LinkedHashMap<>();
        targetUnits.stream()
                .filter(mineral -> UniBotConstants.ALL_SMALL_NEUTRAL_MINERAL_FIELD_TYPES.contains(mineral.getType()))
                .forEach(mineral ->  result.put(
                        UniBotUtils.findNearestUnits(observation, mineral.getPosition().toPoint2d(),
                                Set.of(Units.TERRAN_SCV), Alliance.SELF, 10.0d, 1, u -> !result.containsKey(u)).get(0), mineral));
        targetUnits.stream()
                .filter(mineral -> UniBotConstants.ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES.contains(mineral.getType()))
                .forEach(mineral -> result.put(
                        UniBotUtils.findNearestUnits(observation, mineral.getPosition().toPoint2d(),
                                Set.of(Units.TERRAN_SCV), Alliance.SELF, 10.0d, 1, u -> !result.containsKey(u)).get(0), mineral));

        // optimization
        boolean test = true;
        while (test) {
            int i = 0;
            for (Map.Entry<Unit, Unit> entry : result.entrySet()) {
                Unit actualNearestMineral = UniBotUtils.findNearestUnits(observation, entry.getKey().getPosition().toPoint2d(), UniBotConstants.ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES, Alliance.NEUTRAL, 10.0d, 1, u -> true).get(0);
                if (actualNearestMineral.getTag() != entry.getValue().getTag()) {
                    Map.Entry<Unit, Unit> targetEntry = getEntryByMineralTag(result, actualNearestMineral.getTag());
                    double currentDistance = entry.getKey().getPosition().toPoint2d().distance(entry.getValue().getPosition().toPoint2d()) +
                            targetEntry.getKey().getPosition().toPoint2d().distance(targetEntry.getValue().getPosition().toPoint2d());
                    double afterSwapDistance = entry.getKey().getPosition().toPoint2d().distance(actualNearestMineral.getPosition().toPoint2d()) +
                            targetEntry.getKey().getPosition().toPoint2d().distance(entry.getValue().getPosition().toPoint2d());
                    if (currentDistance > afterSwapDistance) {
                        Unit previousTarget = entry.getValue();
                        result.put(entry.getKey(), actualNearestMineral);
                        result.put(targetEntry.getKey(), previousTarget);
                        break;
                    }
                }
                i++;
                if (i == result.size()) {
                    test = false;
                }
            }
        }

        result.forEach((key, value) -> {
            actions.unitCommand(key, Abilities.SMART, value, false);
        });

        targetUnits.stream()
                .filter(mineral -> UniBotConstants.ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES.contains(mineral.getType()))
                .forEach(mineral -> initialLastSCVsTargets.put(
                        UniBotUtils.findNearestUnits(observation, mineral.getPosition().toPoint2d(),
                                Set.of(Units.TERRAN_SCV), Alliance.SELF, 10.0d, 1, u -> !result.containsKey(u) && !initialLastSCVsTargets.containsKey(u)).get(0), mineral));
    }

    private static Map.Entry<Unit, Unit> getEntryByMineralTag(Map<Unit, Unit> result, Tag mineralTag) {
        return result.entrySet().stream()
                .filter(entry -> entry.getValue().getTag().equals(mineralTag))
                .findFirst()
                .orElse(null);
    }

    // TODO: add implementation via holding
    public static void keepLastSCVs(ObservationInterface observation, ActionInterface actions) {
        if (20 < observation.getGameLoop() && observation.getGameLoop() < 50) {
            initialLastSCVsTargets.forEach((key, value) -> actions.unitCommand(key, Abilities.MOVE, InitialSpeedMining.speedMiningPoints.get(value.getTag()), false));
        }
        if (observation.getGameLoop() == 51) {
            initialLastSCVsTargets.forEach((key, value) -> actions.unitCommand(key, Abilities.HARVEST_GATHER, value, true));
        }
    }
}
