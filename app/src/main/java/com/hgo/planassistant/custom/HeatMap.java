package com.hgo.planassistant.custom;

import com.mapbox.mapboxsdk.style.expressions.Expression;

import static com.mapbox.mapboxsdk.style.expressions.Expression.heatmapDensity;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgba;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;

public class HeatMap {
    public static Expression[] initHeatmapColors(Expression[] listOfHeatmapColors) {
        listOfHeatmapColors = new Expression[] {
                // 0
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.25), rgba(224, 176, 63, 0.5),
                        literal(0.5), rgb(247, 252, 84),
                        literal(0.75), rgb(186, 59, 30),
                        literal(0.9), rgb(255, 0, 0)
                ),
                // 1
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(255, 255, 255, 0.4),
                        literal(0.25), rgba(4, 179, 183, 1.0),
                        literal(0.5), rgba(204, 211, 61, 1.0),
                        literal(0.75), rgba(252, 167, 55, 1.0),
                        literal(1), rgba(255, 78, 70, 1.0)
                ),
                // 2
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(12, 182, 253, 0.0),
                        literal(0.25), rgba(87, 17, 229, 0.5),
                        literal(0.5), rgba(255, 0, 0, 1.0),
                        literal(0.75), rgba(229, 134, 15, 0.5),
                        literal(1), rgba(230, 255, 55, 0.6)
                ),
                // 3
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(135, 255, 135, 0.2),
                        literal(0.5), rgba(255, 99, 0, 0.5),
                        literal(1), rgba(47, 21, 197, 0.2)
                ),
                // 4
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(4, 0, 0, 0.2),
                        literal(0.25), rgba(229, 12, 1, 1.0),
                        literal(0.30), rgba(244, 114, 1, 1.0),
                        literal(0.40), rgba(255, 205, 12, 1.0),
                        literal(0.50), rgba(255, 229, 121, 1.0),
                        literal(1), rgba(255, 253, 244, 1.0)
                ),
                // 5
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.05), rgba(0, 0, 0, 0.05),
                        literal(0.4), rgba(254, 142, 2, 0.7),
                        literal(0.5), rgba(255, 165, 5, 0.8),
                        literal(0.8), rgba(255, 187, 4, 0.9),
                        literal(0.95), rgba(255, 228, 173, 0.8),
                        literal(1), rgba(255, 253, 244, .8)
                ),
                //6
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.3), rgba(82, 72, 151, 0.4),
                        literal(0.4), rgba(138, 202, 160, 1.0),
                        literal(0.5), rgba(246, 139, 76, 0.9),
                        literal(0.9), rgba(252, 246, 182, 0.8),
                        literal(1), rgba(255, 255, 255, 0.8)
                ),

                //7
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 8
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 9
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 10
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 11
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.25),
                        literal(0.25), rgba(229, 12, 1, .7),
                        literal(0.30), rgba(244, 114, 1, .7),
                        literal(0.40), rgba(255, 205, 12, .7),
                        literal(0.50), rgba(255, 229, 121, .8),
                        literal(1), rgba(255, 253, 244, .8)
                )
        };
        return listOfHeatmapColors;
    }

    public static Expression[] initHeatmapRadiusStops(Expression[] listOfHeatmapRadiusStops) {
        listOfHeatmapRadiusStops = new Expression[] {
                // 0
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(30),
                        literal(6), literal(60)
                ),
//                interpolate(
//                        linear(), zoom(),
//                        literal(6), literal(50),
//                        literal(20), literal(100)
//                ),
                // 1
                interpolate(
                        linear(), zoom(),
                        literal(12), literal(70),
                        literal(20), literal(100)
                ),
                // 2
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 3
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 4
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 5
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(15), literal(200)
                ),
                // 6
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(70)
                ),
                // 7
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 8
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 9
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 10
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 11
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
        };
        return listOfHeatmapRadiusStops;
    }

    public static Float[] initHeatmapIntensityStops(Float[] listOfHeatmapIntensityStops) {
        listOfHeatmapIntensityStops = new Float[] {
                // 0
                0.06f,
                // 1
                0.3f,
                // 2
                1f,
                // 3
                1f,
                // 4
                1f,
                // 5
                1f,
                // 6
                1.5f,
                // 7
                0.8f,
                // 8
                0.25f,
                // 9
                0.8f,
                // 10
                0.25f,
                // 11
                0.5f
        };
        return listOfHeatmapIntensityStops;
    }
}
