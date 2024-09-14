package com.uni.functions;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.github.ocraft.s2client.protocol.unit.UnitOrder;
import com.uni.functions.single.initial.InitialSpeedMining;
import com.uni.utils.UniBotPredicateUtils;
import com.uni.utils.UniBotUtils;

import java.util.Iterator;
import java.util.Optional;

public class MineralSCVsAccelerator {

    public static void speedMiningWithSCV(ObservationInterface observation, ActionInterface actions) {
        Iterator<UnitInPool> units = observation.getUnits(Alliance.SELF, UniBotPredicateUtils.isHarvestReturnScv()).iterator();

        while (true) {
            UnitInPool scv;
            Optional<Unit> base;
            do {
                do {
                    do {
                        do {
                            do {
                                if (!units.hasNext()) {
                                    Iterator<UnitInPool> harvestGatherSCVs = observation.getUnits(Alliance.SELF, UniBotPredicateUtils.isHarvestGatherScv()).iterator();

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

                                            Point2d target = InitialSpeedMining.speedMiningPoints.get(harvestGatherScvOrder.getTargetedUnitTag().get());
                                            if (target != null) {
                                                Unit patch = InitialSpeedMining.patchesByTag.get(harvestGatherScvOrder.getTargetedUnitTag().get());
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
}
