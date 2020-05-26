package com.mokhov.climbing.utils;

import com.mokhov.climbing.enumerators.DistanceUnitsEnum;

public class DistanceCalculator {

    public static double distance(double lat1, double lon1, double lat2, double lon2, DistanceUnitsEnum unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            if (unit == DistanceUnitsEnum.DEGREES) return (double)Math.round(dist * 100000000000d) / 100000000000d;
            dist = dist * 60 * 1.1515;
            if (unit == DistanceUnitsEnum.KILOMETERS) {
                dist = dist * 1.609344;
            } else if (unit == DistanceUnitsEnum.NATURAL_MILES) {
                dist = dist * 0.8684;
            }
            return dist; //Miles
        }
    }

    private DistanceCalculator() {
        throw new IllegalStateException("Utility class");
    }

}

