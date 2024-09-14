package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;
import com.uni.surveyor.GameMap;
import com.uni.utils.UniBotConstants;
import com.uni.utils.UniBotUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MineralLineOptimizer {

    private static final Map<Tag, List<UnitInPool>> mineralLines = new HashMap<>();  // <base, <mineral, number_of_SCVs>>
    public static List<UnitInPool> unavailableSCVs = new ArrayList<>();
    public static boolean tempFlag = true;

    public static Unit findOptionalMineral(ObservationInterface observation, float progress) {
        if (progress < 0.97f) {
            tempFlag = true;
        }
        if (tempFlag) {
            if (progress < 0.7f) {
                return null;
            } else if (progress >= 0.7f && progress < 0.98f) {    // data collection
                Map<Tag, List<UnitInPool>> tempResult = observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SCV)).stream()
                        .filter(unit -> unit.unit().getOrders() != null && !unit.unit().getOrders().isEmpty())
                        .filter(unit -> unit.unit().getOrders().stream().anyMatch(order -> Abilities.HARVEST_GATHER.equals(order.getAbility())))
                        .collect(Collectors.groupingBy(unit -> unit.unit().getOrders().stream()
                                        .filter(order -> Abilities.HARVEST_GATHER.equals(order.getAbility()))
                                        .findFirst()
                                        .flatMap(UnitOrder::getTargetedUnitTag)
                                        .orElseThrow(IllegalStateException::new),
                                Collectors.toList()));
                tempResult.forEach((tag, units) -> {
                    // Merge lists of units by the common tag
                    mineralLines.merge(tag, units, (list1, list2) -> {
                        List<UnitInPool> mergedList = new ArrayList<>(list1);
                        for (UnitInPool unit : list2) {
                            if (!containsUnitByTag(mergedList, unit.getTag())) {
                                mergedList.add(unit);
                            }
                        }
                        return mergedList;
                    });
                });

                return null;
            } else {
                Unit result = mineralLines.entrySet().stream()
                        .filter(entry -> entry.getValue().size() < 2)
                        .map(entry -> UniBotUtils.getUnitByTag(observation, entry.getKey()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .min(Comparator.comparing((Unit u) -> UniBotConstants.ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES.contains(u.getType()) ? 0 : 1)
                                .thenComparing(u -> u.getMineralContents().orElse(0))
                                .thenComparing(u -> u.getPosition().toPoint2d().distance(observation.getStartLocation().toPoint2d())))
                        .orElse(null);
                unavailableSCVs = mineralLines.entrySet().stream()
                        .flatMap(entry -> UniBotUtils.getUnitByTag(observation, entry.getKey())
                                .filter(unit -> UniBotConstants.ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES.contains(unit.getType())).stream().flatMap(unit -> entry.getValue().stream()))
                        .toList();
                mineralLines.clear();
                tempFlag = false;
                return result;
            }
        }
        return null;
    }

    private static boolean containsUnitByTag(List<UnitInPool> units, Tag tag) {
        return units.stream()
                .anyMatch(unit -> unit.getTag().equals(tag));
    }

    // TODO: add optimization
    private static final double RADIUS_FOR_SEARCH_ON_WHOLE_MAP = 1000.0;
    public static Optional<Unit> findNearestMineralPatch(ObservationInterface observation, Point2d target, int limit) {
        return UniBotUtils.findNearestUnits(observation, target, UniBotConstants.ALL_NEUTRAL_MINERAL_FIELD_TYPES, Alliance.NEUTRAL, RADIUS_FOR_SEARCH_ON_WHOLE_MAP, limit, MineralLineOptimizer::isMineralCloseEnoughActiveBase).stream()
                .findFirst();
    }

    // TODO: add condition for finding the largest mineral
    private static final float IS_CLOSE_ENOUGH_MINERAL = 8.0f;
    private static boolean isMineralCloseEnoughActiveBase(Unit mineral) {
        return GameMap.basesCoordinates.get(0).distance(mineral.getPosition().toPoint2d()) < IS_CLOSE_ENOUGH_MINERAL;
    }
}
