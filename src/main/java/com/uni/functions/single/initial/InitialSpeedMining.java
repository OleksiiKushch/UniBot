package com.uni.functions.single.initial;

import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitialSpeedMining {

    private static final double RADIUS_FOR_MINERALS_SEARCHING_NEAR_BASE = 10.0f;
    private static final double DISTANCE_TOWARDS_MINERAL_AND_BASE = 1.325d; // radius

    public static final Map<Tag, Point2d> speedMiningPoints = new HashMap<>();
    public static final Map<Tag, Unit> patchesByTag = new HashMap<>();

    public static void calculate(ObservationInterface observation) {
        final Point2d startPosition = observation.getStartLocation().toPoint2d();
        List<UnitInPool> initPatch = findNearestBaseMineralPatches(observation, startPosition);
        calculateSpeedMining(initPatch, startPosition);
    }

    private static List<UnitInPool> findNearestBaseMineralPatches(ObservationInterface observation, Point2d startPosition) {
        List<UnitInPool> result = new ArrayList<>();
        List<UnitInPool> allPatch = new ArrayList<>();
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_MINERAL_FIELD450)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_MINERAL_FIELD750)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_RICH_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_RICH_MINERAL_FIELD750)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_MINERAL_FIELD_OPAQUE)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_MINERAL_FIELD_OPAQUE900)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_LAB_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_LAB_MINERAL_FIELD750)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_BATTLE_STATION_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_BATTLE_STATION_MINERAL_FIELD750)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_PURIFIER_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_PURIFIER_MINERAL_FIELD750)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_PURIFIER_RICH_MINERAL_FIELD)));
        allPatch.addAll(observation.getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(Units.NEUTRAL_PURIFIER_RICH_MINERAL_FIELD750)));
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

    private static Point2d towards(Point2d target1, Point2d target2) {
        Point2d ret = null;
        if (target1 != null && target2 != null) {
            float x = (float) ((double) (target2.getX() - target1.getX()) / (target1.distance(target2) / DISTANCE_TOWARDS_MINERAL_AND_BASE));
            float y = (float) ((double) (target2.getY() - target1.getY()) / (target1.distance(target2) / DISTANCE_TOWARDS_MINERAL_AND_BASE));
            ret = target1.add(Point2d.of(x, y));
        }
        return ret;
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
}
