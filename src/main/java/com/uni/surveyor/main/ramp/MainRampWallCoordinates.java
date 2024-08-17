package com.uni.surveyor.main.ramp;

import com.github.ocraft.s2client.protocol.spatial.Point2d;

public record MainRampWallCoordinates(Point2d firstSupplyPosition,
                                      Point2d secondSupplyPosition,
                                      Point2d barackPosition,
                                      Point2d barackDefaultPositionWithoutExt,
                                      Point2d factoryPosition,
                                      Point2d starPortPosition,
                                      Point2d firstDefaultBarackRallyPoint,
                                      Point2d secondDefaultBarackRallyPoint) {
}