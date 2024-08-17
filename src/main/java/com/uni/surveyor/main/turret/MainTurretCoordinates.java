package com.uni.surveyor.main.turret;

import com.github.ocraft.s2client.protocol.spatial.Point2d;

public record MainTurretCoordinates(Point2d mineralLineTurretPosition,
                                    Point2d gasesTurretPosition,
                                    Point2d aroundTurretPosition) {
}
