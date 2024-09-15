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

    private static final Map<Tag, List<UnitInPool>> mineralLines = new HashMap<>();  // <base, <mineral, number_of_SCVs>> // for form result
    private static LinkedHashMap<Unit, List<UnitInPool>> currentData;  // for consuming result
    private static Stack<Unit> bigMinerals;
    public static List<Unit> unavailableSCVs = new ArrayList<>();
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
            } else { // form result
                currentData = mineralLines.entrySet().stream()
                        .filter(entry -> entry.getValue().size() < 2)
                        .map(entry -> UniBotUtils.getUnitByTag(observation, entry.getKey())
                                .map(mineral -> Map.entry(mineral, entry.getValue())))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .sorted(Comparator.comparing((Map.Entry<Unit, List<UnitInPool>> entry) -> entry.getValue().size())
                                // TODO: may be improve between contests and distance (for example prioritize distance
                                //  but if difference between contests too big then prioritize contests). Same for mule dropping (bigMinerals)
                                .thenComparing(entry -> - entry.getKey().getMineralContents().orElse(0))
                                .thenComparing(entry -> entry.getKey().getPosition().toPoint2d().distance(observation.getStartLocation().toPoint2d())))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new ArrayList<>(entry.getValue()),
                                (oldValue, newValue) -> newValue,
                                LinkedHashMap::new
                        ));
                bigMinerals = mineralLines.keySet().stream()
                        .map(tag -> UniBotUtils.getUnitByTag(observation, tag))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(unit -> UniBotConstants.ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES.contains(unit.getType()))
                        .sorted(Comparator.comparing((Unit mineral) -> mineral.getMineralContents().orElse(0))
                                .thenComparing(mineral -> mineral.getPosition().toPoint2d().distance(observation.getStartLocation().toPoint2d())))
                        .collect(Collectors.toCollection(Stack::new));
                unavailableSCVs = mineralLines.entrySet().stream()
                        .flatMap(entry -> UniBotUtils.getUnitByTag(observation, entry.getKey())
                                .filter(unit -> UniBotConstants.ALL_BIG_NEUTRAL_MINERAL_FIELD_TYPES.contains(unit.getType())).stream().flatMap(unit -> entry.getValue().stream()))
                        .map(UnitInPool::unit)
                        .toList();
                for (List<UnitInPool> unitsList : mineralLines.values()) {
                    unitsList.clear();
                }
                tempFlag = false;
                return getAndRemoveFirstKey();
            }
        }
        return null;
    }

    private static boolean containsUnitByTag(List<UnitInPool> units, Tag tag) {
        return units.stream()
                .anyMatch(unit -> unit.getTag().equals(tag));
    }

    private static Unit getAndRemoveFirstKey() {
        if (currentData == null || currentData.isEmpty()) {
            return null;
        }
        Map.Entry<Unit, List<UnitInPool>> firstEntry = currentData.entrySet().iterator().next();
        Unit firstKey = firstEntry.getKey();
        currentData.remove(firstKey);
        return firstKey;
    }

    public static Unit findNearestMineralPatch() {
        return getAndRemoveFirstKey();
    }

    private static final double RADIUS_FOR_SEARCH_ON_WHOLE_MAP = 1000.0;
    public static Optional<Unit> findNearestMineralPatch(ObservationInterface observation, Point2d target, int limit) {
        return UniBotUtils.findNearestUnits(observation, target, UniBotConstants.ALL_NEUTRAL_MINERAL_FIELD_TYPES, Alliance.NEUTRAL, RADIUS_FOR_SEARCH_ON_WHOLE_MAP, limit, MineralLineOptimizer::isMineralCloseEnoughActiveBase).stream()
                .findFirst();
    }

    private static final float IS_CLOSE_ENOUGH_MINERAL = 10.0f;
    private static boolean isMineralCloseEnoughActiveBase(Unit mineral) {
        return GameMap.basesCoordinates.get(0).distance(mineral.getPosition().toPoint2d()) < IS_CLOSE_ENOUGH_MINERAL;
    }

    public static Unit findNearestMineralPatchForMule() {
        return bigMinerals.pop();
    }
}
