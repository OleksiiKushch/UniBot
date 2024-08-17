package com.uni.surveyor;

import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.spatial.Point2d;

public class FieldAnalizer {

    public static final int FOR_TERRAN_PRODUCTION_EXTENSION = 2;
    public static final int SIZE_3x3  = 3;

    public static int[][] scan(ObservationInterface observation, Point2d point, int size) {
        int[][] fieldMap = new int[size][size];

        int centerX = (int) point.getX();
        int centerY = (int) point.getY();

        int startX = centerX - fieldMap.length / 2;
        int startY = centerY - fieldMap.length / 2;

        for (int i = 0; i < fieldMap.length; i++) {
            for (int j = 0; j < fieldMap.length; j++) {
                Point2d tempPoint = Point2d.of(startX + i, startY + j);
                boolean pathable = observation.isPathable(tempPoint);
                boolean placable = observation.isPlacable(tempPoint);

                if (!pathable && !placable) {
                    fieldMap[i][j] = FieldType.NOT_PATHABLE_AND_NOT_PLACABLE.getValue();
                } else if (pathable && placable) {
                    fieldMap[i][j] = FieldType.PATHABLE_AND_PLACABLE.getValue();
                } else if (pathable) {
                    fieldMap[i][j] = FieldType.ONLY_PATHABLE.getValue();
                } else {
                    fieldMap[i][j] = FieldType.OTHERS.getValue();
                }
            }
        }

        return fieldMap;
    }

    public static Point2d getExternalCoordinates(int size, int internalY, int internalX, float externalCenterX, float externalCenterY) {
        int deltaX = internalY - size / 2;
        int deltaY = internalX - size / 2;

        float externalX = externalCenterX + (float) deltaX;
        float externalY = externalCenterY + (float) deltaY;

        return Point2d.of(externalX, externalY);
    }

    public static Point2d getInternalCoordinates(int size, float externalX, float externalY, float externalCenterX, float externalCenterY) {
        int deltaX = Math.round(externalX - externalCenterX);
        int deltaY = Math.round(externalY - externalCenterY);

        int internalX = size / 2 + deltaY;
        int internalY = size / 2 + deltaX;

        return Point2d.of(internalX, internalY);
    }

    // implement this method, or mark the corresponding structure on the map when calculating its coordinates (position)
    public static void markStructureOnMap(int[][] map, int y, int x, int size) {
    }
}
