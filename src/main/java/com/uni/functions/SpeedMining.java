package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;
import com.uni.utils.UniBotUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SpeedMining {

    private static final double RADIUS_FOR_MINERALS_SEARCHING_NEAR_BASE = 10.0f;
    private static final double DISTANCE_TOWARDS_MINERAL_AND_BASE = 1.325d; // radius

    private static final Map<Unit, Unit> initialLastSCVsTargets = new LinkedHashMap<>();
    private static final Map<Tag, Point2d> speedMiningPoints = new HashMap<>();
    private static final Map<Tag, Unit> patchesByTag = new HashMap<>();
    public static final Map<Tag, List<Unit>> mineralLines = new HashMap<>();  // <base, <mineral, number_of_SCVs>>
    public static boolean tempFlag = true;    // TODO: refactor it

    public static void calculateSpeedMining(ObservationInterface observation) {
        final Point2d startPosition = observation.getStartLocation().toPoint2d();
        List<UnitInPool> initPatch = findNearestBaseMineralPatches(observation, startPosition);
        calculateSpeedMining(initPatch, startPosition);
    }

    private static List<UnitInPool> findNearestBaseMineralPatches(ObservationInterface observation, Point2d startPosition) {
        List<UnitInPool> result = new ArrayList<>();
        List<UnitInPool> allPatch = new ArrayList<>();
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_MINERAL_FIELD750)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_RICH_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_RICH_MINERAL_FIELD750)));
        for (UnitInPool path : allPatch) {
            if (path.unit().getPosition().toPoint2d().distance(startPosition) < RADIUS_FOR_MINERALS_SEARCHING_NEAR_BASE) {
                result.add(path);
            }
        }
        return result;
    }

    private static void calculateSpeedMining(List<UnitInPool> patches, Point2d basePosition) {
        for (UnitInPool patch1 : patches) {
            Point2d toward = towards(patch1.unit().getPosition().toPoint2d(), basePosition);
            for (UnitInPool patch2 : patches) {
                if (patch1 != patch2 && patch2.unit().getPosition().toPoint2d().distance(toward) < DISTANCE_TOWARDS_MINERAL_AND_BASE &&
                        patch1.unit().getPosition().toPoint2d().distance(basePosition) > patch2.unit().getPosition().toPoint2d().distance(basePosition)) {
                    Point2d[] intersections = getIntersections(
                            patch1.unit().getPosition().toPoint2d().getX(),
                            patch1.unit().getPosition().toPoint2d().getY(),
                            patch2.unit().getPosition().toPoint2d().getX(),
                            patch2.unit().getPosition().toPoint2d().getY());
                    if (intersections != null) {
                        Point2d temp;
                        if (intersections[0].distance(basePosition) < intersections[1].distance(basePosition)) {
                            temp = intersections[0];
                        } else {
                            temp = intersections[1];
                        }
                        toward = temp;
                        break;
                    }
                }
            }
            speedMiningPoints.put(patch1.getTag(), toward);
            patchesByTag.put(patch1.getTag(), patch1.unit());
        }
    }

    private static Point2d[] getIntersections(double x0, double y0, double x1, double y1) {
        final double radius0 = DISTANCE_TOWARDS_MINERAL_AND_BASE;
        final double radius1 = DISTANCE_TOWARDS_MINERAL_AND_BASE;
        Point2d[] result = new Point2d[2];
        double diameter = Math.sqrt(Math.pow(x1 - x0, 2.0d) + Math.pow(y1 - y0, 2.0d));
        if (diameter > radius0 + radius1) {
            return null;
        } else if (diameter < Math.abs(radius0 - radius1)) {
            return null;
        } else if (diameter == 0.0d) {
            return null;
        } else {
            double a = (Math.pow(radius0, 2.0d) - Math.pow(radius1, 2.0d) + Math.pow(diameter, 2.0d)) / (2.0d * diameter);
            double h = Math.sqrt(Math.pow(radius0, 2.0d) - Math.pow(a, 2.0d));
            float x2 = (float) (x0 + a * (x1 - x0) / diameter);
            float y2 = (float) (y0 + a * (y1 - y0) / diameter);
            float x3 = (float) ((double) x2 + h * (y1 - y0) / diameter);
            float y3 = (float) ((double) y2 - h * (x1 - x0) / diameter);
            float x4 = (float) ((double) x2 - h * (y1 - y0) / diameter);
            float y4 = (float) ((double) y2 + h * (x1 - x0) / diameter);
            result[0] = Point2d.of(x3, y3);
            result[1] = Point2d.of(x4, y4);
            return result;
        }
    }

    public static void initialSCVsSplit(ObservationInterface observation, ActionInterface actions) {
        Collection<Unit> targetUnits = patchesByTag.values();
        Map<Unit, Unit> result = new LinkedHashMap<>();
        targetUnits.stream()
                .filter(mineral -> mineral.getType() == Units.NEUTRAL_MINERAL_FIELD750)
                .forEach(mineral ->  result.put(
                        UniBotUtils.findNearestUnits(observation, mineral.getPosition().toPoint2d(),
                                Set.of(Units.TERRAN_SCV), Alliance.SELF, 10.0d, 1, u -> !result.containsKey(u)).get(0), mineral));
        targetUnits.stream()
                .filter(mineral -> mineral.getType() == Units.NEUTRAL_MINERAL_FIELD)
                .forEach(mineral -> result.put(
                        UniBotUtils.findNearestUnits(observation, mineral.getPosition().toPoint2d(),
                                Set.of(Units.TERRAN_SCV), Alliance.SELF, 10.0d, 1, u -> !result.containsKey(u)).get(0), mineral));

        // optimization
        boolean test = true;
        while (test) {
            int i = 0;
            for (Map.Entry<Unit, Unit> entry : result.entrySet()) {
                Unit actualNearestMineral = UniBotUtils.findNearestUnits(observation, entry.getKey().getPosition().toPoint2d(), Set.of(Units.NEUTRAL_MINERAL_FIELD), Alliance.NEUTRAL, 10.0d, 1, u -> true).get(0);
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
                .filter(mineral -> mineral.getType() == Units.NEUTRAL_MINERAL_FIELD)
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

    public static void keepLastSCVs(ObservationInterface observation, ActionInterface actions) {
        if (20 < observation.getGameLoop() && observation.getGameLoop() < 50) {
            initialLastSCVsTargets.forEach((key, value) -> actions.unitCommand(key, Abilities.MOVE, speedMiningPoints.get(value.getTag()), false));
        }
        if (observation.getGameLoop() == 51) {
            initialLastSCVsTargets.forEach((key, value) -> actions.unitCommand(key, Abilities.HARVEST_GATHER, value, true));
        }
    }

    public static Unit findOptionalMineral(ObservationInterface observation, float progress) {
        System.out.println(mineralLines.size());
        if (progress < 0.97f) {
            tempFlag = true;
        }
        if (tempFlag) {
            if (progress < 0.7f) {
                return null;
            } else if (progress >= 0.7f && progress < 0.98f) {    // data collection
                Map<Tag, List<Unit>> tempResult = observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SCV)).stream()
                        .map(UnitInPool::unit)
                        .filter(unit -> unit.getOrders() != null && !unit.getOrders().isEmpty())
                        .filter(unit -> unit.getOrders().stream().anyMatch(order -> Abilities.HARVEST_GATHER.equals(order.getAbility())))
                        .collect(Collectors.groupingBy(unit -> unit.getOrders().stream()
                                        .filter(order -> Abilities.HARVEST_GATHER.equals(order.getAbility()))
                                        .findFirst()
                                        .flatMap(UnitOrder::getTargetedUnitTag)
                                        .orElseThrow(IllegalStateException::new),
                                Collectors.toList()));
                System.out.println("____________________________________________________________");
                tempResult.forEach((key, value) -> {
                    System.out.println("Mineral: " + key.getValue() + " SCVs: " + value.stream().map(Unit::getTag).map(Objects::toString).collect(Collectors.joining(", ", "{", "}")));
                });
                System.out.println("____________________________________________________________");
                tempResult.forEach((tag, units) -> {
                    // Merge lists of units by the common tag
                    mineralLines.merge(tag, units, (list1, list2) -> {
                        List<Unit> mergedList = new ArrayList<>(list1);
                        for (Unit unit : list2) {
                            if (!containsUnitByTag(mergedList, unit.getTag())) {
                                mergedList.add(unit);
                            }
                        }
                        return mergedList;
                    });
                });

                return null;
            } else {
                System.out.println("__________________________RESULT____________________________");
                mineralLines.forEach((key, value) -> {
                    System.out.println("Mineral: " + key.getValue() + " SCVs: " + value.stream().map(Unit::getTag).map(Objects::toString).collect(Collectors.joining(", ", "{", "}")));
                });
                Unit result = mineralLines.entrySet().stream()
                        .filter(entry -> entry.getValue().size() < 2)
                        .map(entry -> getUnitByTag(observation, entry.getKey()))
                        .min(Comparator.comparing((Unit u) -> u.getMineralContents().orElse(0))
                                .thenComparing(u -> u.getPosition().toPoint2d().distance(observation.getStartLocation().toPoint2d())))
                        .orElse(null);
                SpeedMining.mineralLines.clear();
                SpeedMining.tempFlag = false;
                return result;
            }
        }
        return null;
    }

    private static boolean containsUnitByTag(List<Unit> units, Tag tag) {
        for (Unit unit : units) {
            if (unit.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    private static Unit getUnitByTag(ObservationInterface observation, Tag tag) {
        return observation.getUnits(Alliance.NEUTRAL).stream()
                .map(UnitInPool::unit)
                .filter(unit -> unit.getTag().equals(tag))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public static void speedMiningWithSCV(ObservationInterface observation, ActionInterface actions) {
        Iterator<UnitInPool> units = observation.getUnits(Alliance.SELF, isHarvestReturnScv()).iterator();

        while (true) {
            UnitInPool scv;
            Optional<Unit> base;
            do {
                do {
                    do {
                        do {
                            do {
                                if (!units.hasNext()) {
                                    Iterator<UnitInPool> harvestGatherSCVs = observation.getUnits(Alliance.SELF, isHarvestGatherScv()).iterator();

                                    reset_label:
                                    while (harvestGatherSCVs.hasNext()) {
                                        UnitInPool harvestGatherScv = harvestGatherSCVs.next();
                                        Iterator<UnitOrder> harvestGatherScvOrders = harvestGatherScv.unit().getOrders().iterator();

                                        while (true) {
                                            UnitOrder harvestGatherScvOrder;
                                            do {
                                                if (!harvestGatherScvOrders.hasNext()) {
                                                    continue reset_label;
                                                }
                                                harvestGatherScvOrder = harvestGatherScvOrders.next();
                                            } while (harvestGatherScvOrder.getTargetedUnitTag().isEmpty());

                                            Point2d target = speedMiningPoints.get(harvestGatherScvOrder.getTargetedUnitTag().get());
                                            if (target != null) {
                                                Unit patch = patchesByTag.get(harvestGatherScvOrder.getTargetedUnitTag().get());
                                                if (patch != null && harvestGatherScv.unit().getOrders().size() == 1 &&
                                                        target.distance(harvestGatherScv.unit().getPosition().toPoint2d()) > 0.75d &&
                                                        target.distance(harvestGatherScv.unit().getPosition().toPoint2d()) < 1.5d) {
                                                    actions.unitCommand(harvestGatherScv.unit(), Abilities.MOVE, target, false);
                                                    actions.unitCommand(harvestGatherScv.unit(), Abilities.HARVEST_GATHER, patch, true);
                                                }
                                            }
                                        }
                                    }
                                    return;
                                }
                                scv = units.next();
                                base = UniBotUtils.findNearestBase(observation, scv.unit().getPosition().toPoint2d());
                            } while (scv.unit().getOrders().size() != 1);
                        } while (base.isEmpty());
                    } while (Float.compare((base.get()).getBuildProgress(), 1.0f) != 0);
                } while (!((base.get()).getPosition().toPoint2d().distance(scv.unit().getPosition().toPoint2d()) > 0.75d + (double) (base.get()).getRadius() + (double) scv.unit().getRadius()));
            } while (!((base.get()).getPosition().toPoint2d().distance(scv.unit().getPosition().toPoint2d()) < 1.5d + (double) (base.get()).getRadius() + (double) scv.unit().getRadius()));

            // return mineral
            if ((scv.unit().getOrders().get(0)).getTargetedUnitTag().isPresent() && !((scv.unit().getOrders().get(0)).getTargetedUnitTag().get()).equals((base.get()).getTag())) {
                // SCV is harvesting another base
            } else {
                double xDiff = (scv.unit().getPosition().toPoint2d().getX() - (base.get()).getPosition().toPoint2d().getX());
                double yDiff = (scv.unit().getPosition().toPoint2d().getY() - (base.get()).getPosition().toPoint2d().getY());
                double sqrt = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
                double x = (double) (base.get()).getPosition().toPoint2d().getX() + (double) ((base.get()).getRadius() + scv.unit().getRadius()) * (xDiff / sqrt);
                double y = (double) (base.get()).getPosition().toPoint2d().getY() + (double) ((base.get()).getRadius() + scv.unit().getRadius()) * (yDiff / sqrt);
                Point2d p = Point2d.of((float) x, (float) y);
                actions.unitCommand(scv.unit(), Abilities.MOVE, p, false);
                actions.unitCommand(scv.unit(), Abilities.SMART, (base.get()), true);
            }
        }
    }

    private static Predicate<UnitInPool> isHarvestReturnScv() {
        return (unitInPool) -> UnitInPool.isUnit(Units.TERRAN_SCV).test(unitInPool) &&
                Float.compare(unitInPool.unit().getBuildProgress(), 1.0f) == 0 &&
                UnitInPool.isCarryingVespene().negate().test(unitInPool) &&
                unitInPool.unit().getOrders().stream()
                        .anyMatch((unitOrder) -> Abilities.HARVEST_RETURN.equals(unitOrder.getAbility()));
    }

    private static Predicate<UnitInPool> isHarvestGatherScv() {
        return (unitInPool) -> UnitInPool.isUnit(Units.TERRAN_SCV).test(unitInPool) &&
                Float.compare(unitInPool.unit().getBuildProgress(), 1.0f) == 0 &&
                unitInPool.unit().getOrders().stream()
                        .anyMatch((unitOrder) -> Abilities.HARVEST_GATHER.equals(unitOrder.getAbility()));
    }

    private static Point2d towards(Point2d target1, Point2d target2) {
        Point2d ret = null;
        if (target1 != null && target2 != null) {
            float x = (float) ((double) (target2.getX() - target1.getX()) / (target1.distance(target2) / DISTANCE_TOWARDS_MINERAL_AND_BASE));
            float y = (float) ((double) (target2.getY() - target1.getY()) / (target1.distance(target2) / DISTANCE_TOWARDS_MINERAL_AND_BASE));
            ret = target1.add(Point2d.of(x, y));
        }
        return ret;
    }
}
