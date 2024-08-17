package com.uni.surveyor.main;

import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.uni.surveyor.FieldType;
import com.uni.surveyor.FieldAnalizer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MainPositionAnalizer {

    public static Point2d findMostDistantPoint(int[][] matrix, Point2d base, Point2d antiRampGas, Point2d rampGas, Point2d ramp) {
        int centerX = matrix.length / 2;    // base internal coordinate
        int centerY = matrix[0].length / 2; // base internal coordinate
        Point2d antiRampGasInternal = FieldAnalizer.getInternalCoordinates(matrix.length, antiRampGas.getX(), antiRampGas.getY(), base.getX(), base.getY());
        Point2d rampGasInternal = FieldAnalizer.getInternalCoordinates(matrix.length, rampGas.getX(), rampGas.getY(), base.getX(), base.getY());
        Point2d rampInternal = FieldAnalizer.getInternalCoordinates(matrix.length, ramp.getX(), ramp.getY(), base.getX(), base.getY());

        Set<Point2d> points = getAllMainPoints(matrix, centerX, centerY);

        Point2d farthestPoint = points.stream()
                .max(Comparator.comparingDouble(point -> {
                    double distanceCenter = Math.pow(point.getX() - centerX, 2) + Math.pow(point.getY() - centerY, 2);
                    double distanceAntiRampGas = Math.pow(point.getX() - antiRampGasInternal.getX(), 2) + Math.pow(point.getY() - antiRampGasInternal.getY(), 2);
                    double distanceRampGas = Math.pow(point.getX() - rampGasInternal.getX(), 2) + Math.pow(point.getY() - rampGasInternal.getY(), 2);
                    double distanceRamp = Math.pow(point.getX() - rampInternal.getX(), 2) + Math.pow(point.getY() - rampInternal.getY(), 2);

                    return Math.min(distanceRampGas, Math.min(distanceRamp,  Math.min(distanceCenter, distanceAntiRampGas))); // TODO: check this implementation
                }))
                .orElse(null);

        return FieldAnalizer.getExternalCoordinates(matrix.length, (int) farthestPoint.getY(), (int) farthestPoint.getX(), base.getX(), base.getY());
    }

    private static Set<Point2d> getAllMainPoints(int[][] matrix, int centerX, int centerY) {
        Set<Point2d> points = new HashSet<>();

        Y(matrix, centerX, centerY, points, -1);
        Y(matrix, centerX, centerY, points, 1);

        X(matrix, centerX, centerY, points, -1);
        X(matrix, centerX, centerY, points, 1);

        return points;
    }

    private static void Y(int[][] matrix, int currentX, int currentY, Set<Point2d> points, int n) {
        while (matrix[currentY][currentX] == FieldType.PATHABLE_AND_PLACABLE.getValue() ||
                matrix[currentY][currentX] == FieldType.OTHERS.getValue()) {
            processY(matrix, currentX, currentY, points);
            currentX = currentX + n;
        }
    }

    private static void X(int[][] matrix, int currentX, int currentY, Set<Point2d> points, int n) {
        while (matrix[currentY][currentX] == FieldType.PATHABLE_AND_PLACABLE.getValue() ||
                matrix[currentY][currentX] == FieldType.OTHERS.getValue()) {
            processX(matrix, currentX, currentY, points);
            currentY = currentY + n;
        }
    }

    private static void processY(int[][] matrix, int currentX, int currentY, Set<Point2d> points) {
        subProcessY(matrix, currentX, currentY, points, -1);
        subProcessY(matrix, currentX, currentY, points, 1);
    }

    private static void processX(int[][] matrix, int currentX, int currentY, Set<Point2d> points) {
        subProcessX(matrix, currentX, currentY, points, -1);
        subProcessX(matrix, currentX, currentY, points, 1);
    }

    private static void subProcessY(int[][] matrix, int currentX, int currentY, Set<Point2d> points, int n) {
        while (matrix[currentY][currentX] == FieldType.PATHABLE_AND_PLACABLE.getValue() ||
                matrix[currentY][currentX] == FieldType.OTHERS.getValue()) {
            points.add(Point2d.of(currentX, currentY));
            currentY = currentY + n;
        }
    }

    private static void subProcessX(int[][] matrix, int currentX, int currentY, Set<Point2d> points, int n) {
        while (matrix[currentY][currentX] == FieldType.PATHABLE_AND_PLACABLE.getValue() ||
                matrix[currentY][currentX] == FieldType.OTHERS.getValue()) {
            points.add(Point2d.of(currentX, currentY));
            currentX = currentX + n;
        }
    }

}
